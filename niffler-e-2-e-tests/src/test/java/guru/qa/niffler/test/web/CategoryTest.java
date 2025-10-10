package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.StaticUser;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class CategoryTest {
    private static final Config CFG = Config.getInstance();

    @User(
            categories = @Category(
                    archived = false
            ))
    @Test
    public void archiveCategoryTest(CategoryJson categoryJson, StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .editProfile()
                .archiveCategory(categoryJson.name())
                .checkShowArchivedCategories()
                .isCategoryExists(categoryJson.name());
    }

    @User(
            categories = @Category(
                    archived = true
            ))
    @Test
    public void unArchiveCathegoryTest(CategoryJson categoryJson, StaticUser user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), user.password())
                .editProfile()
                .checkShowArchivedCategories()
                .unArchiveCategory(categoryJson.name())
                .uncheckShowArchivedCategories()
                .isCategoryExists(categoryJson.name());
    }
}
