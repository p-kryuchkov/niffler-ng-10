package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.TransactionIsolationLevel;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return SpendJson.fromEntity(spendRepository.create(SpendEntity.fromJson(spend)));
    }

    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return SpendJson.fromEntity(spendRepository.update(SpendEntity.fromJson(spend)));
    }

    @Override
    public void deleteSpend(SpendJson spend) {
        spendRepository.deleteSpend(SpendEntity.fromJson(spend));
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return CategoryJson.fromEntity(spendRepository.createCategory(CategoryEntity.fromJson(category)));
    }

    @Override
    public void deleteCategory(CategoryJson category) {
        spendRepository.deleteCategory(CategoryEntity.fromJson(category));
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return transaction(connection -> {
            return new CategoryDaoJdbc().findCategoryByUsernameAndCategoryName(username, categoryName).map(entity -> CategoryJson.fromEntity(entity));
        }, CFG.spendJdbcUrl(), TransactionIsolationLevel.REPEATABLE_READ);
    }
}