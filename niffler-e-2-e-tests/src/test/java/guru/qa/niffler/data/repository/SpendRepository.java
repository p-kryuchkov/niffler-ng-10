package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {
    @Nonnull
    SpendEntity create(@Nonnull SpendEntity spend);
    @Nonnull
    SpendEntity update(@Nonnull SpendEntity spend);
    @Nonnull
    Optional<SpendEntity> findSpendById(@Nonnull UUID id);
    @Nonnull
    List<SpendEntity> findAllByUsername(@Nonnull String username);
    @Nonnull
    List<SpendEntity> findAll();

    void deleteSpend(@Nonnull SpendEntity spend);

    @Nonnull CategoryEntity createCategory(@Nonnull CategoryEntity category);

    @Nonnull CategoryEntity updateCategory(@Nonnull CategoryEntity category);


    @Nonnull Optional<CategoryEntity> findCategoryById(@Nonnull UUID id);

    @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(@Nonnull String username, String categoryName);

    void deleteCategory(@Nonnull CategoryEntity category);
}
