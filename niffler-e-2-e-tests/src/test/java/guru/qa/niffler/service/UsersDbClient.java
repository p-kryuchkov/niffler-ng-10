package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import jaxb.userdata.FriendshipStatus;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
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
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.createUser(authUser);
                    UserEntity friend = udUserRepository.createUser(userEntity(username));
                    udUserRepository.sendInvitation(targetEntity, friend);
                    result.add(UserJson.fromEntity(friend, FriendshipStatus.INVITE_RECEIVED));
                    return null;
                });
            }
        }
        return result;
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();
        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                    String username = randomUsername();
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.createUser(authUser);
                    UserEntity friend = udUserRepository.createUser(userEntity(username));
                    udUserRepository.sendInvitation(friend, targetEntity);
                    result.add(UserJson.fromEntity(friend, FriendshipStatus.INVITE_SENT));
                    return null;
                });
            }
        }
        return result;
    }

    @Override
    public List<UserJson> createFriends(UserJson targetUser, int count) {
        List<UserJson> result = new ArrayList<>();

        if (count > 0) {
            UserEntity targetEntity = udUserRepository.findById(
                    targetUser.id()
            ).orElseThrow();
            for (int i = 0; i < count; i++) {
                xaTransactionTemplate.execute(() -> {
                            String username = randomUsername();
                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.createUser(authUser);
                            UserEntity friend = udUserRepository.createUser(userEntity(username));
                            udUserRepository.addFriend(targetEntity, friend);
                            result.add(UserJson.fromEntity(friend, FriendshipStatus.FRIEND));
                            return null;
                        }
                );
            }
        }
        return result;
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
}