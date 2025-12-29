package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver selenideDriver = new SelenideDriver(SelenideUtils.chromeConfig);

    @User(
            spendings = @Spending(
                    amount = 89900,
                    currency = CurrencyValues.RUB
            ))
    @Test
    void spendingDescriptionShouldBeEditedByTableAction(UserJson user) {
        browserExtension.drivers().add(selenideDriver);

        final String newDescription = "Обучение Niffler Next Generation";


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .editSpending(user.testData().spendings().getFirst().description())
                .setNewSpendingDescription(newDescription)
                .save()
                .checkSnackbarText("Spending is edited successfully")
                .checkThatTableContains(newDescription);
    }

    @User()
    @Test
    void createNewSpendingTest(UserJson user) {
        browserExtension.drivers().add(selenideDriver);

        Date spendingDate = Date.from(LocalDate.of(2025, 12, 11)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        String categoryName = "Тестовая категория";
        String description = "Обучение Niffler Next Generation";
        String amount = "3453";


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .addSpending()
                .addNewCategory(categoryName)
                .setCurrency(CurrencyValues.RUB)
                .setAmount(amount)
                .setDate(spendingDate)
                .setNewSpendingDescription(description)
                .save()
                .checkSnackbarText("New spending is successfully created")
                .checkThatTableContains(description, categoryName, amount);
    }

    @User(
            spendings = @Spending(
                    amount = 1223
            ))
    @Test
    void deleteSpending(UserJson user) {
        browserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), user.testData().password())
                .deleteSpending(user.testData().spendings().getFirst().description())
                .checkSnackbarText("Spendings succesfully deleted");
    }
}