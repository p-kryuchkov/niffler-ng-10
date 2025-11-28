package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement submitBtn = $("#login-button");
    private final SelenideElement registerButton = $("#register-button");
    private final SelenideElement formError = $(".form__error");

    @Step("Submit login form")
    public LoginPage submit() {
        submitBtn.click();
        return this;
    }

    @Step("Set username: {username}")
    public LoginPage setUsername(String username) {
        usernameInput.val(username);
        return this;
    }

    @Step("Set password: {password}")
    public LoginPage setPassword(String password) {
        passwordInput.val(password);
        return this;
    }

    @Step("Login with username: {username} and password: {password}")
    public MainPage login(String username, String password) {
        usernameInput.val(username);
        passwordInput.val(password);
        submitBtn.click();
        return new MainPage();
    }

    @Step("Open registration page")
    public RegisterPage registerNewUser() {
        registerButton.click();
        return new RegisterPage();
    }

    @Step("Check that all login form elements are visible")
    public LoginPage checkElements() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        submitBtn.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }

    @Step("Check form error message: {errorText}")
    public LoginPage checkFormError(String errorText) {
        formError.shouldHave(ownText(errorText));
        return this;
    }

}
