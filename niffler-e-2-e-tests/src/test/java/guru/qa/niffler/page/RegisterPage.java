package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.ownText;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement registerButton = $("#register-button");
    private final SelenideElement signInButton = $(".form_sign-in");
    private final SelenideElement formError = $(".form__error");


    public RegisterPage setUsername(String username) {
        usernameInput.val(username);
        return this;
    }

    public RegisterPage setPassword(String password) {
        passwordInput.val(password);
        return this;
    }

    public RegisterPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput.val(passwordSubmit);
        return this;
    }

    public RegisterPage register() {
        registerButton.click();
        return this;
    }

    public LoginPage signIn() {
        signInButton.click();
        return new LoginPage();
    }

    public RegisterPage checkFormError(String errorText) {
        formError.shouldHave(ownText(errorText));
        return this;
    }

    public RegisterPage checkElements() {
        usernameInput.shouldBe(visible);
        passwordInput.shouldBe(visible);
        passwordSubmitInput.shouldBe(visible);
        registerButton.shouldBe(visible);
        return this;
    }
}
