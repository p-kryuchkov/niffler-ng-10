package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserdataSoapClient;
import jaxb.userdata.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static jaxb.userdata.FriendshipStatus.FRIEND;
import static jaxb.userdata.FriendshipStatus.INVITE_RECEIVED;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SoapTest
public class SoapUsersTest {
    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @Test
    @User
    void currentTest(UserJson user) throws IOException {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(user.username());
        UserResponse userResponse = userdataSoapClient.currentUser(request);
        assertEquals(user.username(), userResponse.getUser().getUsername());
    }

    @Test
    @User(friends = 14)
    void friendsByUserAndPageableTest(UserJson user) throws IOException {
        FriendsPageRequest request = new FriendsPageRequest();
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(1);
        pageInfo.setSize(4);
        request.setUsername(user.username());
        request.setPageInfo(pageInfo);
        UsersResponse usersResponse = userdataSoapClient.getFriendsByUserPageable(request);

        assertEquals(4, usersResponse.getUser().size());
        assertEquals(1, usersResponse.getNumber());
        assertEquals(4, usersResponse.getTotalPages());
        assertEquals(14, usersResponse.getTotalElements());
    }

    @Test
    @User(friends = 2)
    void friendsByUserAndQueryTest(UserJson user) throws IOException {
        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(user.username());
        request.setSearchQuery(user.testData().friends().getFirst().username());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(10);
        request.setPageInfo(pageInfo);

        UsersResponse usersResponse = userdataSoapClient.getFriendsByUserPageable(request);

        assertEquals(user.testData().friends().getFirst().username(), usersResponse.getUser().getFirst().getUsername());
        assertEquals(1, usersResponse.getUser().size());
    }

    @Test
    @User(friends = 2)
    void deleteFriendTest(UserJson user) throws IOException {
        RemoveFriendRequest removeRequest = new RemoveFriendRequest();
        removeRequest.setUsername(user.username());
        removeRequest.setFriendToBeRemoved(user.testData().friends().getFirst().username());

        userdataSoapClient.removeFriend(removeRequest);

        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(user.username());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(10);
        request.setPageInfo(pageInfo);
        UsersResponse usersResponse = userdataSoapClient.getFriendsByUserPageable(request);

        assertEquals(user.testData().friends().getLast().username(), usersResponse.getUser().getFirst().getUsername());
        assertEquals(1, usersResponse.getUser().size());
    }


    @Test
    @User()
    void sendInvitationTest(UserJson user) throws IOException {
        SendInvitationRequest sendInvitationRequest = new SendInvitationRequest();
        sendInvitationRequest.setUsername(user.username());
        sendInvitationRequest.setFriendToBeRequested("TestDefaultUser");

        UserResponse userResponse = userdataSoapClient.sendInvitation(sendInvitationRequest);

        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername("TestDefaultUser");
        request.setSearchQuery(user.username());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(10);
        request.setPageInfo(pageInfo);
        UsersResponse usersResponse = userdataSoapClient.getFriendsByUserPageable(request);

        assertEquals("TestDefaultUser", userResponse.getUser().getUsername());
        assertEquals(user.username(), usersResponse.getUser().getFirst().getUsername());
        assertEquals(INVITE_RECEIVED, usersResponse.getUser().getFirst().getFriendshipStatus());
    }

    @Test
    @User(incomeInvitations = 2)
    void declineInvitationTest(UserJson user) throws IOException {
        DeclineInvitationRequest declineInvitationRequest = new DeclineInvitationRequest();
        declineInvitationRequest.setUsername(user.username());
        declineInvitationRequest.setInvitationToBeDeclined(user.testData().incomeInvitations().getFirst().username());

        UserResponse declineResponse = userdataSoapClient.declineInvitation(declineInvitationRequest);

        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(user.username());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(10);
        request.setPageInfo(pageInfo);
        UsersResponse usersResponse = userdataSoapClient.getFriendsByUserPageable(request);

        assertEquals(user.testData().incomeInvitations().getFirst().username(), declineResponse.getUser().getUsername());
        assertEquals(1, usersResponse.getUser().size());
        assertEquals(user.testData().incomeInvitations().getLast().username(), usersResponse.getUser().getFirst().getUsername());
    }

    @Test
    @User(incomeInvitations = 1)
    void acceptInvitationTest(UserJson user) throws IOException {
        AcceptInvitationRequest acceptInvitationRequest= new AcceptInvitationRequest();
        acceptInvitationRequest.setUsername(user.username());
        acceptInvitationRequest.setFriendToBeAdded(user.testData().incomeInvitations().getFirst().username());

        UserResponse acceptResponse = userdataSoapClient.acceptInvitation(acceptInvitationRequest);

        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(user.username());
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(0);
        pageInfo.setSize(10);
        request.setPageInfo(pageInfo);
        UsersResponse usersResponse = userdataSoapClient.getFriendsByUserPageable(request);

        assertEquals(user.testData().incomeInvitations().getFirst().username(), acceptResponse.getUser().getUsername());
        assertEquals(FRIEND, acceptResponse.getUser().getFriendshipStatus());
    }
}
