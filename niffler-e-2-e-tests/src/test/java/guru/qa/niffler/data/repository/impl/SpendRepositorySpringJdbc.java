package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {
    private static final Config CFG = Config.getInstance();
    private static final SpendDao spendDao = new SpendDaoSpringJdbc();
    private static final CategoryDao categoryDao = new CategoryDaoJdbc();

    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        return xaTransactionTemplate.execute(() -> {
            SpendEntity resultSpend = spendDao.create(spend);
            if (spend.getCategory() != null) {
                categoryDao.findCategoryById(spend.getCategory().getId())
                        .orElseGet(() -> categoryDao.create(spend.getCategory()));
            }
            return resultSpend;
        });
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        return spendDao.update(spend);
    }

    @NotNull
    @Override
    public Optional<SpendEntity> findSpendById(@NotNull UUID id) {
        return spendDao.findSpendById(id).map(spendEntity -> {
            spendEntity.setCategory(categoryDao.findCategoryById(spendEntity.getCategory().getId()).get());
            return spendEntity;
        });
    }

    @Override
    public @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username) {
        List<SpendEntity> resultList = spendDao.findAllByUsername(username);
        for (SpendEntity spend : resultList) {
            spend.setCategory(categoryDao.findCategoryById(spend.getCategory().getId()).get());
        }
        return resultList;
    }

    @Override
    public @Nonnull List<SpendEntity> findAll() {
        return spendDao.findAll();
    }

    @Override
    public void deleteSpend(@Nonnull SpendEntity spend) {
        spendDao.deleteSpend(spend);
    }

    @Override
    public @Nonnull CategoryEntity createCategory(@Nonnull CategoryEntity category) {
        return categoryDao.create(category);
    }

    @Override
    public @Nonnull CategoryEntity updateCategory(@Nonnull CategoryEntity category) {
        return categoryDao.update(category);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
        return categoryDao.findCategoryById(id);
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(@Nonnull String username, String categoryName) {
        return categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);
    }

    @Override
    public void deleteCategory(@Nonnull CategoryEntity category) {
        categoryDao.deleteCategory(category);
    }
}
