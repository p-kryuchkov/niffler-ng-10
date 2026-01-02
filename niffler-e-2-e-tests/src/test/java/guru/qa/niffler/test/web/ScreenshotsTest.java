package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.converter.Browser;
import guru.qa.niffler.jupiter.converter.BrowserDriverConverter;
import guru.qa.niffler.jupiter.extension.StaticBrowserExtension;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import java.awt.image.BufferedImage;
import java.io.IOException;

//тесты будут падать в зависимоcти от браузера
@ExtendWith(StaticBrowserExtension.class)
public class ScreenshotsTest {
    private static final Config CFG = Config.getInstance();
    @RegisterExtension
    private static final StaticBrowserExtension STATIC_BROWSER_EXTENSION = new StaticBrowserExtension();

    @ParameterizedTest
    @EnumSource(Browser.class)
    @User(
            spendings = @Spending(
                    amount = 1223
            ))
    @ScreenshotTest(value = "img/stat.png", rewriteExpected = false)
    void checkStatComponentTest(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
            UserJson user, BufferedImage expected) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);

        String categoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
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
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ScreenshotTest(value = "img/deletestat.png", rewriteExpected = false)
    void checkStatComponentDeleteSpendingTest(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
                                              UserJson user, BufferedImage expected) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);

        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
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
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ScreenshotTest(value = "img/editstat.png", rewriteExpected = false)
    void checkStatComponentEditSpendingTest(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
                                            UserJson user, BufferedImage expected) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);

        String newAmount = "333";
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                newAmount);

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
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
    @ParameterizedTest
    @EnumSource(Browser.class)
    @ScreenshotTest(value = "img/archivestat.png", rewriteExpected = false)
    void checkStatComponentArchiveTest(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
                                       UserJson user, BufferedImage expected) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .editProfile()
                .archiveCategory(user.testData().spendings().getFirst().category().name())
                .goToMainPage()
                .waitingSpendingDiagramLoad()
                .assertDiagramScreenshotsMatch(expected);
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    @User()
    @ScreenshotTest(value = "img/avatar.png", rewriteExpected = false)
        //Тест падает всегда из-за того что после загрузки аватара меняется немного цвет, писал в чат ответа не получил
    void checkAvatarTest(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
                         UserJson user, BufferedImage expected) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .editProfile()
                .uploadPicture(expected)
                .assertAvatarScreenshotsMatch(expected);
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    void checkOrderedBubbles(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
                             UserJson user) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);

        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().get(0).amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkBubblesWithOrder(new Bubble(Color.yellow, firstCategoryString),
                        new Bubble(Color.green, secondCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    void checkBubblesInAnyOrder(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
                                UserJson user) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);

        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().get(0).amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkBubblesInAnyOrder(
                        new Bubble(Color.green, secondCategoryString),
                        new Bubble(Color.yellow, firstCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }

    @ParameterizedTest
    @EnumSource(Browser.class)
    @User(
            spendings = {
                    @Spending(amount = 1223, category = "Путешествия"),
                    @Spending(amount = 100, category = "Учеба")
            }
    )
    void checkBubblesContains(@ConvertWith(BrowserDriverConverter.class) SelenideDriver driver,
                              UserJson user) throws IOException {
        STATIC_BROWSER_EXTENSION.drivers().add(driver);

        String firstCategoryString = String.format("%s %s ₽", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());


        driver.open(CFG.frontUrl());
        new LoginPage(driver)
                .login(user.username(), user.testData().password())
                .checkBubblesContains(
                        new Bubble(Color.yellow, firstCategoryString))
                .checkSpends(user.testData().spendings().toArray(SpendJson[]::new));
    }
}
