package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class RegisterPage {
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement registerButton = $("#register-button");
    private final SelenideElement signInButton = $(".form_sign-in");


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

    public LoginPage submitRegistration() {
        registerButton.click();
        signInButton.click();
        return new LoginPage();
    }
}
