package guru.qa.niffler.test.graphql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendsQuery;
import guru.qa.RecursiveFriendsQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserGraphQlTest extends BaseGraphQlTest {
    @User(
            friends = 2
    )
    @Test
    @ApiLogin
    void categoryErrorTest(@Token String bearerToken) {
        ApolloCall<FriendsQuery.Data> friendsCall = apolloClient.query(FriendsQuery.builder()
                        .page(0)
                        .size(10)
                        .searchQuery(null)
                        .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        ApolloResponse<FriendsQuery.Data> response = Rx2Apollo.single(friendsCall).blockingGet();
        List<String> errors = response.errors.stream().map(error -> {
            return error.getMessage();
        }).toList();
        assertTrue(errors.contains("Can`t query categories for another user"));
    }

    @User(
            friends = 1
    )
    @Test
    @ApiLogin
    void recursiveErrorTest(@Token String bearerToken) {
        ApolloCall<RecursiveFriendsQuery.Data> friendsCall = apolloClient.query(RecursiveFriendsQuery.builder()
                        .page(0)
                        .size(10)
                        .build())
                .addHttpHeader("authorization", "Bearer " + bearerToken);

        ApolloResponse<RecursiveFriendsQuery.Data> response = Rx2Apollo.single(friendsCall).blockingGet();
        List<String> errors = response.errors.stream().map(error -> {
            return error.getMessage();
        }).toList();
        assertTrue(errors.contains("Can`t fetch over 2 friends sub-queries"));
    }
}
