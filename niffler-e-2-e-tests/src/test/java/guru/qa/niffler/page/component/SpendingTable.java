package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static guru.qa.niffler.condition.SpendConditions.spends;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendingTable extends BaseComponent<SpendingTable> {
    private final SearchField searchField = new SearchField(driver);
    private final SelenideElement periodSelector = self.$("#period");
    private final CurrencySelector currencySelector = new CurrencySelector(driver);
    private final SelenideElement deleteButton = self.$("#delete");
    private final AlertDialog alertDialog = new AlertDialog(driver);
    private final ElementsCollection spendingRows = self.$$("tr");
    private final ElementsCollection periodRows = self.$(":rb:").$$("li");

    public SpendingTable(SelenideDriver driver) {
        super(driver, driver.$("#spendings"));
    }

    public SpendingTable selectPeriod(DataFilterValues period) {
        periodSelector.shouldBe(visible).click();
        periodRows.findBy(text(period.name())).click();
        return this;
    }

    public EditSpendingPage editSpending(String description) {
        searchField.search(description);
        spendingRows.find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage(driver);
    }

    public SpendingTable deleteSpending(String description) {
        searchField.search(description);
        spendingRows.findBy(text(description)).click();
        deleteButton.click();
        alertDialog.submitDelete();
        return this;
    }

    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    public SpendingTable checkTableContains(String... expectedValues) {
        for (String description : expectedValues) {
            spendingRows.findBy(text(description)).should(visible);
        }
        return this;
    }

    public SpendingTable checkTableSize(int expectedSize) {
        assertEquals(expectedSize, spendingRows.size());
        return this;
    }

    public SpendingTable checkSpends(SpendJson... spendJsons) {
        spendingRows.should(spends(spendJsons));
        return this;
    }
}
