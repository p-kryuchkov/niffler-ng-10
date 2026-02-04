package guru.qa.niffler.service;

import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserDataApi;
import guru.qa.niffler.api.UserDataSoapApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import jaxb.userdata.CurrentUserRequest;
import jaxb.userdata.UserResponse;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jetbrains.annotations.NotNull;
import retrofit2.Response;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

public class UserdataSoapClient extends RestClient{
public static final Config CFG = Config.getInstance();
private final UserDataSoapApi userDataSoapApi;
    public UserdataSoapClient() {
        super(CFG.userdataUrl(), SoapConverterFactory.create("niffler-userdata"), false, HttpLoggingInterceptor.Level.BODY);
        this.userDataSoapApi = create(UserDataSoapApi.class);
    }

    @Step("Get current user info using SOAP")
    public UserResponse currentUser(CurrentUserRequest request) throws IOException {
       return userDataSoapApi.currentUser(request).execute().body();
    }
}