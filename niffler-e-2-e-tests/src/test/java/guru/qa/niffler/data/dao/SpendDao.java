package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.spend.SpendEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpendDao {
    @Nonnull  SpendEntity create(@Nonnull SpendEntity spend);

    @Nonnull SpendEntity update(@Nonnull SpendEntity spend);

    @Nonnull Optional<SpendEntity> findSpendById(@Nonnull UUID id);

    @Nonnull List<SpendEntity> findAllByUsername(@Nonnull String username);

    @Nonnull List<SpendEntity> findAll();

    @Nonnull void deleteSpend(@Nonnull SpendEntity spend);
}