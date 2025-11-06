package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserAuthEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.impl.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;

import guru.qa.niffler.model.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryJdbc();
    private final UserDao udUserDao = new UserDaoSpringJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    @Override
    public UserJson createUser(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    UserAuthEntity authUser = new UserAuthEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
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
                    return UserJson.fromEntity(
                            udUserDao.createUser(UserEntity.fromJson(user)),
                            null
                    );
                }
        );
    }
}