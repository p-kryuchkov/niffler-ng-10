package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.TransactionIsolationLevel;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    @Override
    public SpendJson createSpend(SpendJson spend) {
        return transaction(connection -> {
            SpendEntity spendEntity = SpendEntity.fromJson(spend);
            if (spendEntity.getCategory().getId() == null) {
                CategoryEntity categoryEntity = new CategoryDaoJdbc().create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
            return SpendJson.fromEntity(new SpendDaoJdbc().create(spendEntity));
        }, CFG.spendJdbcUrl(), TransactionIsolationLevel.REPEATABLE_READ);
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc().create(CategoryEntity.fromJson(category)));
        }, CFG.spendJdbcUrl(), TransactionIsolationLevel.REPEATABLE_READ);
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return transaction(connection -> {
            return CategoryJson.fromEntity(new CategoryDaoJdbc().update(CategoryEntity.fromJson(category)));
        }, CFG.spendJdbcUrl(), TransactionIsolationLevel.REPEATABLE_READ);
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return transaction(connection -> {
            return new CategoryDaoJdbc().findCategoryByUsernameAndCategoryName(username, categoryName).map(entity -> CategoryJson.fromEntity(entity));
        }, CFG.spendJdbcUrl(), TransactionIsolationLevel.REPEATABLE_READ);
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return transaction(connection -> {
            return new CategoryDaoJdbc().findCategoryById(id).map(entity -> CategoryJson.fromEntity(entity));

        }, CFG.spendJdbcUrl(), TransactionIsolationLevel.REPEATABLE_READ);
    }

    private SpendEntity enrichSpend(SpendEntity spend) {
        return transaction(connection -> {
            if (spend.getCategory() != null && spend.getCategory().getId() != null) {
                CategoryEntity category = new CategoryDaoJdbc().findCategoryById(spend.getCategory().getId()).orElseThrow(() -> new NoSuchElementException("Category not found by id: " + spend.getCategory().getId()));
                spend.setCategory(category);
            }
            return spend;
        }, CFG.spendJdbcUrl(), TransactionIsolationLevel.REPEATABLE_READ);
    }
}