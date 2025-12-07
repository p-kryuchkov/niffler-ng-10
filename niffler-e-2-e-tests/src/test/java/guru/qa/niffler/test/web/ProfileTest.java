package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.service.UsersApiClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({BrowserExtension.class})
public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    UsersClient usersClient = new UsersApiClient();

    @User
    @Test
    public void changeInfoTest(UserJson user) {

        String name = "ИмяТест";
        Selenide.open(CFG.frontUrl(), LoginPage.class)
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