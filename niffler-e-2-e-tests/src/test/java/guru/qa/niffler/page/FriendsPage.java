package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.AlertDialog;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;

public class FriendsPage extends BasePage<FriendsPage> {
    private final SelenideElement allPeopleTab;
    private final SearchField searchInput;
    private final ElementsCollection friendsTableRows;
    private final ElementsCollection requestsTableRows;
    private final SelenideElement pageNextButton;
    private final SelenideElement pagePreviousButton;
    private final AlertDialog alertDialog;
    private final String unfriendButtonXpath;
    private final String acceptButtonXpath;
    private final String declineButtonXpath;
    private final SelenideDriver driver;

    public FriendsPage(SelenideDriver driver) {
        super(driver);
        this.driver = driver;
        this.allPeopleTab = driver.$("a[href='/people/all']");
        this.searchInput = new SearchField(driver);
        this.friendsTableRows = driver.$$("#friends tr");
        this.requestsTableRows = driver.$$("#requests tr");
        this.pageNextButton = driver.$("#page-next");
        this.pagePreviousButton = driver.$("#page-prev");
        this.alertDialog = new AlertDialog(driver);
        this.unfriendButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Unfriend']";
        this.acceptButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Accept']";
        this.declineButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Decline']";
    }

    @Step("Switch to 'All People' tab")
    public AllPeoplePage switchToAllPeopleTab() {
        allPeopleTab.click();
        return new AllPeoplePage(driver);
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