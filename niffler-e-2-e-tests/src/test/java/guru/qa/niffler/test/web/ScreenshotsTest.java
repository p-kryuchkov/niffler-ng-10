package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(BrowserExtension.class)
public class ScreenshotsTest {
    private static final Config CFG = Config.getInstance();

    @User(
            spendings = @Spending(
                    amount = 1223
            ))
    @ScreenshotTest(value = "img/stat.png", rewriteExpected = false)
    void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
        String categoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());

        BufferedImage actual = ImageIO.read(
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .login(user.username(), user.testData().password())
                        .waitingSpendingDiagramLoad()
                        .checkLegendContainsCategory(categoryString)
                        .screenshotDiagram());

        assertFalse(new ScreenDiffResult(expected, actual));
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
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());

        BufferedImage actual = ImageIO.read(Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .deleteSpending(user.testData().spendings().getFirst().description())
                .waitingSpendingDiagramLoad()
                .checkLegendNotContainsCategory(firstCategoryString)
                .checkLegendContainsCategory(secondCategoryString)
                .screenshotDiagram());

        assertFalse(new ScreenDiffResult(expected, actual));
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
        String newAmount = "333";
        String firstCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                newAmount);

        String secondCategoryString = String.format("%s %s", user.testData().spendings().get(1).category().name(),
                user.testData().spendings().get(1).amount().intValue());

        String categoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue());
        BufferedImage actual = ImageIO.read(Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .editSpending(user.testData().spendings().getFirst().description())
                .setAmount(newAmount)
                .save()
                .waitingSpendingDiagramLoad()
                .checkLegendContainsCategory(firstCategoryString)
                .checkLegendContainsCategory(secondCategoryString)
                .screenshotDiagram());

        assertFalse(new ScreenDiffResult(expected, actual));
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
        String archiveCategoryString = String.format("%s %s", user.testData().spendings().getFirst().category().name(),
                user.testData().spendings().getFirst().amount().intValue() +
                        user.testData().spendings().get(1).amount().intValue());
        BufferedImage actual = ImageIO.read(Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.testData().password())
                .editProfile()
                .archiveCategory(user.testData().spendings().getFirst().category().name())
                .archiveCategory(user.testData().spendings().get(1).category().name())
                .goToMainPage()
                .waitingSpendingDiagramLoad()
                .screenshotDiagram());

        assertFalse(new ScreenDiffResult(expected, actual));
    }

    @User()
    @ScreenshotTest(value = "img/avatar.png", rewriteExpected = false)
        //Тест падает всегда из-за того что после загрузки аватара меняется немного цвет, писал в чат ответа не получил
    void checkAvatarTest(UserJson user, BufferedImage expected) throws IOException {
        BufferedImage actual = ImageIO.read(
                Selenide.open(CFG.frontUrl(), LoginPage.class)
                        .login(user.username(), user.testData().password())
                        .editProfile()
                        .uploadPicture(expected)
                        .screenshotAvatar());
        assertFalse(new ScreenDiffResult(expected, actual));
    }
}
