package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.page.EditSpendingPage;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendingTable extends BaseComponent<SpendingTable> {
    private final SearchField searchField = new SearchField();
    private final SelenideElement periodSelector = self.$("#period");
    private final CurrencySelector currencySelector = new CurrencySelector();
    private final SelenideElement deleteButton = self.$("#delete");
    private final SelenideElement diagram = $("canvas[role='img']");
    private final AlertDialog alertDialog = new AlertDialog();
    private final ElementsCollection spendingRows = self.$$("tr");
    private final ElementsCollection periodRows = self.$(":rb:").$$("li");

    public SpendingTable() {
        super($("#spendings"));
    }

    public SpendingTable selectPeriod(DataFilterValues period) {
        periodSelector.shouldBe(visible).click();
        periodRows.findBy(text(period.name())).click();
        return this;
    }

    public EditSpendingPage editSpending(String description) {
        searchField.search(description);
        spendingRows.find(text(description)).$$("td").get(5).click();
        return new EditSpendingPage();
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

    public SpendingTable waitLoadingDiagram(){
        try {
            Thread.sleep(4000); // енашел метода проверки заагрузки canvas, чат гпт предлагает какоето трехэтажное решение через JSН
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        diagram.shouldBe(visible);
        return this;
    }
}
