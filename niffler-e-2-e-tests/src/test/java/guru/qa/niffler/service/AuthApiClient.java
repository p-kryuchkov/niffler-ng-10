package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.CodeInterceptor;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import lombok.SneakyThrows;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;

public class AuthApiClient extends RestClient {
    private final AuthApi authApi;
    private static final Config CFG = Config.getInstance();


    private final String RESPONSE_TYPE = "code";
    private final String CLIENT_ID = "client";
    private final String SCOPE = "openid";
    private final String REDIRECT_URI = CFG.frontUrl() + "authorized";
    private final String CODE_CHALLENGE_METHOD = "S256";
    private final String GRANT_TYPE = "authorization_code";


    public AuthApiClient() {
        super(CFG.authUrl(), true, new CodeInterceptor());
        this.authApi = create(AuthApi.class);
    }

    @SneakyThrows
    public String apiLogin(@Nonnull String username, @Nonnull String password) {
        final String codeVerifier = generateCodeVerifier();
        final String codeChallenge = generateCodeChallenge(codeVerifier);
        authorize(codeChallenge);
        login(username, password);
        return getToken(ApiLoginExtension.getCode(), codeVerifier);
    }

    @Nonnull
    public Response<Void> register(@Nonnull String username, @Nonnull String password) throws IOException {
        authApi.requestRegisterForm().execute();
        return authApi.register(
                username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.xsrfCookie()
        ).execute();
    }

    private void authorize(String codeChallenge) throws IOException {
        authApi.authorize(RESPONSE_TYPE,
                        CLIENT_ID,
                        SCOPE,
                        REDIRECT_URI,
                        codeChallenge,
                        CODE_CHALLENGE_METHOD)
                .execute();
    }

    private void login(@Nonnull String username, @Nonnull String password) throws IOException {
        authApi.login(
                        ThreadSafeCookieStore.INSTANCE.xsrfCookie(),
                        username,
                        password
                )
                .execute();
    }

    private String getToken(@Nonnull String code, String codeVerifier) throws IOException {
        return authApi.token(code, REDIRECT_URI, codeVerifier, GRANT_TYPE, CLIENT_ID).execute().body().path("id_token").asText();
    }
}
