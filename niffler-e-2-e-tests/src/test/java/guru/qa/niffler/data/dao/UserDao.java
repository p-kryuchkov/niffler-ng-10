package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.user.UserEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDao {
    @Nonnull UserEntity createUser(@Nonnull UserEntity user);

    @Nonnull UserEntity updateUser(@Nonnull UserEntity user);

    @Nonnull Optional<UserEntity> findById(@Nonnull UUID id);

    @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username);

    @Nonnull List<UserEntity> findAll();

    void delete(UserEntity user);
}
