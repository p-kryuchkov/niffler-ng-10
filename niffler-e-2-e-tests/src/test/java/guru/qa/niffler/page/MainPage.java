package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {
    private final SelenideElement spendings = $("#spendings");
    private final SelenideElement statistics = $("#stat");
    private final SearchField searchInput = new SearchField();
    private final Header header = new Header();
    private final SpendingTable spendingTable = new SpendingTable();

    @Step("Open spending editor for spending with description: {description}")
    public EditSpendingPage editSpending(String description) {
        return spendingTable.editSpending(description);
    }

    @Step("Verify that the spending table contains values: {args}")
    public MainPage checkThatTableContains(String... args) {
        spendingTable.checkTableContains(args);
        return this;
    }

    @Step("Delete Spending: {description}")
    public MainPage deleteSpending(String description) {
        spendingTable.deleteSpending(description);
        return this;
    }

    @Step("Check that main page elements are visible")
    public MainPage checkElements() {
        statistics.should(visible);
        spendings.shouldBe(visible);
        return this;
    }

    @Step("Open profile page")
    public ProfilePage editProfile() {
        return header.toProfilePage();
    }

    @Step("Open 'All People' page")
    public AllPeoplePage goToAllPeoplePage() {
        return header.toAllPeoplesPage();
    }

    @Step("Open friends page")
    public FriendsPage goToFriendsPage() {
        return header.toFriendsPage();
    }

    @Step("Open 'Add spending' page")
    public EditSpendingPage addSpending() {
        return header.addSpendingPage();
    }

    @Step("Waiting spending diagram load")
    public MainPage waitingSpendingDiagramLoad() {
        spendingTable.waitLoadingDiagram();
        return this;
    }
}
