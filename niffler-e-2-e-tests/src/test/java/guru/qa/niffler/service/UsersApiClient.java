package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserDataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class UsersApiClient extends RestClient implements UsersClient {
    private final AuthApi authApi;
    private final UserDataApi userDataApi;

    public UsersApiClient() {
        super(CFG.userdataUrl());
        this.userDataApi = create(UserDataApi.class);
        this.authApi = create(AuthApi.class);
    }

    @Override
    public @Nonnull UserJson createUser(@Nonnull String username, @Nonnull String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.xsrfCookie()
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        final Response<UserJson> response;
        try {
            response = userDataApi.current(username)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return requireNonNull(response.body());
    }

    @Override
    public @Nonnull UserJson updateUser(@Nonnull UserJson user) {
        final Response<UserJson> response;
        try {
            response = userDataApi.update(user)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return requireNonNull(response.body());
    }

    @Override
    public void deleteUser(@Nonnull UserJson user) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public List<UserJson> createIncomeInvitations(@Nonnull UserJson targetUser, @Nonnull int count) {
        List<UserJson> resultList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
                resultList.add(userDataApi.sendInvitation(friend.username(), targetUser.username())
                        .execute().body());
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
        return resultList;
    }

    @Override
    public @Nonnull List<UserJson> createOutcomeInvitations(@Nonnull UserJson targetUser, int count) {
        List<UserJson> resultList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
                resultList.add(userDataApi.sendInvitation(targetUser.username(), friend.username())
                        .execute().body());
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
        return resultList;
    }

    @Nonnull
    @Override
    public List<UserJson> createFriends(@Nonnull UserJson targetUser, int count) {
        List<UserJson> resultList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            try {
                UserJson friend = createUser(RandomDataUtils.randomUsername(), "12345");
                resultList.add(userDataApi.sendInvitation(targetUser.username(), friend.username())
                        .execute().body());
                userDataApi.acceptInvitation(friend.username(), targetUser.username()).execute();
            } catch (IOException e) {
                throw new AssertionError(e);
            }
        }
        return resultList;
    }
}
