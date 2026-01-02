package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.StaticBrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;

@ExtendWith(StaticBrowserExtension.class)

public class CategoryTest {
    private static final Config CFG = Config.getInstance();
    @RegisterExtension
    private final StaticBrowserExtension staticBrowserExtension = new StaticBrowserExtension();
    private final SelenideDriver selenideDriver = new SelenideDriver(SelenideUtils.chromeConfig);

    @User(categories = @Category(
            archived = false
    ))
    @Test
    public void archiveCategoryTest(UserJson user) {
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
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
        staticBrowserExtension.drivers().add(selenideDriver);


        selenideDriver.open(CFG.frontUrl());
        new LoginPage(selenideDriver)
                .login(user.username(), "12345")
                .editProfile()
                .checkShowArchivedCategories()
                .unArchiveCategory(user.testData().categories().getFirst().name())
                .isCategoryExists(user.testData().categories().getFirst().name())
                .uncheckShowArchivedCategories()
                .isCategoryExists(user.testData().categories().getFirst().name());
    }
}
