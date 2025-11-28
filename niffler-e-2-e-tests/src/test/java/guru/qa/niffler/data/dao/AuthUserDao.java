package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthUserDao {
    @Nonnull AuthUserEntity createUser(@Nonnull AuthUserEntity user);

    @Nonnull  AuthUserEntity updateUser(@Nonnull AuthUserEntity user);

    @Nonnull Optional<AuthUserEntity> findById(@Nonnull UUID id);

    @Nonnull List<AuthUserEntity> findAll();

    @Nonnull Optional<AuthUserEntity> findByUsername(@Nonnull String username);

    void delete(@Nonnull AuthUserEntity user);
}
