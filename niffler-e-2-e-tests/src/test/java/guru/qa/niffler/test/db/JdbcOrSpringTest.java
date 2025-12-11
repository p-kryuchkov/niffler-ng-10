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
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.sql.SQLException;

import static guru.qa.niffler.data.Databases.xaTransaction;

@Disabled
public class JdbcOrSpringTest {
    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    public void jdbcWithoutTransactionTest() {
        AuthUserEntity authUserEntity = new AuthUserEntity();

        authUserEntity.setEnabled(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setUsername(RandomDataUtils.randomUsername());
        authUserEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(authUserEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        try (Connection authConn = DataSources.dataSource(CFG.authJdbcUrl()).getConnection();
             Connection userConn = DataSources.dataSource(CFG.userdataJdbcUrl()).getConnection()) {
            authUserEntity = new AuthUserDaoJdbc().createUser(authUserEntity);
            System.out.println("Создали юзера в auth " + authUserEntity.getUsername());
            userData = new UserDaoJdbc().createUser(userData);
            System.out.println("Создали юзера в userdata");
            authority.setUser(null);
            new AuthAuthorityDaoJdbc().create(authority);
            System.out.println("Создали authority в auth");
        } catch (Exception e) {
            try (Connection authConn = DataSources.dataSource(CFG.authJdbcUrl()).getConnection();
                 Connection userConn = DataSources.dataSource(CFG.userdataJdbcUrl()).getConnection()) {
                System.out.println("В базе auth лежит юзер " + new AuthUserDaoJdbc().findById(authUserEntity.getId()).map(user -> {
                    return user.getUsername();
                }));
                System.out.println("В базе userdata лежит юзер " + new UserDaoJdbc().findById(userData.getId()).map(user -> {
                    return user.getUsername();
                }));
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Test
    public void jdbcWithTransactionTest() {
        AuthUserEntity authUserEntity = new AuthUserEntity();

        authUserEntity.setEnabled(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setUsername(RandomDataUtils.randomUsername());
        authUserEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(authUserEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);
        xaTransaction(TransactionIsolationLevel.REPEATABLE_READ, new Databases.XaFunction<>(connection -> {
                    authUserEntity.setId(new AuthUserDaoJdbc().createUser(authUserEntity).getId());
                    System.out.println("Создали юзера в auth " + authUserEntity.getUsername());
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    new UserDaoJdbc().createUser(userData);
                    System.out.println("Создали Юзера в userdata");
                    return null;
                }, CFG.userdataJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    authority.setUser(authUserEntity);
                    new AuthAuthorityDaoJdbc().create(authority);
                    System.out.println("Создали authority в auth");
                    return null;
                }, "нет такого урл"));
    }

    @Test
    public void springWithoutTransactionTest() {
        AuthUserEntity authUserEntity = new AuthUserEntity();

        authUserEntity.setEnabled(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setUsername(RandomDataUtils.randomUsername());
        authUserEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(authUserEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                CFG.authJdbcUrl(),
                CFG.userdataJdbcUrl());
        AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
        AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
        UserDao udUserDao = new UserDaoSpringJdbc();
        try {
            authUserEntity = authUserDao.createUser(authUserEntity);
            System.out.println("Создали юзера в auth " + authUserEntity.getUsername());
            udUserDao.createUser(userData);
            System.out.println("Создали юзера в userdata");
            authAuthorityDao.create(authority);
            System.out.println("Создали authority в auth");
        } catch (Exception e) {
            System.out.println("В базе auth лежит юзер " + authUserDao.findById(authUserEntity.getId()).map(user -> {
                return user.getUsername();
            }));
            System.out.println("В базе userdata лежит юзер " + udUserDao.findById(userData.getId()).map(user -> {
                return user.getUsername();
            }));
        }
    }

    @Test
    public void springWithTransactionTest() {
        AuthUserEntity authUserEntity = new AuthUserEntity();

        authUserEntity.setEnabled(true);
        authUserEntity.setCredentialsNonExpired(true);
        authUserEntity.setAccountNonLocked(true);
        authUserEntity.setAccountNonExpired(true);
        authUserEntity.setUsername(RandomDataUtils.randomUsername());
        authUserEntity.setPassword("12345");

        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(Authority.read);

        UserEntity userData = new UserEntity();
        userData.setFullname("Testovii Test Testovitch");
        userData.setFirstname("Test");
        userData.setSurname("Testovii");
        userData.setUsername(authUserEntity.getUsername());
        userData.setCurrency(CurrencyValues.RUB);

        XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
                CFG.authJdbcUrl(),
                CFG.userdataJdbcUrl());
        AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
        AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();
        UserDao udUserDao = new UserDaoSpringJdbc();
        xaTransactionTemplate.execute(() -> {
            authUserDao.createUser(authUserEntity);
            System.out.println("Создали юзера в auth " + authUserEntity.getUsername());
            udUserDao.createUser(userData);
            System.out.println("Создали юзера в userdata");
            authAuthorityDao.create(authority);
            System.out.println("Создали authority в auth");
            return null;
        });
    }
}
