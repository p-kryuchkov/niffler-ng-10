package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    protected static final Config CFG = Config.getInstance();
    public static final String URL = CFG.authUrl() + "register";

    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement registerButton = $("#register-button");
    private final SelenideElement signInButton = $(".form_sign-in");
    private final SelenideElement formError = $(".form__error");

    @Step("Set username: {username}")
    public RegisterPage setUsername(String username) {
        usernameInput.val(username);
        return this;
    }

    @Step("Set password")
    public RegisterPage setPassword(String password) {
        passwordInput.val(password);
        return this;
    }

    @Step("Confirm password")
    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput.val(passwordSubmit);
        return this;
    }

    @Step("Click 'Register' button")
    public RegisterPage register() {
        registerButton.click();
        return this;
    }

    @Step("Click 'Sign in' and go to Login page")
    public LoginPage signIn() {
        signInButton.click();
        return new LoginPage();
    }

    @Step("Check form error message: {errorText}")
    public RegisterPage checkFormError(String errorText) {
        formError.shouldHave(ownText(errorText));
        return this;
    }

    @Step("Check that all registration form elements are visible")
    public RegisterPage checkElements() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        passwordSubmitInput.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }

}
