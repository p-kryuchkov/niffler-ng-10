package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserdataSoapClient;
import jaxb.userdata.CurrentUserRequest;
import jaxb.userdata.UserResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;

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
}
