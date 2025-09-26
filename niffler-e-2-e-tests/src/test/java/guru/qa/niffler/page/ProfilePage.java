package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import java.io.File;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class ProfilePage {

    private final SelenideElement uploadPictureButton = $(".image__input-label");
    private final SelenideElement registerPassKey = $("#:r11:");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement nameInput = $("#name");
    private final SelenideElement saveChangesButton = $("#:r12:");
    private final SelenideElement showArchivedCategoriesCheckbox = $x("//span[contains(text(),'Show archived')]/..//input[@type='checkbox']");
    private final SelenideElement addNewCategoryInput = $("#category");
    private final SelenideElement submitArchive = $x("//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Archive']");
    private final SelenideElement submitUnArchive = $x("//button[contains(@class,'MuiButtonBase-root') and normalize-space(text())='Unarchive']");

    private SelenideElement findCategory(String category) {
        return $x("//span[contains(@class,'MuiChip-label') and text()='" + category + "']/ancestor::*[2]");
    }

    private SelenideElement editCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Edit category\"]");
    }

    private SelenideElement archiveCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Archive category\"]");
    }

    private SelenideElement unArchiveCategoryButton(String category) {
        return findCategory(category).find("[aria-label=\"Unarchive category\"]");
    }

    public ProfilePage uploadPicture(File imageFile) {
        uploadPictureButton.uploadFile(imageFile);
        return this;
    }

    public ProfilePage clickRegisterPassKey() {
        registerPassKey.click();
        // не очень понятно что дальше
        return this;
    }

    public String getUsername() {
        return usernameInput.getValue();
    }

    public boolean isUsernameFieldVisible() {
        return usernameInput.isDisplayed();
    }

    public ProfilePage setName(String name) {
        nameInput.val(name);
        return this;
    }

    public ProfilePage clearName() {
        nameInput.clear();
        return this;
    }

    public String getName() {
        return nameInput.getValue();
    }

    public boolean isNameFieldVisible() {
        return nameInput.isDisplayed();
    }

    public ProfilePage clickSaveChanges() {
        saveChangesButton.click();
        return this;
    }


    public ProfilePage checkShowArchivedCategories() {
        if (!isShowArchivedCategoriesChecked()) {
            toggleShowArchivedCategories();
        }
        return this;
    }

    public ProfilePage uncheckShowArchivedCategories() {
        if (isShowArchivedCategoriesChecked()) {
            toggleShowArchivedCategories();
        }
        return this;
    }

    public ProfilePage toggleShowArchivedCategories() {
        showArchivedCategoriesCheckbox.click();
        return this;
    }

    public boolean isShowArchivedCategoriesChecked() {
        return showArchivedCategoriesCheckbox.isSelected();
    }

    public ProfilePage setNewCategoryName(String categoryName) {
        addNewCategoryInput.val(categoryName);
        return this;
    }

    public ProfilePage addNewCategory(String categoryName) {
        setNewCategoryName(categoryName).pressEnterOnNewCategory();
        return this;
    }

    public ProfilePage clearNewCategoryInput() {
        addNewCategoryInput.clear();
        return this;
    }

    public ProfilePage pressEnterOnNewCategory() {
        addNewCategoryInput.pressEnter();
        return this;
    }

    public String getNewCategoryInputValue() {
        return addNewCategoryInput.getValue();
    }


    public ProfilePage clickEditCategoryButton(String category) {
        editCategoryButton(category);
        return this;
    }

    public boolean isEditCategoryButtonVisible(String category) {
        return editCategoryButton(category).isDisplayed();
    }

    public ProfilePage archiveCategory(String category) {
        archiveCategoryButton(category).click();
        submitArchive.click();
        return this;
    }

    public boolean isArchiveCategoryButtonVisible(String category) {
        return archiveCategoryButton(category).isDisplayed();
    }

    public ProfilePage unArchiveCategory(String category) {
        unArchiveCategoryButton(category).click();
        submitUnArchive.click();
        return this;
    }

    public boolean isUnArchiveCategoryButtonVisible(String category) {
        return unArchiveCategoryButton(category).isDisplayed();
    }

    public boolean isCategoryExists(String category) {
        return findCategory(category).exists();
    }
}
