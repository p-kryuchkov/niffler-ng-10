package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith({BrowserExtension.class})
public class FriendsTest {
    private static final Config CFG = Config.getInstance();

    @User(friends = 1)
    @Test
    @ApiLogin
    void friendShouldBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkFriend(user.testData().friends().getFirst().username());
    }

    @User()
    @Test
    @ApiLogin
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkFriendsEmpty();
    }

    @User(incomeInvitations = 1)
    @Test
    @ApiLogin
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkRequest(user.testData().incomeInvitations().getFirst().username());
    }

    @User(outcomeInvitations = 1)
    @Test
    @ApiLogin
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        Selenide.open(AllPeoplePage.URL, AllPeoplePage.class)
                .checkUserWaiting(user.testData().outcomeInvitations().getFirst().username());
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void acceptInvitationTest(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .acceptRequest(user.testData().incomeInvitations().getFirst().username())
                .checkSnackbarText(String.format("Invitation of %s", user.testData().incomeInvitations().getFirst().username()))
                .checkFriend(user.testData().incomeInvitations().getFirst().username());
    }

    @User(incomeInvitations = 1)
    @ApiLogin
    @Test
    void declineInvitationTest(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .declineRequest(user.testData().incomeInvitations().getFirst().username())
                .checkSnackbarText(String.format("Invitation of %s is declined", user.testData().incomeInvitations().getFirst().username()))
                .checkFriendsEmpty();
    }

    @User(friends = 1)
    @ApiLogin
    @Test
    void deleteFriendTest(UserJson user) {
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .unfriend(user.testData().friends().getFirst().username())
                .checkSnackbarText(String.format("Friend %s is deleted", user.testData().friends().getFirst().username()))
                .checkFriendsEmpty();
    }

    @User()
    @ApiLogin
    @Test
    void createInvitationTest(UserJson user) {
        Selenide.open(AllPeoplePage.URL, AllPeoplePage.class)
                .addFriend("TestDefaultUser")
                .checkSnackbarText("Invitation sent to TestDefaultUser");
    }
}