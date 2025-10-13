package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    @Override
    public SpendJson createSpend(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);
        if (spendEntity.getCategory().getId() == null) {
            CategoryEntity categoryEntity = categoryDao.create(spendEntity.getCategory());
            spendEntity.setCategory(categoryEntity);
        }
        return SpendJson.fromEntity(
                spendDao.create(spendEntity)
        );
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return CategoryJson.fromEntity(
                categoryDao.create(
                        CategoryEntity.fromJson(category)
                )
        );
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return CategoryJson.fromEntity(
                categoryDao.update(
                        CategoryEntity.fromJson(category)
                )
        );
    }

    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(String categoryName, String username) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName)
                .map(entity -> CategoryJson.fromEntity(entity));
    }

    public Optional<CategoryJson> findCategoryById(UUID id) {
        return categoryDao.findCategoryById(id)
                .map(entity -> CategoryJson.fromEntity(entity));

    }

    private SpendEntity enrichSpend(SpendEntity spend) {
        if (spend.getCategory() != null && spend.getCategory().getId() != null) {
            CategoryEntity category = categoryDao.findCategoryById(spend.getCategory().getId())
                    .orElseThrow(() -> new NoSuchElementException(
                            "Category not found by id: " + spend.getCategory().getId()
                    ));
            spend.setCategory(category);
        }
        return spend;
    }
}