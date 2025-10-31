package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.mapper.FriendshipEntityRowMapper;
import guru.qa.niffler.data.mapper.UserEntityRowMapper;
import guru.qa.niffler.data.mapper.UserdataSetExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement userPs = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES (?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getCurrency().name());
            userPs.setString(3, user.getFirstname());
            userPs.setString(4, user.getSurname());
            userPs.setBytes(5, user.getPhoto());
            userPs.setBytes(6, user.getPhotoSmall());
            userPs.setString(7, user.getFullname());
            return userPs;
        }, kh);

        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        for (FriendshipEntity friendship : user.getFriendshipRequests()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO friendship (requester_id, addressee_id, status) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (requester_id, addressee_id) " +
                            "DO UPDATE SET status = ? ",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setObject(1, friendship.getRequester().getId());
                            ps.setObject(2, friendship.getAddressee().getId());
                            ps.setString(3, friendship.getStatus().name());
                            ps.setString(4, friendship.getStatus().name());
                        }

                        @Override
                        public int getBatchSize() {
                            return user.getFriendshipRequests().size();
                        }
                    }
            );
        }

        for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
            jdbcTemplate.batchUpdate(
                    "INSERT INTO friendship (requester_id, addressee_id, status) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (requester_id, addressee_id) " +
                            "DO UPDATE SET status = ? ",
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setObject(1, friendship.getRequester().getId());
                            ps.setObject(2, friendship.getAddressee().getId());
                            ps.setString(3, friendship.getStatus().name());
                            ps.setString(4, friendship.getStatus().name());
                        }

                        @Override
                        public int getBatchSize() {
                            return user.getFriendshipAddressees().size();
                        }
                    }
            );
        }
        return user;
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT * FROM \"user\" u " +
                                "JOIN friendship fr " +
                                "ON u.id = fr.requester_id " +
                                "JOIN friendship fa " +
                                "ON u.id = fa.addressee_id " +
                                "WHERE u.id = ?",
                        UserdataSetExtractor.instance,
                        id));
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT * FROM \"user\" u " +
                                "JOIN friendship fr " +
                                "ON u.id = fr.requester_id " +
                                "JOIN friendship fa " +
                                "ON u.id = fa.addressee_id " +
                                "WHERE u.username = ?",
                        UserdataSetExtractor.instance,
                        username));
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipRow(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        createFriendshipRow(requester, addressee, FriendshipStatus.PENDING);
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        createFriendshipRow(requester, addressee, FriendshipStatus.ACCEPTED);
        createFriendshipRow(addressee, requester, FriendshipStatus.ACCEPTED);
    }

    @Override
    public List<UserEntity> findAll() {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM \"user\"",
                UserEntityRowMapper.instance
        );
    }

    @Override
    public void delete(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement userPs = con.prepareStatement(
                    "DELETE FROM \"user\" WHERE id = ?");
            PreparedStatement friendshipPs = con.prepareStatement(
                    "DELETE FROM friendship WHERE requester_id = ? " +
                            "OR addressee_id  = ?");
            userPs.setObject(1, user.getId());
            friendshipPs.setObject(1, user.getId());
            friendshipPs.setObject(2, user.getId());
            friendshipPs.executeUpdate();
            return userPs;
        });
    }

    public List<FriendshipEntity> getFriendshipRequests(UserEntity user) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE requester_id = ?",
                FriendshipEntityRowMapper.instance
        );
    }

    public List<FriendshipEntity> getFriendshipAddressee(UserEntity user) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return jdbcTemplate.query(
                "SELECT * FROM friendship WHERE addressee = ?",
                FriendshipEntityRowMapper.instance
        );
    }

    private void createFriendshipRow(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        jdbcTemplate.update(con -> {
            PreparedStatement friendshipPs = con.prepareStatement(
                    "INSERT INTO friendship (requester_id, addressee_id, status) " +
                            "VALUES (?, ?, ?) " +
                            "ON CONFLICT (requester_id, addressee_id) " +
                            "DO UPDATE SET status = ? "
            );
            friendshipPs.setObject(1, requester.getId());
            friendshipPs.setObject(2, addressee.getId());
            friendshipPs.setString(3, status.name());
            friendshipPs.setString(4, status.name());
            return friendshipPs;
        });
    }
}
