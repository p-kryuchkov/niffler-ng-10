package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.codeborne.selenide.Condition.text;

public class Calendar {
    private final SelenideElement self;
    private final SelenideElement currentMonthAndYear;
    private final SelenideElement switchToYearViewButton;
    private final SelenideElement arrowLeftButton;
    private final SelenideElement arrowRightButton;
    private final ElementsCollection dayButtons;
    private final ElementsCollection yearButtons;


    public Calendar(SelenideElement self) {
        this.self = self;
        this.currentMonthAndYear = self.$("div.MuiPickersCalendarHeader-label");
        this.switchToYearViewButton = self.$("[aria-label='calendar view is open, switch to year view']");
        this.arrowLeftButton = self.$("[data-testid='ArrowLeftIcon']");
        this.arrowRightButton = self.$("[data-testid='ArrowRightIcon']");
        this.dayButtons = self.$$("button.MuiButtonBase-root");
        this.yearButtons = self.$$("button.MuiPickersYear-yearButton");
    }

    public Calendar selectDateInCalendar(@Nonnull Date date) {
        String displayed = currentMonthAndYear.getText();
        LocalDate currentDate = LocalDate.parse("01 " + displayed, DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        LocalDate targetDate = LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
        if (targetDate.getYear() != currentDate.getYear()) {
            switchToYearViewButton.click();
            yearButtons.findBy(text(String.valueOf(targetDate.getYear()))).scrollTo().click();
        }
        while (targetDate.getMonth() != currentDate.getMonth()){
            if (targetDate.getMonth().getValue() < currentDate.getMonth().getValue()){
                arrowLeftButton.click();
            }
            if (targetDate.getMonth().getValue() > currentDate.getMonth().getValue()){
                arrowRightButton.click();
            }
            displayed = currentMonthAndYear.getText();
            currentDate = LocalDate.parse("01 " + displayed, DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        }
        return this;
    }


}
