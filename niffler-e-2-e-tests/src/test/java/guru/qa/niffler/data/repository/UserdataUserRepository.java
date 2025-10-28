package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.user.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserdataUserRepository {
    default UserEntity create(UserEntity user) {
        return null;
    }

    Optional<UserEntity> findById(UUID id);

    Optional<UserEntity> findByUsername(String username);

    void addIncomeInvitation(UserEntity requester, UserEntity addressee);

    void addOutcomeInvitation(UserEntity requester, UserEntity addressee);

    void addFriend(UserEntity requester, UserEntity addressee);
}