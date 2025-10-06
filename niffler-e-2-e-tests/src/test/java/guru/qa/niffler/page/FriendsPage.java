package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
    private final SelenideElement allPeopleTab = $("a[href='/people/all']");
    private final SelenideElement searchInput = $("[aria-label=\"search\"]");
    private final ElementsCollection friendsTableRows = $$("#friends tr");
    private final ElementsCollection requestsTableRows = $$("#requests tr");
    private final SelenideElement pageNextButton = $("#page-next");
    private final SelenideElement pagePreviousButton = $("#page-prev");

    private final String unfriendButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Add friend']";
    private final String acceptButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Accept']";
    private final String declineButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Decline']";

    public AllPeoplePage switchToAllPeopleTab() {
        allPeopleTab.click();
        return new AllPeoplePage();
    }

    public FriendsPage search(String searchValue) {
        searchInput.val(searchValue);
        return this;
    }

    public FriendsPage unfriend(String friendName) {
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(unfriendButtonXpath))
                .click();
        return this;
    }

    public FriendsPage acceptRequest(String friendName) {
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(acceptButtonXpath))
                .click();
        return this;
    }

    public FriendsPage declineRequest(String friendName) {
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible)
                .find(byXpath(declineButtonXpath))
                .click();
        return this;
    }

    public FriendsPage checkFriend(String friendName) {
        friendsTableRows.findBy(text(friendName))
                .shouldBe(visible);
        return this;
    }

    public FriendsPage checkFriendsEmpty() {
        friendsTableRows.first().shouldNot(exist);
        return this;
    }

    public FriendsPage checkRequest(String friendName) {
        requestsTableRows.findBy(text(friendName))
                .shouldBe(visible);
        return this;
    }

    public FriendsPage nextPage() {
        pageNextButton.click();
        return this;
    }

    public FriendsPage previousPage() {
        pagePreviousButton.click();
        return this;
    }
}
