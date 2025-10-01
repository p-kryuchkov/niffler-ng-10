package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.*;

public class AllPeoplePage {
    private final SelenideElement friendsTab = $("a[href='/people/friends']");
    private final SelenideElement searchInput = $("[aria-label=\"search\"]");
    private final ElementsCollection peopleTableRows = $$("#all tr");
    private final SelenideElement pageNextButton = $("#page-next");
    private final SelenideElement pagePreviousButton = $("#page-prev");

    private static final String addFriendButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Add friend']";

    public FriendsPage friendsTabSwitch() {
        friendsTab.click();
        return new FriendsPage();
    }

    public AllPeoplePage search(String searchValue) {
        searchInput.val(searchValue);
        return this;
    }

    public AllPeoplePage addFriend(String userName) {
        peopleTableRows.findBy(text(userName))
                .shouldBe(visible)
                .find(byXpath(addFriendButtonXpath))
                .click();
        return this;
    }

    public AllPeoplePage userExist(String userName) {
        peopleTableRows.findBy(text(userName))
                .shouldBe(visible);
        return this;
    }

    public AllPeoplePage userWaitingExists(String userName) {
        peopleTableRows.findBy(text(userName))
                .shouldHave(text("Waiting..."));
        return this;
    }

    public AllPeoplePage nextPage() {
        pageNextButton.click();
        return this;
    }

    public AllPeoplePage previousPage() {
        pagePreviousButton.click();
        return this;
    }
}
