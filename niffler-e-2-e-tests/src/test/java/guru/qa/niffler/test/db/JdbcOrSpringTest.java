package guru.qa.niffler.test.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.TransactionIsolationLevel;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserAuthEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class JdbcOrSpringTest {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserDao authUserSpringDao = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthoritySpringDao = new AuthAuthorityDaoSpringJdbc();
    private final UserDao udUserSpringDao = new UserDaoSpringJdbc();


    @Test
    public void jdbcWithoutTransactionTest() {
        UserAuthEntity userAuthEntity = new UserAuthEntity();

        userAuthEntity.setEnabled(true);
        userAuthEntity.setCredentialsNonExpired(true);
        userAuthEntity.setAccountNonLocked(true);
        userAuthEntity.setAccountNonExpired(true);
        userAuthEntity.setUsername(RandomDataUtils.randomUsername());
        userAuthEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(userAuthEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        try (Connection authConn = DataSources.dataSource(CFG.authJdbcUrl()).getConnection();
             Connection userConn = DataSources.dataSource(CFG.userdataJdbcUrl()).getConnection()) {
            userAuthEntity = new AuthUserDaoJdbc(authConn).createUser(userAuthEntity);
            System.out.println("Создали юзера в auth " + userAuthEntity.getUsername());
            userData = new UserDaoJdbc(userConn).createUser(userData);
            System.out.println("Создали юзера в userdata");
            authority.setUser(null);
            new AuthAuthorityDaoJdbc(authConn).create(authority);
            System.out.println("Создали authority в auth");
        } catch (Exception e) {
            try (Connection authConn = DataSources.dataSource(CFG.authJdbcUrl()).getConnection();
                 Connection userConn = DataSources.dataSource(CFG.userdataJdbcUrl()).getConnection()) {
                System.out.println("В базе auth лежит юзер " + new AuthUserDaoJdbc(authConn).findById(userAuthEntity.getId()).map(user -> {
                    return user.getUsername();
                }));
                System.out.println("В базе userdata лежит юзер " + new UserDaoJdbc(userConn).findById(userData.getId()).map(user -> {
                    return user.getUsername();
                }));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Test
    public void jdbcWithTransactionTest() {
        UserAuthEntity userAuthEntity = new UserAuthEntity();

        userAuthEntity.setEnabled(true);
        userAuthEntity.setCredentialsNonExpired(true);
        userAuthEntity.setAccountNonLocked(true);
        userAuthEntity.setAccountNonExpired(true);
        userAuthEntity.setUsername(RandomDataUtils.randomUsername());
        userAuthEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(userAuthEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);
        xaTransaction(TransactionIsolationLevel.REPEATABLE_READ, new Databases.XaFunction<>(connection -> {
                    userAuthEntity.setId(new AuthUserDaoJdbc(connection).createUser(userAuthEntity).getId());
                    System.out.println("Создали юзера в auth " + userAuthEntity.getUsername());
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    new UserDaoJdbc(connection).createUser(userData);
                    System.out.println("Создали Юзера в userdata");
                    return null;
                }, CFG.userdataJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    authority.setUser(userAuthEntity);
                    new AuthAuthorityDaoJdbc(connection).create(authority);
                    System.out.println("Создали authority в auth");
                    return null;
                }, "нет такого урл"));
    }

    @Test
    public void springWithoutTransactionTest() {
        UserAuthEntity userAuthEntity = new UserAuthEntity();

        userAuthEntity.setEnabled(true);
        userAuthEntity.setCredentialsNonExpired(true);
        userAuthEntity.setAccountNonLocked(true);
        userAuthEntity.setAccountNonExpired(true);
        userAuthEntity.setUsername(RandomDataUtils.randomUsername());
        userAuthEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(userAuthEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                CFG.authJdbcUrl(),
                CFG.userdataJdbcUrl());
        AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
        AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
        UserDao udUserDao = new UserDaoSpringJdbc();
        try {
            userAuthEntity = authUserDao.createUser(userAuthEntity);
            System.out.println("Создали юзера в auth " + userAuthEntity.getUsername());
            udUserDao.createUser(userData);
            System.out.println("Создали юзера в userdata");
            authAuthorityDao.create(authority);
            System.out.println("Создали authority в auth");
        } catch (Exception e) {
            System.out.println("В базе auth лежит юзер " + authUserDao.findById(userAuthEntity.getId()).map(user -> {
                return user.getUsername();
            }));
            System.out.println("В базе userdata лежит юзер " + udUserDao.findById(userData.getId()).map(user -> {
                return user.getUsername();
            }));
        }
    }

    @Test
    public void springWithTransactionTest() {
        UserAuthEntity userAuthEntity = new UserAuthEntity();

        userAuthEntity.setEnabled(true);
        userAuthEntity.setCredentialsNonExpired(true);
        userAuthEntity.setAccountNonLocked(true);
        userAuthEntity.setAccountNonExpired(true);
        userAuthEntity.setUsername(RandomDataUtils.randomUsername());
        userAuthEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(userAuthEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                CFG.authJdbcUrl(),
                CFG.userdataJdbcUrl());
        AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
        AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
        UserDao udUserDao = new UserDaoSpringJdbc();
        xaTransactionTemplate.execute(() -> {
            authUserDao.createUser(userAuthEntity);
            System.out.println("Создали юзера в auth " + userAuthEntity.getUsername());
            udUserDao.createUser(userData);
            System.out.println("Создали юзера в userdata");
            authAuthorityDao.create(authority);
            System.out.println("Создали authority в auth");
            return null;
        });
    }
}
