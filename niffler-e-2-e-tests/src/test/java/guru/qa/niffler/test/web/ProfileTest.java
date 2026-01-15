package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.service.UsersApiClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({BrowserExtension.class})
public class ProfileTest {
    private static final Config CFG = Config.getInstance();
    UsersClient usersClient = new UsersApiClient();

    @User
    @ApiLogin
    @Test
    public void changeInfoTest() {

        String name = "ИмяТест";
        ProfilePage profilePage = Selenide.open(ProfilePage.URL, ProfilePage.class)
                .setName(name)
                .clickSaveChanges()
                .checkSnackbarText("Profile successfully updated")
                .goToMainPage()
                .editProfile()
                .checkNameInputValue(name);
    }
}