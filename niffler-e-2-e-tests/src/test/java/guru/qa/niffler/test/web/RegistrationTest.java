package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.google.protobuf.StringValue;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Random;

@ExtendWith(BrowserExtension.class)
public class RegistrationTest {
    private static final Config CFG = Config.getInstance();

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void shouldRegisterNewUser() {
        final String username = "testRegisterUser" + new Random().nextInt(999);
        final String password = String.valueOf(new Random().nextInt(100000, 1000000));

        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        final String username = "Olduser";
        final String password = "54321";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        final String username = "testRegisterUser" + new Random().nextInt(999);
        final String password = String.valueOf(new Random().nextInt(100000, 1000000));

        Selenide.open(CFG.frontUrl(), LoginPage.class)
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
        final String username = "TestDefaultUser";
        final String password = "12345";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .checkElements()
                .login(username, password)
                .checkElements();
    }

    @Test
    @DisplayName("Ошибка при логине с некорректными данными")
    void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
        final String username = "Testuser";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .setUsername(username)
                .setPassword("WrongPassword")
                .submit()
                .checkFormError("Неверные учетные данные пользователя")
                .checkElements();
    }
}
