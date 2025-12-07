package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith({BrowserExtension.class})
public class FriendsTest {
    private static final Config CFG = Config.getInstance();

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkFriend(user.testData().friends().getFirst().username());
    }

    @User()
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkFriendsEmpty();
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkRequest(user.testData().incomeInvitations().getFirst().username());
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToAllPeoplePage()
                .checkUserWaiting(user.testData().outcomeInvitations().getFirst().username());
    }

    @User(incomeInvitations = 1)
    @Test
    void acceptInvitationTest(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .acceptRequest(user.testData().incomeInvitations().getFirst().username())
                .checkSnackbarText(String.format("Invitation of %s", user.testData().incomeInvitations().getFirst().username()))
                .checkFriend(user.testData().incomeInvitations().getFirst().username());
    }

    @User(incomeInvitations = 1)
    @Test
    void declineInvitationTest(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .declineRequest(user.testData().incomeInvitations().getFirst().username())
                .checkSnackbarText(String.format("Invitation of %s is declined", user.testData().incomeInvitations().getFirst().username()))
                .checkFriendsEmpty();
    }

    @User(friends = 1)
    @Test
    void deleteFriendTest(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .unfriend(user.testData().friends().getFirst().username())
                .checkSnackbarText(String.format("Friend %s is deleted", user.testData().friends().getFirst().username()))
                .checkFriendsEmpty();
    }

    @User()
    @Test
    void createInvitationTest(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .goToAllPeoplePage()
                .addFriend("TestDefaultUser")
                .checkSnackbarText("Invitation sent to TestDefaultUser");
    }
}