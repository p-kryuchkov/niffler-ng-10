package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spending(
                    amount = 89900,
                    currency = CurrencyValues.RUB
            ))
    @Test
    void spendingDescriptionShouldBeEditedByTableAction(UserJson user) {
        final String newDescription = "Обучение Niffler Next Generation";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .editSpending(user.testData().spendings().getFirst().description())
                .setNewSpendingDescription(newDescription)
                .save()
                .checkThatTableContains(newDescription);
    }

    @User()
    @Test
    void createNewSpendingTest(UserJson user){
        Date spendingDate = Date.from(LocalDate.of(2025, 12, 11)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant());
        String categoryName = "Тестовая категория";
        String description = "Обучение Niffler Next Generation";
        String amount = "3453";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .addSpending()
                .addNewCategory(categoryName)
                .setCurrency(CurrencyValues.RUB)
                .setAmount(amount)
                .setDate(spendingDate)
                .setNewSpendingDescription(description)
                .save()
                .checkThatTableContains(description, categoryName, amount);
    }
}
