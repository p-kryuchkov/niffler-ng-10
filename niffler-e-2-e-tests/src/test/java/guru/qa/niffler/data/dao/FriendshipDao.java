package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.user.FriendshipEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

public interface FriendshipDao {
    void createFriendship(@Nonnull FriendshipEntity friendship);

    void deleteFriendship(@Nonnull FriendshipEntity friendship);

    @Nonnull List<FriendshipEntity> findByRequesterId(@Nonnull UUID requesterId);

    @Nonnull List<FriendshipEntity> findByAddresseeId(@Nonnull UUID addresseeId);
}
