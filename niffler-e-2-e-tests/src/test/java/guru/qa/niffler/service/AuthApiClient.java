package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;

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
        super(CFG.authUrl(), true);
        this.authApi = create(AuthApi.class);
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

    public void authorize(String codeChallenge) throws IOException {
        authApi.authorize(RESPONSE_TYPE,
                        CLIENT_ID,
                        SCOPE,
                        REDIRECT_URI,
                        codeChallenge,
                        CODE_CHALLENGE_METHOD)
                .execute();
    }

    public String login(@Nonnull String username, @Nonnull String password) throws IOException {
        return StringUtils.substringAfter(authApi.login(
                        ThreadSafeCookieStore.INSTANCE.xsrfCookie(),
                        username,
                        password
                )
                .execute().raw().request().url().toString(), "code=");
    }

    public String getToken(@Nonnull String code, String codeVerifier) throws IOException {
        return authApi.token(code, REDIRECT_URI, codeVerifier, GRANT_TYPE, CLIENT_ID).execute().body().path("id_token").asText();
    }
}
