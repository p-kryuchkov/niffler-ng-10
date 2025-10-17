package guru.qa.niffler.test.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.TransactionIsolationLevel;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDaoJdbc;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserAuthEntity;
import guru.qa.niffler.data.entity.user.UserEntity;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class TransactionTest {
    private static final Config CFG = Config.getInstance();

    @Test
    public void succesTransactionTest() {
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
                    authority.setUser(userAuthEntity);
                    new AuthAuthorityDaoJdbc(connection).create(authority);
                    System.out.println("Создали authority в auth");
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    new UserDaoJdbc(connection).createUser(userData);
                    System.out.println("Создали Юзера в userdata");
                    return null;
                }, CFG.userdataJdbcUrl()));
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

        xaTransaction(TransactionIsolationLevel.REPEATABLE_READ, new Databases.XaFunction<>(connection -> {
                    userAuthEntity.setId(new AuthUserDaoJdbc(connection).createUser(userAuthEntity).getId());
                    System.out.println("Создали юзера в auth " + userAuthEntity.getUsername());
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    authority.setUser(userAuthEntity);
                    new AuthAuthorityDaoJdbc(connection).create(authority);
                    System.out.println("Создали authority в auth");
                    return null;
                }, CFG.authJdbcUrl()),
                new Databases.XaFunction<>(connection -> {
                    new UserDaoJdbc(connection).createUser(userData);
                    System.out.println("Создали Юзера в userdata");
                    return null;
                }, "нет такого урла"));
    }
}
