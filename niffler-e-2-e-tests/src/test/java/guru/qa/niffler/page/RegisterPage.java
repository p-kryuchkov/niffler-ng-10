package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.visible;

public class RegisterPage {
    private final SelenideElement usernameInput;
    private final SelenideElement passwordInput;
    private final SelenideElement passwordSubmitInput;
    private final SelenideElement registerButton;
    private final SelenideElement signInButton;
    private final SelenideElement formError;
    private final SelenideDriver driver;

    public RegisterPage(SelenideDriver driver) {
        this.usernameInput = driver.$("#username");
        this.passwordInput =  driver.$("#password");
        this.passwordSubmitInput =  driver.$("#passwordSubmit");
        this.registerButton =  driver.$("#register-button");
        this.signInButton =  driver.$(".form_sign-in");
        this.formError =  driver.$(".form__error");
        this.driver = driver;
    }

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
        return new LoginPage(driver);
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
