package guru.qa.niffler.data.mapper;

import guru.qa.niffler.data.entity.auth.UserAuthEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserAuthEntityRowMapper implements RowMapper<UserAuthEntity> {

    public static final UserAuthEntityRowMapper instance = new UserAuthEntityRowMapper();

    private UserAuthEntityRowMapper() {
    }

    @Override
    public UserAuthEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        UserAuthEntity result = new UserAuthEntity();
        result.setId(rs.getObject("id", UUID.class));
        result.setUsername(rs.getString("username"));
        result.setPassword(rs.getString("password"));
        result.setEnabled(rs.getBoolean("enabled"));
        result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
        result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
        result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
        return result;
    }
}

