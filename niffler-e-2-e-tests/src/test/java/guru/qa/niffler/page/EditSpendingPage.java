package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.CurrencySelector;
import io.qameta.allure.Step;

import java.util.Date;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage extends BasePage <EditSpendingPage>{
    private final SelenideElement descriptionInput = $("#description");
    private final SelenideElement amountInput = $("#amount");
    private final SelenideElement category = $("#category");
    private final SelenideElement calendarButton = $("[alt='Calendar']");
    private final Calendar calendar = new Calendar($("div.MuiDateCalendar-root"));
    private final CurrencySelector currencySelector = new CurrencySelector();
    private final SelenideElement saveBtn = $("#save");

    @Step("Set amount to: {amount}")
    public EditSpendingPage setAmount(String amount) {
        amountInput.val(amount);
        return this;
    }

    @Step("Add new category: {categoryName}")
    public EditSpendingPage addNewCategory(String categoryName) {
        category.val(categoryName);
        return this;
    }

    @Step("Set currency to: {currency}")
    public EditSpendingPage setCurrency(CurrencyValues currency) {
        currencySelector.changeCurrency(currency);
        return this;
    }

    @Step("Set date: {date}")
    public EditSpendingPage setDate(Date date) {
        calendarButton.shouldBe(visible).click();
        calendar.selectDateInCalendar(date);
        return this;
    }

    @Step("Set description to: {description}")
    public EditSpendingPage setNewSpendingDescription(String description) {
        descriptionInput.val(description);
        return this;
    }

    @Step("Save spending")
    public MainPage save() {
        saveBtn.click();
        return new MainPage();
    }
}
