package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.page.component.Calendar;
import guru.qa.niffler.page.component.CurrencySelector;

import java.util.Date;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement category = $("#category");
  private final SelenideElement calendarButton = $("[alt='Calendar']");
  private final Calendar calendar = new Calendar($("div.MuiDateCalendar-root"));
  private final CurrencySelector currencySelector = new CurrencySelector();
  private final SelenideElement saveBtn = $("#save");

  public EditSpendingPage setAmount(String amount) {
    amountInput.val(amount);
    return this;
  }

  public EditSpendingPage addNewCategory(String categoryName) {
    category.val(categoryName);
    return this;
  }

  public EditSpendingPage setCurrency(CurrencyValues currency) {
    currencySelector.changeCurrency(currency);
    return this;
  }

  public EditSpendingPage setDate(Date date) {
    calendarButton.shouldBe(visible).click();
    calendar.selectDateInCalendar(date);
    return this;
  }

  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.val(description);
    return this;
  }

  public MainPage save() {
    saveBtn.click();
    return new MainPage();
  }
}
