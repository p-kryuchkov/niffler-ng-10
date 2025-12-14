package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.parallel.Isolated;

import java.io.IOException;
import java.util.List;

@Order(1)
public class FirstOrderedTest {
    private final UsersApiClient usersApiClient = new UsersApiClient();

    @Test
    @User
    void newUserListEmpty(UserJson user) throws IOException {
        final List<UserJson> response = usersApiClient.getAll(user);
        Assertions.assertEquals(0, response.size());
    }
}
