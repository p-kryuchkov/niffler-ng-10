package guru.qa.niffler.test.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserAuthEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;

public class ChainedTransactionTest {
    private static final Config CFG = Config.getInstance();

    @Test
    public void successTransactionTest() {
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
        DataSourceTransactionManager authManager = new DataSourceTransactionManager(
                DataSources.dataSource(CFG.authJdbcUrl())
        );
        DataSourceTransactionManager userManager = new DataSourceTransactionManager(
                DataSources.dataSource(CFG.userdataJdbcUrl())
        );
        ChainedTransactionManager chainedTxManager = new ChainedTransactionManager(authManager, userManager);
        TransactionTemplate txTemplate = new TransactionTemplate(chainedTxManager);

        txTemplate.execute(status -> {
            try (Connection authConn = authManager.getDataSource().getConnection();
                 Connection userConn = userManager.getDataSource().getConnection()) {
                new AuthUserDaoJdbc().createUser(userAuthEntity);
                System.out.println("Создали юзера в auth " + userAuthEntity.getUsername());
                new UserDaoJdbc().createUser(userData);
                System.out.println("Создали юзера в userdata");
                authority.setUser(userAuthEntity);
                new AuthAuthorityDaoJdbc().create(authority);
                System.out.println("Создали authority в auth");
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    public void failTransactionTest() {
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
        DataSourceTransactionManager authManager = new DataSourceTransactionManager(
                DataSources.dataSource(CFG.authJdbcUrl())
        );
        DataSourceTransactionManager userManager = new DataSourceTransactionManager(
                DataSources.dataSource(CFG.userdataJdbcUrl())
        );
        ChainedTransactionManager chainedTxManager = new ChainedTransactionManager(authManager, userManager);
        TransactionTemplate txTemplate = new TransactionTemplate(chainedTxManager);

        txTemplate.execute(status -> {
            try (Connection authConn = authManager.getDataSource().getConnection();
                 Connection userConn = userManager.getDataSource().getConnection()) {
                new AuthUserDaoSpringJdbc().createUser(userAuthEntity);
                System.out.println("Создали юзера в auth " + userAuthEntity.getUsername());
                new UserDaoSpringJdbc().createUser(userData);
                System.out.println("Создали юзера в userdata");
                authority.setUser(new UserAuthEntity());
                new AuthAuthorityDaoSpringJdbc().create(authority);
                System.out.println("Создали authority в auth");
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
