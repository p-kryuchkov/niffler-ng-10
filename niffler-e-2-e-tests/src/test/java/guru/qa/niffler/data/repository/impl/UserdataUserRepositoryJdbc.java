package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        try (PreparedStatement userPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (currency, firstname, full_name, photo, photo_small, surname, username) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "INSERT INTO friendship (requester_id, addressee_id, status) " +
                             "VALUES (?, ?, ?) " +
                             "ON CONFLICT (requester_id, addressee_id) " +
                             "DO UPDATE SET status = ? "
             )) {
            userPs.setString(1, user.getCurrency().name());
            userPs.setString(2, user.getFirstname());
            userPs.setString(3, user.getFullname());
            userPs.setBytes(4, user.getPhoto());
            userPs.setBytes(5, user.getPhotoSmall());
            userPs.setString(6, user.getSurname());
            userPs.setString(7, user.getUsername());

            userPs.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = userPs.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);

            for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                friendshipPs.setObject(1, friendship.getRequester().getId());
                friendshipPs.setObject(2, friendship.getAddressee().getId());
                friendshipPs.setString(3, friendship.getStatus().name());
                friendshipPs.setString(4, friendship.getStatus().name());
                friendshipPs.addBatch();
                friendshipPs.clearParameters();
            }

            for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
                friendshipPs.setObject(1, friendship.getAddressee().getId());
                friendshipPs.setObject(2, friendship.getRequester().getId());
                friendshipPs.setString(3, friendship.getStatus().name());
                friendshipPs.setString(4, friendship.getStatus().name());
                friendshipPs.addBatch();
                friendshipPs.clearParameters();
            }

            friendshipPs.executeBatch();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findById(UUID id) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(getUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<UserEntity> findByUsername(String username) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setObject(1, username);
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    return Optional.of(getUser(rs));
                } else {
                    return Optional.empty();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

    private void createFriendshipRow(UserEntity requester, UserEntity addressee, FriendshipStatus status) {
        try (PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status) " +
                        "VALUES (?, ?, ?) " +
                        "ON CONFLICT (requester_id, addressee_id) " +
                        "DO UPDATE SET status = ? "
        )) {
            friendshipPs.setObject(1, requester.getId());
            friendshipPs.setObject(2, addressee.getId());
            friendshipPs.setString(3, status.name());
            friendshipPs.setString(4, status.name());
            friendshipPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<UserEntity> findAll() {
        List<UserEntity> users = new ArrayList<>();

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(

                "SELECT * FROM \"user\""
        )) {
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    users.add(getUser(rs));
                }
                return users;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE id = ?");
             PreparedStatement friendshipPs = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                     "DELETE FROM friendship WHERE requester_id = ? " +
                             "OR addressee_id  = ?");
        ) {
            ps.setObject(1, user.getId());
            ps.executeUpdate();
            friendshipPs.setObject(1, user.getId());
            friendshipPs.setObject(2, user.getId());
            friendshipPs.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public List<FriendshipEntity> getFriendshipRequests(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" " +
                        "JOIN friendship " +
                        "ON id = requester_id " +
                        "WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return getFriendships(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<FriendshipEntity> getFriendshipAddressee(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" " +
                        "JOIN friendship " +
                        "ON id = addressee_id " +
                        "WHERE id = ?"
        )) {
            ps.setObject(1, user.getId());
            ps.execute();
            try (ResultSet rs = ps.getResultSet()) {
                return getFriendships(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private UserEntity getUser(ResultSet rs) throws SQLException {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(rs.getObject("id", UUID.class));
        userEntity.setUsername(rs.getString("username"));
        userEntity.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
        userEntity.setFirstname(rs.getString("firstname"));
        userEntity.setFullname(rs.getString("full_name"));
        userEntity.setPhoto(rs.getBytes("photo"));
        userEntity.setPhotoSmall(rs.getBytes("photo_small"));
        userEntity.setSurname(rs.getString("surname"));
        return userEntity;
    }


    private FriendshipEntity getFriendship(ResultSet rs) throws SQLException {
        FriendshipEntity friendship = new FriendshipEntity();

        UUID addresseeId = rs.getObject("addressee_id", UUID.class);
        UUID requesterId = rs.getObject("requester_id", UUID.class);

        friendship.setAddressee(findById(addresseeId).orElse(null));
        friendship.setRequester(findById(requesterId).orElse(null));
        friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));
        return friendship;
    }

    private List<FriendshipEntity> getFriendships(ResultSet rs) throws SQLException {
        List<FriendshipEntity> friendshipEntities = new ArrayList<>();
        while (rs.next()) friendshipEntities.add(getFriendship(rs));
        return friendshipEntities;
    }
}
