package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.CurrencySelector;
import io.qameta.allure.Step;

import java.io.File;
import java.util.Date;

import static com.codeborne.selenide.Condition.visible;


public class EditSpendingPage extends BasePage <EditSpendingPage>{
    private final SelenideElement descriptionInput;
    private final SelenideElement amountInput;
    private final SelenideElement category;
    private final SelenideElement calendarButton;
    private final Calendar calendar;
    private final CurrencySelector currencySelector;
    private final SelenideElement saveBtn;


    public EditSpendingPage(SelenideDriver driver) {
        super(driver);
        this.descriptionInput = driver.$("#description");
        this.amountInput = driver.$("#amount");
        this.category = driver.$("#category");
        this.calendarButton = driver.$("[alt='Calendar']");
        this.calendar = new Calendar(driver, driver.$("div.MuiDateCalendar-root"));
        this.currencySelector = new CurrencySelector(driver);
        this.saveBtn = driver.$("#save");
    }

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
        return new MainPage(driver);
    }
}
