package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.UserAuthEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {
    UserAuthEntity createUser(UserAuthEntity user);

    UserAuthEntity updateUser(UserAuthEntity user);

    Optional<UserAuthEntity> findById(UUID id);

    Optional<UserAuthEntity> findByUsername(String username);

    void delete(UserAuthEntity user);
}
