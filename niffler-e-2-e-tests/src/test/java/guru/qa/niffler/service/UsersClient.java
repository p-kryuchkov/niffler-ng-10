package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.io.IOException;
import java.util.List;

public interface UsersClient {

    UserJson createUser(String username, String password);

    UserJson updateUser(UserJson user);

    void deleteUser(UserJson user);

    List<UserJson> createIncomeInvitations(UserJson targetUser, int count);

    List<UserJson> createOutcomeInvitations(UserJson targetUser, int count);

    List<UserJson> createFriends(UserJson targetUser, int count);
}
