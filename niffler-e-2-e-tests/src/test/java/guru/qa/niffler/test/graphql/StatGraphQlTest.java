package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.jupiter.annotation.meta.GqlTest;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Test;

import static guru.qa.type.CurrencyValues.EUR;
import static org.junit.jupiter.api.Assertions.assertEquals;
@GqlTest
public class StatGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void statTest(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> statsCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statsCall).blockingGet();
        StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                0.0,
                result.total
        );
    }


    @User(
            categories = {
                    @Category(name = "Actual category"),
                    @Category(name = "Archive category 1", archived = true),
                    @Category(name = "Archive category 2", archived = true),
            },
            spendings = {
                    @Spending(category = "Actual category", description = "Actual spending", amount = 100),
                    @Spending(category = "Archive category 1", description = "Archive spending 1", amount = 200),
                    @Spending(category = "Archive category 2", description = "Archive spending 2", amount = 200, currency = CurrencyValues.KZT)
            }
    )
    @Test
    @ApiLogin
    void statWithArchiveCategoriesTest(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> statsCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statsCall).blockingGet();
        StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                "Archived",
                result.statByCategories.getLast().categoryName
        );
        assertEquals(
                228,
                result.statByCategories.getLast().sum
        );
        assertEquals(
                CurrencyValues.RUB.name(),
                result.statByCategories.getLast().currency.rawValue
        );
    }

    @User(
            spendings = {
                    @Spending(category = "Rub category", description = "Rub spending", amount = 100),
                    @Spending(category = "EUR category", description = "EUR spending", amount = 200, currency = CurrencyValues.EUR),
                    @Spending(category = "USD category", description = "USD spending", amount = 200, currency = CurrencyValues.USD),
                    @Spending(category = "KZT category", description = "KZT spending", amount = 300, currency = CurrencyValues.KZT),

            }
    )
    @Test
    @ApiLogin
    void statWithDifferenceCurrenciesTest(@Token String bearerToken) {
        ApolloCall<StatQuery.Data> statsCall = apolloClient.query(StatQuery.builder()
                        .filterCurrency(EUR)
                        .statCurrency(EUR)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statsCall).blockingGet();
        StatQuery.Data data = response.dataOrThrow();
        StatQuery.Stat result = data.stat;
        assertEquals(
                1,
                result.statByCategories.size()
        );

        assertEquals(
                CurrencyValues.EUR.name(),
                result.statByCategories.getFirst().currency.rawValue
        );

        assertEquals(
                CurrencyValues.EUR.name(),
                result.currency.rawValue
        );
    }
}
