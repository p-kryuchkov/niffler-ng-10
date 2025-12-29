package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.visible;

public class LoginPage {
    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement submitBtn;
    private final SelenideElement registerButton;
    private final SelenideElement formError;
    private final SelenideDriver driver;

    public LoginPage(SelenideDriver driver) {
        this.usernameInput = driver.$("#username");
        this.passwordInput = driver.$("#password");
        this.submitBtn = driver.$("#login-button");
        this.registerButton = driver.$("#register-button");
        this.formError = driver.$(".form__error");
        this.driver = driver;
    }

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
        return new MainPage(driver);
    }

    @Step("Open registration page")
    public RegisterPage registerNewUser() {
        registerButton.click();
        return new RegisterPage(driver);
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
