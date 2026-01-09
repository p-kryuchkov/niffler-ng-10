package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class ScreenshotsTest {
    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spending(
                    amount = 1223
            ))
    @ApiLogin
    @ScreenshotTest(value = "img/stat.png", rewriteExpected = false)
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        String categoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());

        Selenide.open(MainPage.URL, MainPage.class)
                .waitingSpendingDiagramLoad()
                .checkLegendContainsCategory(categoryString)
                .assertDiagramScreenshotsMatch(expected)
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 500, category = "Еда")
            }
    )
    @ApiLogin
    @Test
    @ScreenshotTest(value = "img/deletestat.png", rewriteExpected = false)
    void checkStatComponentDeleteSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());

        Selenide.open(MainPage.URL, MainPage.class)
                .deleteSpending(user.testData().spendings().getFirst().description())
                .waitingSpendingDiagramLoad()
                .checkLegendNotContainsCategory(firstCategoryString)
                .checkLegendContainsCategory(secondCategoryString)
                .assertDiagramScreenshotsMatch(expected)
                .checkSpends(user.testData().spendings().get(1));
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @ApiLogin
    @Test
    @ScreenshotTest(value = "img/editstat.png", rewriteExpected = false)
    void checkStatComponentEditSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        String newAmount = "333";
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                newAmount);

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());

        Selenide.open(MainPage.URL, MainPage.class)
                .editSpending(user.testData().spendings().getFirst().description())
                .setAmount(newAmount)
                .save()
                .waitingSpendingDiagramLoad()
                .checkLegendContainsCategory(firstCategoryString)
                .checkLegendContainsCategory(secondCategoryString)
                .assertDiagramScreenshotsMatch(expected);
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @ApiLogin
    @Test
    @ScreenshotTest(value = "img/archivestat.png", rewriteExpected = false)
    void checkStatComponentArchiveTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .archiveCategory(user.testData().spendings().getFirst().category().name())
                .goToMainPage()
                .waitingSpendingDiagramLoad()
                .assertDiagramScreenshotsMatch(expected);
    }

    @User()
    @ApiLogin
    @ScreenshotTest(value = "img/avatar.png", rewriteExpected = false)
        //Тест падает всегда из-за того что после загрузки аватара меняется немного цвет, писал в чат ответа не получил
    void checkAvatarTest(UserJson user, BufferedImage expected) throws IOException {
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .uploadPicture(expected)
                .assertAvatarScreenshotsMatch(expected);
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @ApiLogin
    @Test
    void checkOrderedBubbles(UserJson user) throws IOException {
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().get(0).amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());

        Selenide.open(MainPage.URL, MainPage.class)
                .checkBubblesWithOrder(new Bubble(Color.YELLOW, firstCategoryString),
                        new Bubble(Color.GREEN, secondCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @ApiLogin
    @Test
    void checkBubblesInAnyOrder(UserJson user) throws IOException {
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().get(0).amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());

        Selenide.open(MainPage.URL, MainPage.class)
                .checkBubblesInAnyOrder(
                        new Bubble(Color.GREEN, secondCategoryString),
                        new Bubble(Color.YELLOW, firstCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @ApiLogin
    @Test
    void checkBubblesContains(UserJson user) throws IOException {
        String firstCategoryString = String.format("%s %s ₽", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());

        Selenide.open(MainPage.URL, MainPage.class)
                .checkBubblesContains(
                        new Bubble(Color.YELLOW, firstCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }
}