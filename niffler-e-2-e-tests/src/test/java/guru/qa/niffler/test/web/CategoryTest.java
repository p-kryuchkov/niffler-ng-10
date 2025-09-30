package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.service.SpendApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class CategoryTest {
    private static final Config CFG = Config.getInstance();

    @Category(
            username = "TestUserForCategory",
            archived = false
    )
    @Test
    public void archiveCategoryTest(CategoryJson categoryJson) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(categoryJson.username(), "CatCat")
                .editProfile()
                .archiveCategory(categoryJson.name())
                .checkShowArchivedCategories()
                .isCategoryExists(categoryJson.name());
    }

    @Category(
            username = "TestUserForCategory",
            archived = true
    )
    @Test
    public void unArchiveCathegoryTest(CategoryJson categoryJson) {

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(categoryJson.username(), "CatCat")
                .editProfile()
                .checkShowArchivedCategories()
                .unArchiveCategory(categoryJson.name())
                .uncheckShowArchivedCategories()
                .isCategoryExists(categoryJson.name());
    }
}
