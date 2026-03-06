package guru.qa.niffler.api;


import guru.qa.jaxb.userdata.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-charset: utf-8"
    })
    @POST("ws")
    Call<UsersResponse> getFriendsByUserPageable(
            @Body() FriendsPageRequest friendsPageRequest
    );

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-charset: utf-8"
    })
    @POST("ws")
    Call<Void> removeFriend(
            @Body() RemoveFriendRequest removeFriendRequest
    );

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> acceptInvitation(
            @Body() AcceptInvitationRequest acceptInvitationRequest
    );

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> sendInvitation(
            @Body() SendInvitationRequest sendInvitationRequest
    );

    @Headers(value = {
            "Content-type: text/xml",
            "Accept-charset: utf-8"
    })
    @POST("ws")
    Call<UserResponse> declineInvitation(
            @Body() DeclineInvitationRequest declineInvitationRequest
    );
}

