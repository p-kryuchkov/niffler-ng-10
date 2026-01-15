package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byXpath;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage extends BasePage <AllPeoplePage>{
    public static final String URL = CFG.frontUrl() + "people/all";

    private final SelenideElement friendsTab = $("a[href='/people/friends']");
    private final SearchField searchInput = new SearchField();
    private final ElementsCollection peopleTableRows = $$("#all tr");
    private final SelenideElement pageNextButton = $("#page-next");
    private final SelenideElement pagePreviousButton = $("#page-prev");

    private static final String addFriendButtonXpath = ".//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Add friend']";

    public FriendsPage switchToFriendsTab() {
        friendsTab.click();
        return new FriendsPage();
    }

    @Step("Search value {searchValue}")
    public AllPeoplePage search(String searchValue) {
        searchInput.search(searchValue);
        return this;
    }

    @Step("Add friend {username}")
    public AllPeoplePage addFriend(String userName) {
        search(userName);
        peopleTableRows.findBy(text(userName))
                .shouldBe(visible)
                .find(byXpath(addFriendButtonXpath))
                .click();
        return this;
    }

    public AllPeoplePage checkUser(String userName) {
        peopleTableRows.findBy(text(userName))
                .shouldBe(visible);
        return this;
    }

    @Step("Check outcome invitation {userName}")
    public AllPeoplePage checkUserWaiting(String userName) {
        search(userName);
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
