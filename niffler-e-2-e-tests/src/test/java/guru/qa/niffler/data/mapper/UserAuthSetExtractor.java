package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserAuthEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserAuthSetExtractor implements ResultSetExtractor<UserAuthEntity> {
    public static final UserAuthSetExtractor instance = new UserAuthSetExtractor();

    private UserAuthSetExtractor() {
    }

    @Override
    public UserAuthEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<UUID, UserAuthEntity> users = new ConcurrentHashMap<>();
        UserAuthEntity user = null;
        while (rs.next()) {
            UUID userId = rs.getObject("id", UUID.class);

            user = users.computeIfAbsent(userId, id -> {
                UserAuthEntity result = new UserAuthEntity();
                try {
                    result.setId(userId);
                    result.setUsername(rs.getString("username"));
                    result.setPassword(rs.getString("password"));
                    result.setEnabled(rs.getBoolean("enabled"));
                    result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
                    result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
                    result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
                    users.put(userId, result);
                    return result;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            AuthorityEntity authority = new AuthorityEntity();
            authority.setId(rs.getObject("authority_id", UUID.class));
            authority.setAuthority(Authority.valueOf(rs.getString("authority")));
            user.addAuthorities(authority);
        }
        return user;
    }
}
