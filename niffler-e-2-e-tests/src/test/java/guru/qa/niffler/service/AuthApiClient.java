package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

public class AuthApiClient extends RestClient {
    private final AuthApi authApi;
    private static final Config CFG = Config.getInstance();

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
}
