package guru.qa.niffler.page;

import com.codeborne.selenide.ClickOptions;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage extends BasePage<ProfilePage> {
    public static final String URL = CFG.frontUrl() + "profile";

    private final SelenideElement avatar = $(".MuiAvatar-img");
    private final SelenideElement uploadPictureButton = $("input[type='file']");
    private final SelenideElement registerPassKey = $("#:r11:");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesButton = $("[type=submit]");
    private final SelenideElement showArchivedCategoriesCheckbox = $x("//span[contains(text(),'Show archived')]/..//input[@type='checkbox']");
    private final SelenideElement addNewCategoryInput = $("#category");
    private final SelenideElement submitArchive = $x("//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Archive']");
    private final SelenideElement submitUnArchive = $x("//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Unarchive']");
    private final Header header = new Header();

    @Step("Find category element: {category}")
    private SelenideElement findCategory(String category) {
        return $x("//span[contains(@class,'MuiChip-label') and text()='" + category + "']/ancestor::*[2]");
    }

    @Step("Find edit button for category: {category}")
    private SelenideElement editCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Edit category\"]");
    }

    @Step("Find archive button for category: {category}")
    private SelenideElement archiveCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Archive category\"]");
    }

    @Step("Find unarchive button for category: {category}")
    private SelenideElement unArchiveCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Unarchive category\"]");
    }

    @Step("Screenshot avatar")
    public File screenshotAvatar() {
        return avatar.screenshot();
    }

    @Step("Assert avatar screenshots match")
    public ProfilePage assertAvatarScreenshotsMatch(BufferedImage expected) {
        try {
            assertFalse(new ScreenDiffResult(expected, ImageIO.read(screenshotAvatar())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Step("Upload profile picture: {imageFile}")
    public ProfilePage uploadPicture(File imageFile) {
        uploadPictureButton.uploadFile(imageFile);
        return this;
    }

    @Step("Upload profile picture: {imageFile}")
    public ProfilePage uploadPicture(BufferedImage imageFile) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("img-", ".png");
            ImageIO.write(imageFile, "png", tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        uploadPicture(tempFile);
        return this;
    }

    @Step("Click 'Register PassKey' button")
    public ProfilePage clickRegisterPassKey() {
        registerPassKey.click();
        return this;
    }

    @Step("Check that username input contains value: {value}")
    public ProfilePage checkUserNameInputValue(String value) {
        usernameInput.shouldHave(text(value));
        return this;
    }

    @Step("Verify that username field is visible")
    public ProfilePage isUsernameFieldVisible() {
        usernameInput.shouldBe(visible);
        return this;
    }

    @Step("Set name to: {name}")
    public ProfilePage setName(String name) {
        nameInput.val(name);
        return this;
    }

    @Step("Clear name input field")
    public ProfilePage clearName() {
        nameInput.clear();
        return this;
    }

    @Step("Check that name input value equals: {value}")
    public ProfilePage checkNameInputValue(String value) {
        nameInput.shouldHave(value(value));
        return this;
    }

    @Step("Verify that name field is visible")
    public ProfilePage isNameFieldVisible() {
        nameInput.shouldBe(visible);
        return this;
    }

    @Step("Click 'Save changes' button")
    public ProfilePage clickSaveChanges() {
        saveChangesButton.click();
        return this;
    }

    @Step("Ensure 'Show archived categories' is checked")
    public ProfilePage checkShowArchivedCategories() {
        if (!showArchivedCategoriesCheckbox.isSelected()) {
            showArchivedCategoriesCheckbox.click(ClickOptions.usingJavaScript());
        }
        return this;
    }

    @Step("Ensure 'Show archived categories' is unchecked")
    public ProfilePage uncheckShowArchivedCategories() {
        if (showArchivedCategoriesCheckbox.isSelected()) {
            showArchivedCategoriesCheckbox.click(ClickOptions.usingJavaScript());
        }
        return this;
    }

    @Step("Toggle 'Show archived categories' checkbox")
    public ProfilePage toggleShowArchivedCategories() {
        showArchivedCategoriesCheckbox.shouldBe(clickable).click();
        return this;
    }

    @Step("Verify that 'Show archived categories' is checked")
    public ProfilePage isShowArchivedCategoriesChecked() {
        showArchivedCategoriesCheckbox.shouldBe(selected);
        return this;
    }

    @Step("Set new category name: {categoryName}")
    public ProfilePage setNewCategoryName(String categoryName) {
        addNewCategoryInput.val(categoryName);
        return this;
    }

    @Step("Add new category: {categoryName}")
    public ProfilePage addNewCategory(String categoryName) {
        setNewCategoryName(categoryName).pressEnterOnNewCategory();
        return this;
    }

    @Step("Clear new category input field")
    public ProfilePage clearNewCategoryInput() {
        addNewCategoryInput.clear();
        return this;
    }

    @Step("Press Enter in new category input field")
    public ProfilePage pressEnterOnNewCategory() {
        addNewCategoryInput.pressEnter();
        return this;
    }

    @Step("Check new category input value equals: {value}")
    public ProfilePage checkNewCategoryInputValue(String value) {
        addNewCategoryInput.shouldHave(text(value));
        return this;
    }

    @Step("Click edit button for category: {category}")
    public ProfilePage clickEditCategoryButton(String category) {
        editCategoryButton(category).click();
        return this;
    }

    @Step("Archive category: {category}")
    public ProfilePage archiveCategory(String category) {
        archiveCategoryButton(category).click();
        submitArchive.click();
        return this;
    }

    @Step("Unarchive category: {category}")
    public ProfilePage unArchiveCategory(String category) {
        unArchiveCategoryButton(category).click();
        submitUnArchive.click();
        return this;
    }

    @Step("Verify that category exists: {category}")
    public ProfilePage isCategoryExists(String category) {
        findCategory(category).shouldBe(exist);
        return this;
    }

    @Step("Go to main page")
    public MainPage goToMainPage() {
        return header.toMainPage();
    }
}
