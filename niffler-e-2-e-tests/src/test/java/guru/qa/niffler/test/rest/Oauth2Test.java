package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.AuthApiClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Oauth2Test {
    private final AuthApiClient authApiClient = new AuthApiClient();

    @User
    @Test
    public void oauth2Test(UserJson user) throws IOException {
        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);

        authApiClient.authorize(codeChallenge);
        String code = authApiClient.login(user.username(), "12345");
        String token = authApiClient.getToken(code, codeVerifier);
        assertNotNull(token);
    }
}
