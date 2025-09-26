package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.service.SpendApiClient;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CategoryTest {
    private static final Config CFG = Config.getInstance();

    @Category(
            name = "TestCategory",
            username = "TestUserForCategory",
            archived = false
    )
    @Test
    public void achiveCathegoryTest(CategoryJson categoryJson) {
        SpendApiClient spendApiClient = new SpendApiClient();

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .login(categoryJson.username(), "CatCat")
                .editProfile()
                .clickArchiveCategory(categoryJson.name());

        CategoryJson found = spendApiClient
                .findCategoryByNameAndUsername(categoryJson.name(), categoryJson.username())
                .orElseThrow(() -> new AssertionError("Category not found"));

        assertTrue(found.archived());
    }
}
