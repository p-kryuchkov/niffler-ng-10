package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryDao {
    @Nonnull CategoryEntity create(@Nonnull CategoryEntity category);

    @Nonnull Optional<CategoryEntity> findCategoryById(@Nonnull UUID id);

    @Nonnull CategoryEntity update(@Nonnull CategoryEntity categoryEntity);

    @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(@Nonnull String username, @Nonnull String categoryName);

    @Nonnull List<CategoryEntity> findAllByUsername(@Nonnull String username);

    @Nonnull List<CategoryEntity> findAll();

    void deleteCategory(@Nonnull CategoryEntity category);
}