package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import guru.qa.niffler.model.CurrencyValues;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CurrencySelector extends BaseComponent<CurrencySelector> {

    private final ElementsCollection currencyRows = $("#menu-currency").$$("li");

    public CurrencySelector() {
        super($("#currency"));
    }

    public CurrencySelector changeCurrency(CurrencyValues currencyValues) {
        self.click();
        currencyRows.findBy(text(currencyValues.name())).shouldBe(visible).click();
        return this;
    }
}