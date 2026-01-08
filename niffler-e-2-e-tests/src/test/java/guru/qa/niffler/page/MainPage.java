package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.model.Bubble;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SearchField;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.Statistics;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage extends BasePage<MainPage> {
    public static final String URL = CFG.frontUrl() + "main";
    private final SelenideElement spendings = $("#spendings");
    private final SearchField searchInput = new SearchField();
    private final Header header = new Header();
    private final SpendingTable spendingTable = new SpendingTable();
    private final Statistics statistics = new Statistics();

    @Step("Open spending editor for spending with description: {description}")
    public EditSpendingPage editSpending(String description) {
        return spendingTable.editSpending(description);
    }

    @Step("Verify that the spending table contains values: {args}")
    public MainPage checkThatTableContains(String... args) {
        spendingTable.checkTableContains(args);
        return this;
    }

    @Step("Delete Spending: {description}")
    public MainPage deleteSpending(String description) {
        spendingTable.deleteSpending(description);
        return this;
    }

    @Step("Check that main page elements are visible")
    public MainPage checkElements() {
        spendings.shouldBe(visible);
        return this;
    }

    @Step("Open profile page")
    public ProfilePage editProfile() {
        return header.toProfilePage();
    }

    @Step("Open 'All People' page")
    public AllPeoplePage goToAllPeoplePage() {
        return header.toAllPeoplesPage();
    }

    @Step("Open friends page")
    public FriendsPage goToFriendsPage() {
        return header.toFriendsPage();
    }

    @Step("Open 'Add spending' page")
    public EditSpendingPage addSpending() {
        return header.addSpendingPage();
    }

    @Step("Waiting spending diagram load")
    public MainPage waitingSpendingDiagramLoad() {
        statistics.waitLoadingDiagram();
        return this;
    }

    @Step("Screenshot diagram")
    public File screenshotDiagram() {
        return statistics.screenshotDiagram();
    }

    @Step("Assert diagram screenshots match")
    public MainPage assertDiagramScreenshotsMatch(BufferedImage expected) {
        try {
            assertFalse(new ScreenDiffResult(expected, ImageIO.read(screenshotDiagram())), "Screen comparison failure");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Step("Check category in legend")
    public MainPage checkLegendContainsCategory(String category) {
        statistics.checkLegendContainsCategory(category);
        return this;
    }

    @Step("Check category is not in legend")
    public MainPage checkLegendNotContainsCategory(String category) {
        statistics.checkLegendNotContainsCategory(category);
        return this;
    }

    @Step("Check bubbles with order under diagram")
    public MainPage checkBubblesWithOrder(String... categories){
        statistics.checkBubbles(getOrderedBubbles(categories));
        return this;
    }

    @Step("Check bubbles with order under diagram")
    public MainPage checkBubblesWithOrder(Bubble... bubbles){
        statistics.checkBubbles(bubbles);
        return this;
    }

    @Step("Check bubbles contains under diagram")
    public MainPage checkBubblesContains(Bubble... bubbles){
        statistics.checkBubblesContains(bubbles);
        return this;
    }

    @Step("Check bubbles in any order under diagram")
    public MainPage checkBubblesInAnyOrder(Bubble... bubbles){
        statistics.checkBubblesInAnyOrder(bubbles);
        return this;
    }

    @Step("Check spends in table")
    public MainPage checkSpends(SpendJson... spends){
        spendingTable.checkSpends(spends);
        return this;
    }

    private Bubble[] getOrderedBubbles(String... categories){
        Bubble[] bubbles = new Bubble[categories.length];
        Color[] orderedColors = Color.orderedColors();
        for (int i = 0; i < bubbles.length; i++) {
            bubbles[i] = new Bubble(orderedColors[i], categories[i]);
        }
        return bubbles;
    }
}
