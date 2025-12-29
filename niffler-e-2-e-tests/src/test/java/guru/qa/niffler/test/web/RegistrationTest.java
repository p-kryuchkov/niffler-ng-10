package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.Random;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {
    private static final Config CFG = Config.getInstance();

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver selenideDriver = new SelenideDriver(SelenideUtils.chromeConfig);

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        browserExtension.drivers().add(selenideDriver);
        final String username = "testRegisterUser" + new Random().nextInt(999);
        final String password = String.valueOf(new Random().nextInt(100000, 1000000));

        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .registerNewUser()
                .checkElements()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .register()
                .signIn()
                .checkElements()
                .login(username, password)
                .checkElements();
    }

    @Test
    @DisplayName("Ошибка регистрации существующего пользователя")
    void shouldNotRegisterUserWithExistingUsername() {
        browserExtension.drivers().add(selenideDriver);

        final String username = "Olduser";
        final String password = "54321";

        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .registerNewUser()
                .checkElements()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit(password)
                .register()
                .checkFormError(String.format("Username `%s` already exists", username));
    }

    @Test
    @DisplayName("Ошибка при несовпадении пароля и подтверждения")
    void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
        browserExtension.drivers().add(selenideDriver);

        final String username = "testRegisterUser" + new Random().nextInt(999);
        final String password = String.valueOf(new Random().nextInt(100000, 1000000));

        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .registerNewUser()
                .checkElements()
                .setUsername(username)
                .setPassword(password)
                .setPasswordSubmit("notValidPassword")
                .register()
                .checkFormError("Passwords should be equal");
    }

    @Test
    @DisplayName("Успешный логин существующего пользователя")
    void mainPageShouldBeDisplayedAfterSuccessLogin() {
        browserExtension.drivers().add(selenideDriver);

        final String username = "TestDefaultUser";
        final String password = "12345";


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .checkElements()
                .login(username, password)
                .checkElements();
    }

    @Test
    @DisplayName("Ошибка при логине с некорректными данными")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        browserExtension.drivers().add(selenideDriver);

        final String username = "Testuser";

        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .setUsername(username)
                .setPassword("WrongPassword")
                .submit()
                .checkFormError("Неверные учетные данные пользователя")
                .checkElements();
    }
}
