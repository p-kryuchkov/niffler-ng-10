package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UserdataUserRepository udUserRepository = new UserdataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserJson createUser(String username, String password) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(username);
                    authUser.setPassword(pe.encode(password));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);
                    authUser.setAuthorities(Arrays.stream(Authority.values()).map(
                                    e -> {
                                        AuthorityEntity ae = new AuthorityEntity();
                                        ae.setUser(authUser);
                                        ae.setAuthority(e);
                                        return ae;
                                    }
                            ).toList()
                    );
                    authUserRepository.createUser(authUser);

                    UserEntity userdataUser = new UserEntity();
                    userdataUser.setUsername(username);
                    userdataUser.setFirstname(RandomDataUtils.randomName());
                    userdataUser.setSurname(RandomDataUtils.randomSurname());
                    userdataUser.setFullname(String.format("%s %s", userdataUser.getFirstname(), userdataUser.getSurname()));
                    userdataUser.setCurrency(CurrencyValues.RUB);

                    return UserJson.fromEntity(
                            udUserRepository.createUser(userdataUser),
                            null
                    );
                }
        );
    }

    @Override
    public UserJson updateUser(UserJson userJson) {
        return xaTransactionTemplate.execute(() -> {
            UserEntity userdataUser = UserEntity.fromJson(userJson);
            return UserJson.fromEntity(udUserRepository.updateUser(userdataUser), null);
        });
    }

    @Override
    public void deleteUser(UserJson userJson) {
        xaTransactionTemplate.execute(() -> {
            udUserRepository.delete(UserEntity.fromJson(userJson));
            authUserRepository.delete(authUserRepository.findByUsername(userJson.username()).get());
            return null;
        });
    }

    @Override
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        xaTransactionTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
                friend.friendshipStatus();
                udUserRepository.sendInvitation(UserEntity.fromJson(friend), UserEntity.fromJson(targetUser));
                result.add(friend);
            }
            return null;
        });
        return null;
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        xaTransactionTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
                friend.friendshipStatus();
                udUserRepository.sendInvitation(UserEntity.fromJson(friend), UserEntity.fromJson(targetUser));
                result.add(friend);
            }
            return null;
        });
        return null;
    }

    @Override
    public List<UserJson> createFriends(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        xaTransactionTemplate.execute(() -> {
            for (int i = 0; i < count; i++) {
                UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
                udUserRepository.addFriend(UserEntity.fromJson(targetUser), UserEntity.fromJson(friend));
                result.add(friend);
            }
            return null;
        });
        return null;
    }
}