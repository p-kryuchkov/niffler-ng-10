package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.UserAuthEntity;

import java.util.Optional;
import java.util.UUID;

public interface AuthUserRepository {
    UserAuthEntity createUser(UserAuthEntity user);

    Optional<UserAuthEntity> findById(UUID id);

    Optional<UserAuthEntity> findByUsername(String username);
}