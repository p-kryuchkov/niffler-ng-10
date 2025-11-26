package guru.qa.niffler.service;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserDataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;

public class UsersApiClient implements UsersClient {
    private static final Config CFG = Config.getInstance();

    private static final CookieManager cm = new CookieManager(null, CookiePolicy.ACCEPT_ALL);

    private final Retrofit authRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(
                            cm
                    ))
                    .build())
            .build();

    private final Retrofit userdataRetrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final AuthApi authApi = authRetrofit.create(AuthApi.class);
    private final UserDataApi userDataApi = userdataRetrofit.create(UserDataApi.class);

    @Override
    public UserJson createUser(String username, String password) {
        try {
            authApi.requestRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    cm.getCookieStore().getCookies()
                            .stream()
                            .filter(c -> c.getName().equals("XSRF-TOKEN"))
                            .findFirst()
                            .get()
                            .getValue()
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
        return response.body();
    }

    @Override
    public UserJson updateUser(UserJson user) {
        final Response<UserJson> response;
        try {
            response = userDataApi.update(user)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        return response.body();
    }

    @Override
    public void deleteUser(UserJson user) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
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
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
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

    @Override
    public List<UserJson> createFriends(UserJson targetUser, int count) {
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
