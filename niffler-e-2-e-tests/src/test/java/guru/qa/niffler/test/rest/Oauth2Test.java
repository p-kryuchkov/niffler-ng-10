package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.AuthApiClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Oauth2Test {
    private final AuthApiClient authApiClient = new AuthApiClient();

    @Test
    @ApiLogin(username = "TestDefaultUser", password = "12345")
    public void oauth2Test(@Token String token, UserJson user) throws IOException {
        System.out.println(user);
        assertNotNull(token);
    }
}
