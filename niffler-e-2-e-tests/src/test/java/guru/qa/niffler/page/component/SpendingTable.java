package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.page.EditSpendingPage;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendingTable {
    private final SelenideElement self = $("#spendings");
    private final SelenideElement searchField = self.$("[aria-label='search']");
    private final SelenideElement periodSelector = self.$("#period");
    private final SelenideElement currencySelector = self.$("#currency");
    private final SelenideElement deleteButton = self.$("#delete");
    private final ElementsCollection spendingRows = self.$$("tr");
    private final ElementsCollection periodRows = self.$(":rb:").$$("li");
    private final ElementsCollection currencyRows = self.$(":rd:").$$("li");


    public SpendingTable selectPeriod(DataFilterValues period) {
        periodSelector.shouldBe(visible).click();
        periodRows.findBy(text(period.name())).click();
        return this;
    }

    public EditSpendingPage editSpending(String description) {
        spendingRows.findBy(text(description)).$("[aria-label=Edit spending]").click();
        return new EditSpendingPage();
    }

    public SpendingTable deleteSpending(String description) {
        spendingRows.findBy(text(description)).click();
        deleteButton.click();
        return this;
    }

    public SpendingTable searchSpendingByDescription(String description) {
        searchField.val(description).sendKeys(Keys.ENTER);
        return this;
    }

    public SpendingTable checkTableContains(String... expectedSpends) {
        for (String description : expectedSpends) {
            spendingRows.findBy(text(description)).should(visible);
        }
        return this;
    }

    public SpendingTable checkTableSize(int expectedSize) {
        assertEquals(expectedSize, spendingRows.size());
        return this;
    }
}
