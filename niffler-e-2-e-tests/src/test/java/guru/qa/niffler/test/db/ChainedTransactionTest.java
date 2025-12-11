package guru.qa.niffler.test.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
@Disabled
public class ChainedTransactionTest {
    private static final Config CFG = Config.getInstance();

    @Test
    public void successTransactionTest() {
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
                new AuthUserDaoJdbc().createUser(authUserEntity);
                System.out.println("Создали юзера в auth " + authUserEntity.getUsername());
                new UserDaoJdbc().createUser(userData);
                System.out.println("Создали юзера в userdata");
                authority.setUser(authUserEntity);
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
                new AuthUserDaoSpringJdbc().createUser(authUserEntity);
                System.out.println("Создали юзера в auth " + authUserEntity.getUsername());
                new UserDaoSpringJdbc().createUser(userData);
                System.out.println("Создали юзера в userdata");
                authority.setUser(new AuthUserEntity());
                new AuthAuthorityDaoSpringJdbc().create(authority);
                System.out.println("Создали authority в auth");
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
