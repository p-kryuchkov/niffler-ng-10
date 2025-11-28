package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.user.UserEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {
    @Nonnull
    UserEntity createUser(@Nonnull UserEntity user);

    @Nonnull
    UserEntity updateUser(@Nonnull UserEntity user);

    @Nonnull
    Optional<UserEntity> findById(@Nonnull UUID id);

    @Nonnull
    Optional<UserEntity> findByUsername(@Nonnull String username);

    void sendInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee);

    void addFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee);

    @Nonnull
    List<UserEntity> findAll();

    void delete(@Nonnull UserEntity user);
}