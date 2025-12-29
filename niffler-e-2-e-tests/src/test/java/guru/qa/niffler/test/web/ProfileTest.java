package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.service.UsersApiClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

@ExtendWith({BrowserExtension.class})
public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver selenideDriver = new SelenideDriver(SelenideUtils.chromeConfig);

    UsersClient usersClient = new UsersApiClient();

    @User
    @Test
    public void changeInfoTest(UserJson user) {
        browserExtension.drivers().add(selenideDriver);

        String name = "ИмяТест";

        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .editProfile()
                .setName(name)
                .clickSaveChanges()
                .checkSnackbarText("Profile successfully updated")
                .goToMainPage()
                .editProfile()
                .checkNameInputValue(name);
    }
}