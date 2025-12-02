package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface UsersClient {
    @Step("Create new user: {username}")
    @Nonnull
    UserJson createUser(@Nonnull String username, @Nonnull String password);

    @Step("Update user: {user.username}")
    @Nonnull
    UserJson updateUser(@Nonnull UserJson user);

    @Step("Delete user: {user.username}")
    void deleteUser(@Nonnull UserJson user);

    @Step("Create new Income Invitations: {user.username}, {count}")
    @Nonnull
    List<UserJson> createIncomeInvitations(@Nonnull UserJson targetUser, int count);

    @Step("Create new Outcome Invitations: {user.username}, {count}")
    @Nonnull
    List<UserJson> createOutcomeInvitations(@Nonnull UserJson targetUser, int count);

    @Step("Create new Friends: {user.username}, {count}")
    @Nonnull
    List<UserJson> createFriends(@Nonnull UserJson targetUser, int count);
}
