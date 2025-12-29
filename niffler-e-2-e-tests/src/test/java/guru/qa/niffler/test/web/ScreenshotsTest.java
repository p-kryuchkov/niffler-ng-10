package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class ScreenshotsTest {
    private static final Config CFG = Config.getInstance();
    @RegisterExtension
    private final BrowserExtension browserExtension = new BrowserExtension();
    private final SelenideDriver chrome = new SelenideDriver(SelenideUtils.chromeConfig);
    private final SelenideDriver firefox = new SelenideDriver(SelenideUtils.chromeConfig);

    @User(
            spendings = @Spending(
                    amount = 1223
            ))
    @ScreenshotTest(value = "img/stat.png", rewriteExpected = false)
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        browserExtension.drivers().add(chrome);
        browserExtension.drivers().add(firefox);

        String categoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
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
    @Test
    @ScreenshotTest(value = "img/deletestat.png", rewriteExpected = false)
    void checkStatComponentDeleteSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        browserExtension.drivers().add(chrome);

        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
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
    @Test
    @ScreenshotTest(value = "img/editstat.png", rewriteExpected = false)
    void checkStatComponentEditSpendingTest(UserJson user, BufferedImage expected) throws IOException {
        browserExtension.drivers().add(chrome);

        String newAmount = "333";
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                newAmount);

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
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
    @Test
    @ScreenshotTest(value = "img/archivestat.png", rewriteExpected = false)
    void checkStatComponentArchiveTest(UserJson user, BufferedImage expected) throws IOException {
        browserExtension.drivers().add(chrome);


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
                .editProfile()
                .archiveCategory(user.testData().spendings().getFirst().category().name())
                .goToMainPage()
                .waitingSpendingDiagramLoad()
                .assertDiagramScreenshotsMatch(expected);
    }

    @User()
    @ScreenshotTest(value = "img/avatar.png", rewriteExpected = false)
        //Тест падает всегда из-за того что после загрузки аватара меняется немного цвет, писал в чат ответа не получил
    void checkAvatarTest(UserJson user, BufferedImage expected) throws IOException {
        browserExtension.drivers().add(chrome);


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
                .editProfile()
                .uploadPicture(expected)
                .assertAvatarScreenshotsMatch(expected);
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @Test
    void checkOrderedBubbles(UserJson user) throws IOException {
        browserExtension.drivers().add(chrome);

        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().get(0).amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
                .checkBubblesWithOrder(new Bubble(Color.yellow, firstCategoryString),
                        new Bubble(Color.green, secondCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @Test
    void checkBubblesInAnyOrder(UserJson user) throws IOException {
        browserExtension.drivers().add(chrome);

        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().get(0).amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
                .checkBubblesInAnyOrder(
                        new Bubble(Color.green, secondCategoryString),
                        new Bubble(Color.yellow, firstCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    @Test
    void checkBubblesContains(UserJson user) throws IOException {
        browserExtension.drivers().add(chrome);

        String firstCategoryString = String.format("%s %s ₽", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());


        chrome.open(CFG.frontUrl());
        new LoginPage(chrome)
                .login(user.username(), user.testData().password())
                .checkBubblesContains(
                        new Bubble(Color.yellow, firstCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }
}
