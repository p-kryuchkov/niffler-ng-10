package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import jaxb.userdata.AllUsersRequest;
import jaxb.userdata.CurrentUserRequest;
import jaxb.userdata.UserResponse;
import jaxb.userdata.UsersResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserDataSoapApi {
    @Headers(value = {
            "Content-type: text/xml",
            "Accept-charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> currentUser(
            @Body() CurrentUserRequest currentUserRequest
    );

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> allUsers(
            @Body() AllUsersRequest allUsersRequest
    );
}

