package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.StaticBrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;


@ExtendWith({StaticBrowserExtension.class})
public class FriendsTest {
    private static final Config CFG = Config.getInstance();
    @RegisterExtension
    private final StaticBrowserExtension staticBrowserExtension = new StaticBrowserExtension();
    private final SelenideDriver selenideDriver = new SelenideDriver(SelenideUtils.chromeConfig);

    @User(friends = 1)
    @Test
    void friendShouldBePresentInFriendsTable(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkFriend(user.testData().friends().getFirst().username());
    }

    @User()
    @Test
    void friendsTableShouldBeEmptyForNewUser(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkFriendsEmpty();
    }

    @User(incomeInvitations = 1)
    @Test
    void incomeInvitationBePresentInFriendsTable(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .checkRequest(user.testData().incomeInvitations().getFirst().username());
    }

    @User(outcomeInvitations = 1)
    @Test
    void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToAllPeoplePage()
                .checkUserWaiting(user.testData().outcomeInvitations().getFirst().username());
    }

    @User(incomeInvitations = 1)
    @Test
    void acceptInvitationTest(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .acceptRequest(user.testData().incomeInvitations().getFirst().username())
                .checkSnackbarText(String.format("Invitation of %s", user.testData().incomeInvitations().getFirst().username()))
                .checkFriend(user.testData().incomeInvitations().getFirst().username());
    }

    @User(incomeInvitations = 1)
    @Test
    void declineInvitationTest(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .declineRequest(user.testData().incomeInvitations().getFirst().username())
                .checkSnackbarText(String.format("Invitation of %s is declined", user.testData().incomeInvitations().getFirst().username()))
                .checkFriendsEmpty();
    }

    @User(friends = 1)
    @Test
    void deleteFriendTest(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToFriendsPage()
                .unfriend(user.testData().friends().getFirst().username())
                .checkSnackbarText(String.format("Friend %s is deleted", user.testData().friends().getFirst().username()))
                .checkFriendsEmpty();
    }

    @User()
    @Test
    void createInvitationTest(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .goToAllPeoplePage()
                .addFriend("TestDefaultUser")
                .checkSnackbarText("Invitation sent to TestDefaultUser");
    }
}