package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class SpendRepositoryHibernate implements SpendRepository {
    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public @Nonnull SpendEntity create(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public @Nonnull SpendEntity update(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.merge(spend);
        return spend;
    }

    @Nonnull
    @Override
    public Optional<SpendEntity> findSpendById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(SpendEntity.class, id));
    }

    @Override
    public @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username) {
        return entityManager.createQuery("select s from SpendEntity s where s.username = :username", SpendEntity.class)
                .setParameter("username", username)
                .getResultList();
    }

    @Override
    public @Nonnull List<SpendEntity> findAll() {
        return entityManager.createQuery("select s from SpendEntity s", SpendEntity.class)
                .getResultList();
    }

    @Override
    public void deleteSpend(@Nonnull SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.remove(spend);
    }

    @Override
    public @Nonnull CategoryEntity createCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public @Nonnull CategoryEntity updateCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.merge(category);
        return category;
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryById(@Nonnull UUID id) {
        return Optional.ofNullable(entityManager.find(CategoryEntity.class, id));
    }

    @Override
    public @Nonnull Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(@Nonnull String username, String categoryName) {
        try {
            return Optional.ofNullable(entityManager.createQuery("select c from CategoryEntity c where c.username = :username and c.name  = :name", CategoryEntity.class)
                    .setParameter("username", username)
                    .setParameter("name", categoryName)
                    .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteCategory(@Nonnull CategoryEntity category) {
        entityManager.joinTransaction();
        if (!entityManager.contains(category)) {
            category = entityManager.merge(category);
        }
        entityManager.remove(category);
    }
}
