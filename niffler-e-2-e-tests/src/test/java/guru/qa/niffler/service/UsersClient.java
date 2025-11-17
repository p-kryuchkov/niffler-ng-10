package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

public interface UsersClient {

    UserJson createUser(String username, String password);

    UserJson updateUser(UserJson user);

    void deleteUser(UserJson user);

    void createIncomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson targetUser, int count);

    void createFriends(UserJson targetUser, int count);
}
