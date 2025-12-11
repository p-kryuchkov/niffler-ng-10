package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class CategoryTest {
    private static final Config CFG = Config.getInstance();

    @User(categories = @Category(
            archived = false
    ))
    @Test
    public void archiveCategoryTest(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), "12345")
                .editProfile()
                .archiveCategory(user.testData().categories().getFirst().name())
                .checkShowArchivedCategories()
                .isCategoryExists(user.testData().categories().getFirst().name());
    }

    @User(categories = @Category(
            archived = true
    ))
    @Test
    public void unArchiveCathegoryTest(UserJson user) {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(user.username(), "12345")
                .editProfile()
                .checkShowArchivedCategories()
                .unArchiveCategory(user.testData().categories().getFirst().name())
                .isCategoryExists(user.testData().categories().getFirst().name())
                .uncheckShowArchivedCategories()
                .isCategoryExists(user.testData().categories().getFirst().name());
    }
}
