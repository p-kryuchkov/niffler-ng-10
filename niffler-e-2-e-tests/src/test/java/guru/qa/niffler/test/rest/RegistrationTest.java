package guru.qa.niffler.test.rest;

import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
@Order(2)
public class RegistrationTest {

    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    void newUserShouldRegisteredByApiCall() throws IOException {
        final Response<Void> response = authApiClient.register(RandomDataUtils.randomUsername(), "12345");
        Assertions.assertEquals(201, response.code());
    }
}
