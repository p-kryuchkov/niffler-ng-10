package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthorityEntity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthAuthorityDao {
    void create(@Nonnull AuthorityEntity... authority);

    @Nonnull List<AuthorityEntity> findAuthoritiesByUserId(@Nonnull UUID id);

    @Nonnull Optional<AuthorityEntity> findById(@Nonnull UUID id);

    void delete(@Nonnull AuthorityEntity authority);
}
