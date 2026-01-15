package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.AlertDialog;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage extends BasePage<FriendsPage> {
    public static final String URL = CFG.frontUrl() + "people/friends";

    private final SelenideElement allPeopleTab = $("a[href='/people/all']");
    private final SearchField searchInput = new SearchField();
    private final ElementsCollection friendsTableRows = $$("#friends tr");
    private final ElementsCollection requestsTableRows = $$("#requests tr");
    private final SelenideElement pageNextButton = $("#page-next");
    private final SelenideElement pagePreviousButton = $("#page-prev");
    private final AlertDialog alertDialog = new AlertDialog();
    private final String unfriendButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Unfriend']";
    private final String acceptButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Accept']";
    private final String declineButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Decline']";

    @Step("Switch to 'All People' tab")
    public AllPeoplePage switchToAllPeopleTab() {
        allPeopleTab.click();
        return new AllPeoplePage();
    }

    @Step("Search for: {searchValue}")
    public FriendsPage search(String searchValue) {
        searchInput.search(searchValue);
        return this;
    }

    @Step("Unfriend user: {friendName}")
    public FriendsPage unfriend(String friendName) {
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(unfriendButtonXpath))
                .click();
        alertDialog.submitDelete();
        return this;
    }

    @Step("Accept friend request from: {friendName}")
    public FriendsPage acceptRequest(String friendName) {
        search(friendName);
        requestsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(acceptButtonXpath))
                .click();
        return this;
    }

    @Step("Decline friend request from: {friendName}")
    public FriendsPage declineRequest(String friendName) {
        search(friendName);
        requestsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(declineButtonXpath))
                .click();
        alertDialog.submitDecline();
        return this;
    }

    @Step("Check that user '{friendName}' is in friends list")
    public FriendsPage checkFriend(String friendName) {
        search(friendName);
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible);
        return this;
    }

    @Step("Check that friends list is empty")
    public FriendsPage checkFriendsEmpty() {
        friendsTableRows.first().shouldNot(exist);
        return this;
    }

    @Step("Check that request from '{friendName}' exists")
    public FriendsPage checkRequest(String friendName) {
        searchInput.search(friendName);
        requestsTableRows.findBy(text(friendName))
                .shouldBe(visible);
        return this;
    }

    @Step("Go to next page")
    public FriendsPage nextPage() {
        pageNextButton.click();
        return this;
    }

    @Step("Go to previous page")
    public FriendsPage previousPage() {
        pagePreviousButton.click();
        return this;
    }
}