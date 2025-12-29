package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.model.CurrencyValues;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;

public class CurrencySelector extends BaseComponent<CurrencySelector> {

    private final ElementsCollection currencyRows = driver.$("#menu-currency").$$("li");

    public CurrencySelector(SelenideDriver driver) {
        super(driver, driver.$("#currency"));
    }

    public CurrencySelector changeCurrency(CurrencyValues currencyValues) {
        self.click();
        currencyRows.findBy(text(currencyValues.name())).shouldBe(visible).click();
        return this;
    }
}