package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserDataApi {
    @GET("internal/users/current")
    Call<UserJson> current(
            @Query("username") String username
    );

    @GET("internal/users/all")
    Call<List<UserJson>> all(
            @Query("username") String username,
            @Query("searchQuery") String searchQuery
    );

    @POST("internal/users/update")
    Call<UserJson> update(@Body UserJson user);

    @GET("internal/friends/all")
    Call<List<UserJson>> getFriends(
            @Query("username") String username,
            @Query("searchQuery") String searchQuery
    );

    @DELETE("internal/friends/remove")
    Call<List<UserJson>> removeFriend(
            @Query("username") String username,
            @Query("targetUsername") String targetUsername
    );

    @POST("internal/invitations/send")
    Call<UserJson> sendInvitation(
            @Query("username") String username,
            @Query("targetUsername") String targetUsername
    );

    @POST("internal/invitations/accept")
    Call<UserJson> acceptInvitation(
            @Query("username") String username,
            @Query("targetUsername") String targetUsername
    );

    @POST("internal/invitations/decline")
    Call<UserJson> declineInvitation(
            @Query("username") String username,
            @Query("targetUsername") String targetUsername
    );
}

