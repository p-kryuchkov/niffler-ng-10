package guru.qa.niffler.service;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public interface SpendClient {
    @Step("Create new spend: {spend}")
    @Nullable
    SpendJson createSpend(@Nonnull SpendJson spend);

    @Step("Update spend: {spend}")
    @Nullable
    SpendJson updateSpend(@Nonnull SpendJson spend);

    @Step("Delete spend: {spend}")
    void deleteSpend(@Nonnull SpendJson spend);

    @Step("Create new category: {category}")
    @Nullable
    CategoryJson createCategory(@Nonnull CategoryJson category);

    @Step("Update category: {category}")
    @Nullable
    CategoryJson updateCategory(@Nonnull CategoryJson category);

    @Step("Create new category: {category}")
    void deleteCategory(@Nonnull CategoryJson category);

    @Step("Find category by name: '{categoryName}' for user: {username}")
    @Nullable
    Optional<CategoryJson> findCategoryByNameAndUsername(@Nonnull String categoryName, @Nonnull String username);
}
