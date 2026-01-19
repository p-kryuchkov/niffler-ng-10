package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
