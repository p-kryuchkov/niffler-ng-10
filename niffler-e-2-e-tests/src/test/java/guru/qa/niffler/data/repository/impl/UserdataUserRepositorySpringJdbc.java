package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.FriendshipDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.FriendshipDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoSpringJdbc;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.FriendshipStatus;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {
    private static final Config CFG = Config.getInstance();

    private static final UserDao userDao = new UserDaoSpringJdbc();
    private static final FriendshipDao friendshipDao = new FriendshipDaoSpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.userdataJdbcUrl()
    );

    @Override
    public @Nonnull UserEntity createUser(@Nonnull UserEntity user) {
        return xaTransactionTemplate.execute(() -> {
            UserEntity result = userDao.createUser(user);
            for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
                friendshipDao.createFriendship(friendship);
            }
            for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                friendshipDao.createFriendship(friendship);
            }
            return result;
        });
    }

    @Override
    public @Nonnull UserEntity updateUser(@Nonnull UserEntity user) {
        return userDao.updateUser(user);
    }

    @Override
    public @Nonnull Optional<UserEntity> findById(@Nonnull UUID id) {
        return userDao.findById(id).map(userEntity -> {
            userEntity.setFriendshipAddressees(friendshipDao.findByAddresseeId(id));
            userEntity.setFriendshipRequests(friendshipDao.findByRequesterId(id));
            return userEntity;
        });
    }


    @Override
    public @Nonnull Optional<UserEntity> findByUsername(@Nonnull String username) {
        return userDao.findByUsername(username).map(userEntity -> {
            userEntity.setFriendshipAddressees(friendshipDao.findByAddresseeId(userEntity.getId()));
            userEntity.setFriendshipRequests(friendshipDao.findByRequesterId(userEntity.getId()));
            return userEntity;
        });
    }

    @Override
    public @Nonnull void sendInvitation(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setAddressee(addressee);
        friendship.setRequester(requester);
        friendship.setStatus(FriendshipStatus.PENDING);
        friendshipDao.createFriendship(friendship);
    }


    @Override
    public void addFriend(@Nonnull UserEntity requester, @Nonnull UserEntity addressee) {
        xaTransactionTemplate.execute(() -> {
            FriendshipEntity friendshipIncome = new FriendshipEntity();
            friendshipIncome.setAddressee(addressee);
            friendshipIncome.setRequester(requester);
            friendshipIncome.setStatus(FriendshipStatus.ACCEPTED);

            FriendshipEntity friendshipOutcome = new FriendshipEntity();
            friendshipOutcome.setAddressee(requester);
            friendshipOutcome.setRequester(addressee);
            friendshipOutcome.setStatus(FriendshipStatus.ACCEPTED);

            friendshipDao.createFriendship(friendshipIncome);
            friendshipDao.createFriendship(friendshipOutcome);
            return null;
        });
    }

    @Override
    public @Nonnull List<UserEntity> findAll() {
        return userDao.findAll();
    }

    @Override
    public void delete(@Nonnull UserEntity user) {
        xaTransactionTemplate.execute(() -> {
                    for (FriendshipEntity friendship : user.getFriendshipAddressees()) {
                        friendshipDao.deleteFriendship(friendship);
                    }
                    for (FriendshipEntity friendship : user.getFriendshipRequests()) {
                        friendshipDao.deleteFriendship(friendship);
                    }
                    userDao.delete(user);
                    return null;
                }
        );
    }
}
