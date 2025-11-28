package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private final ElementsCollection tableRows = $$("#spendings tr");
    private final SelenideElement spendings = $("#spendings");
    private final SelenideElement statistics = $("#stat");
    private final SearchField searchInput = new SearchField();
    private final Header header = new Header();
    private final SpendingTable spendingTable = new SpendingTable();

    public EditSpendingPage editSpending(String description) {
        searchInput.clearIfNotEmpty().search(description);
        tableRows.find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
    }

    public MainPage checkThatTableContains(String... args) {
        spendingTable.checkTableContains(args);
        return this;
    }

    public MainPage checkElements() {
        statistics.should(visible);
        spendings.shouldBe(visible);
        return this;
    }

    public ProfilePage editProfile() {
        return header.toProfilePage();
    }

    public AllPeoplePage goToAllPeoplePage() {
        return header.toAllPeoplesPage();
    }

    public FriendsPage goToFriendsPage() {
        return header.toFriendsPage();
    }

    public EditSpendingPage addSpending() {
        return header.addSpendingPage();
    }


}
