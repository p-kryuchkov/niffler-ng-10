package guru.qa.niffler.data.jpa;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import guru.qa.niffler.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {
    private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

    private EntityManagers() {
    }

    @Nonnull
    @SuppressWarnings("resource")
    public static EntityManager em(String jdbcUrl) {
        return new ThreadSafeEntityManager(
                emfs.computeIfAbsent(
                        jdbcUrl,
                        key -> {
                            DataSources.dataSource(jdbcUrl);
                            final String persistenceUnitName = StringUtils.substringAfter(jdbcUrl, "5432/");
                            return Persistence.createEntityManagerFactory(persistenceUnitName);
                        }
                ).createEntityManager()
        );
    }
}