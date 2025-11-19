package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
            return SpendJson.fromEntity(spendRepository.create(SpendEntity.fromJson(spend)));
        });
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return xaTransactionTemplate.execute(() -> {
            return SpendJson.fromEntity(spendRepository.update(SpendEntity.fromJson(spend)));
        });
    }

    @Override
    public void deleteSpend(SpendJson spend) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.deleteSpend(SpendEntity.fromJson(spend));
            return null;
        });
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
            return CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(category)));
        });
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return xaTransactionTemplate.execute(() -> {
            return CategoryJson.fromEntity(spendRepository.updateCategory(CategoryEntity.fromJson(category)));
        });
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        xaTransactionTemplate.execute(() -> {
            spendRepository.deleteCategory(CategoryEntity.fromJson(category));
            return null;
        });
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return xaTransactionTemplate.execute(() -> {
            return Optional.of(CategoryJson.fromEntity(spendRepository.findCategoryByUsernameAndCategoryName(categoryName, username).get()));
        });
    }
}