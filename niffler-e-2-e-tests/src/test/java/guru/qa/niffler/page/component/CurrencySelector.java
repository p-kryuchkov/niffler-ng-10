package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CurrencySelector {
    private final SelenideElement selector = $("#currency");
    private final ElementsCollection currencyRows = $("#menu-currency").$$("li");

    public CurrencySelector changeCurrency(CurrencyValues currencyValues) {
        selector.shouldBe(visible).click();
        currencyRows.findBy(text(currencyValues.name())).shouldBe(visible).click();
        return this;
    }
}
