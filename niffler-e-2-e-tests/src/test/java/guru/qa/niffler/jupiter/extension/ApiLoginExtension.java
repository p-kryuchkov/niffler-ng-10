package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.AuthApiClient;
import guru.qa.niffler.service.SpendApiClient;
import guru.qa.niffler.service.UsersApiClient;
import jaxb.userdata.FriendshipStatus;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import java.util.Optional;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
    private final AuthApiClient authApiClient = new AuthApiClient();
    private final SpendApiClient spendApiClient = new SpendApiClient();
    private final UsersApiClient usersApiClient = new UsersApiClient();
    private static final Config CFG = Config.getInstance();
    private final boolean setupBrowser;

    private ApiLoginExtension(boolean setupBrowser) {
        this.setupBrowser = setupBrowser;
    }

    private ApiLoginExtension() {
        this.setupBrowser = true;
    }

    public static ApiLoginExtension restApiLoginExtension() {
        return new ApiLoginExtension(false);
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
                .ifPresent(apiLogin -> {
                    final UserJson userToLogin;
                    final Optional<UserJson> userFromUserExtension = UserExtension.getUser();
                    if ("".equals(apiLogin.username()) || "".equals(apiLogin.password())) {
                        if (userFromUserExtension.isEmpty()) {
                            throw new IllegalStateException("@User must be present in case that @ApiLogin Is Empty");
                        }
                        userToLogin = userFromUserExtension.get();
                    } else {
                        UserJson fakeUser = new UserJson(
                                apiLogin.username(),
                                new TestData(
                                        apiLogin.password())
                        );
                        final TestData testData = new TestData(
                                fakeUser.testData().password(),
                                usersApiClient.getFriends(fakeUser).stream().filter(friend -> FriendshipStatus.INVITE_RECEIVED.equals(friend.friendshipStatus())).toList(),
                                usersApiClient.getAll(fakeUser).stream().filter(friend -> FriendshipStatus.INVITE_SENT.equals(friend.friendshipStatus())).toList(),
                                usersApiClient.getFriends(fakeUser).stream().filter(friend -> FriendshipStatus.FRIEND.equals(friend.friendshipStatus())).toList(),
                                spendApiClient.getAllCategories(apiLogin.username(), true),
                                spendApiClient.getAllSpends(apiLogin.username(), null, null, null
                                ));
                        fakeUser = fakeUser.addTestData(testData);
                        if (userFromUserExtension.isPresent()) {
                            throw new IllegalStateException("@User must not be present in case that @ApiLogin contains username or password");
                        }
                        UserExtension.setUser(fakeUser);
                        userToLogin = fakeUser;
                    }

                    final String token = authApiClient.apiLogin(userToLogin.username(), userToLogin.testData().password());
                    setToken(token);
                    if (setupBrowser) {
                        Selenide.open(CFG.frontUrl());
                        Selenide.localStorage().setItem("id_token", getToken());
                        WebDriverRunner.getWebDriver().manage().addCookie(
                                new Cookie(
                                        "JSESSIONID",
                                        ThreadSafeCookieStore.INSTANCE.jsessionIdCookie()
                                )
                        );
                        Selenide.open(MainPage.URL, MainPage.class).checkElements();
                    }
                });
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(String.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
    }

    @Override
    public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return getToken();
    }

    public static void setToken(String token) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
    }

    public static String getToken() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
    }

    public static void setCode(String code) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
    }

    public static String getCode() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
    }

    public static Cookie getJsessionIdCookie() {
        return new Cookie(
                "JSESSIONID",
                ThreadSafeCookieStore.INSTANCE.jsessionIdCookie()
        );
    }
}
