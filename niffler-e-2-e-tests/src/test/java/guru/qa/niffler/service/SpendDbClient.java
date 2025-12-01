package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Optional;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Step("Create spend: {spend}")
    @Nullable
    @Override
    public SpendJson createSpend(@Nonnull SpendJson spend) {
        return xaTransactionTemplate.execute(() ->
                SpendJson.fromEntity(spendRepository.create(SpendEntity.fromJson(spend)))
        );
    }

    @Step("Update spend: {spend}")
    @Nullable
    @Override
    public SpendJson updateSpend(@Nonnull SpendJson spend) {
        return xaTransactionTemplate.execute(() ->
                SpendJson.fromEntity(spendRepository.update(SpendEntity.fromJson(spend)))
        );
    }

    @Step("Delete spend: {spend}")
    @Override
    public void deleteSpend(@Nonnull SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.deleteSpend(SpendEntity.fromJson(spend));
            return null;
        });
    }

    @Step("Create category: {category}")
    @Nullable
    @Override
    public CategoryJson createCategory(@Nonnull CategoryJson category) {
        return xaTransactionTemplate.execute(() ->
                CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(category)))
        );
    }

    @Step("Update category: {category}")
    @Override
    public @javax.annotation.Nullable CategoryJson updateCategory(@Nonnull CategoryJson category) {
        return xaTransactionTemplate.execute(() ->
                CategoryJson.fromEntity(spendRepository.updateCategory(CategoryEntity.fromJson(category)))
        );
    }

    @Step("Delete category: {category}")
    @Override
    public void deleteCategory(@Nonnull CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.deleteCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }

    @Step("Find category '{categoryName}' for username '{username}'")
    @Nullable
    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(@Nonnull String categoryName,
                                                                @Nonnull String username) {
        return xaTransactionTemplate.execute(() ->
                spendRepository.findCategoryByUsernameAndCategoryName(username, categoryName)
                        .map(CategoryJson::fromEntity)
        );
    }
}