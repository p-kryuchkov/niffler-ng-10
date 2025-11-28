package guru.qa.niffler.data;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import jakarta.transaction.SystemException;
import jakarta.transaction.UserTransaction;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
@ParametersAreNonnullByDefault
public class Databases {
    private Databases() {
    }

    private static final Map<String, DataSource> dataSources = new ConcurrentHashMap<>();
    private static final Map<Long, Map<String, Connection>> threadConnections = new ConcurrentHashMap<>();

    public record XaFunction<T>(Function<Connection, T> function, String jdbcUrl) {
    }

    public record XaConsumer(Consumer<Connection> function, String jdbcUrl) {
    }

    @Nonnull
    public static <T> T transaction(Function<Connection, T> function, String jdbcUrl) {
        return transaction(function, jdbcUrl, TransactionIsolationLevel.READ_COMMITTED);
    }

    @Nonnull
    public static <T> T transaction(Function<Connection, T> function, String jdbcUrl, TransactionIsolationLevel isolationLevel) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);
            connection.setTransactionIsolation(isolationLevel.getLevel());
            connection.setAutoCommit(false);
            T result = function.apply(connection);
            connection.commit();
            connection.setAutoCommit(true);
            return result;
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static <T> T xaTransaction(XaFunction<T>... actions) {
        return xaTransaction(TransactionIsolationLevel.READ_COMMITTED, actions);
    }

    @Nullable
    public static <T> T xaTransaction(TransactionIsolationLevel isolationLevel, XaFunction<T>... actions) {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin();
            T result = null;
            for (XaFunction<T> action : actions) {
                Connection connection = connection(action.jdbcUrl);
                connection.setTransactionIsolation(isolationLevel.getLevel());
                result = action.function.apply(connection);
            }
            ut.commit();
            return result;
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    public static void transaction(Consumer<Connection> consumer, String jdbcUrl) {
        transaction(consumer, jdbcUrl, TransactionIsolationLevel.READ_COMMITTED);
    }

    public static void transaction(Consumer<Connection> consumer, String jdbcUrl, TransactionIsolationLevel isolationLevel) {
        Connection connection = null;
        try {
            connection = connection(jdbcUrl);
            connection.setTransactionIsolation(isolationLevel.getLevel());
            connection.setAutoCommit(false);
            consumer.accept(connection);
            connection.commit();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    connection.setAutoCommit(true);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            throw new RuntimeException(e);
        }
    }

    public static void xaTransaction(XaConsumer... actions) {
        xaTransaction(TransactionIsolationLevel.READ_COMMITTED, actions);
    }

    @Nonnull
    public static void xaTransaction(TransactionIsolationLevel isolationLevel, XaConsumer... actions) {
        UserTransaction ut = new UserTransactionImp();
        try {
            ut.begin();
            for (XaConsumer action : actions) {
                Connection connection = connection(action.jdbcUrl);
                connection.setTransactionIsolation(isolationLevel.getLevel());
                action.function.accept(connection);
            }
            ut.commit();
        } catch (Exception e) {
            try {
                ut.rollback();
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    private static DataSource dataSource(String jdbcUrl) {
        return dataSources.computeIfAbsent(jdbcUrl, key -> {
            AtomikosDataSourceBean dsBean = new AtomikosDataSourceBean();
            final String uniqId = StringUtils.substringAfter(jdbcUrl, "5432/");
            dsBean.setUniqueResourceName(uniqId);
            dsBean.setXaDataSourceClassName("org.postgresql.xa.PGXADataSource");
            Properties props = new Properties();
            props.put("URL", jdbcUrl);
            props.put("user", "postgres");
            props.put("password", "secret");
            dsBean.setXaProperties(props);
            dsBean.setMaxPoolSize(10);
            return dsBean;
        });
    }

    @Nonnull
    private static Connection connection(String jdbcUrl) throws SQLException {
        return threadConnections.computeIfAbsent(Thread.currentThread().threadId(), key -> {
            try {
                return new HashMap<>(Map.of(jdbcUrl, dataSource(jdbcUrl).getConnection()));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }).computeIfAbsent(jdbcUrl, key -> {
            try {
                return dataSource(jdbcUrl).getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void closeAllConnections() {
        for (Map<String, Connection> connectionMap : threadConnections.values()) {
            for (Connection connection : connectionMap.values()) {
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // NOP
                }
            }
        }
    }
}