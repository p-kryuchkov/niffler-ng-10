package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static guru.qa.niffler.jupiter.extension.TestMethodContextExtension.context;

public class UserExtension implements BeforeEachCallback, ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
    public static final String DEFAULT_PASSWORD = "12345";

    private final UsersClient usersClient = new UsersDbClient();

    @Override
    public void beforeEach(ExtensionContext context) {
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
                .ifPresent(userAnno -> {
                            if ("".equals(userAnno.username())) {
                                final String username = RandomDataUtils.randomUsername();
                                final UserJson user = usersClient.createUser(username, DEFAULT_PASSWORD);
                                final List<UserJson> incomeInvitations = usersClient.createIncomeInvitations(user, userAnno.incomeInvitations());
                                final List<UserJson> outcomeInvitations = usersClient.createOutcomeInvitations(user, userAnno.outcomeInvitations());
                                final List<UserJson> friends = usersClient.createFriends(user, userAnno.friends());

                                final TestData testData = new TestData(
                                        DEFAULT_PASSWORD,
                                        incomeInvitations,
                                        outcomeInvitations,
                                        friends,
                                        new ArrayList<>(),
                                        new ArrayList<>()
                                );

                                context.getStore(NAMESPACE).put(
                                        context.getUniqueId(),
                                        user.addTestData(testData)
                                );
                            }
                        }
                        //Todo ока работает только с созданием нового юзера
                );
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
    }

    @Override
    public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws
            ParameterResolutionException {
        return getUser().orElseThrow();
    }

    public static Optional<UserJson> getUser() {
        final ExtensionContext methodContext = context();
        return Optional.ofNullable(methodContext.getStore(NAMESPACE)
                .get(methodContext.getUniqueId(), UserJson.class));
    }

    public static void setUser(UserJson userJson) {
        final ExtensionContext methodContext = context();
        methodContext.getStore(NAMESPACE).put(
                methodContext.getUniqueId(),
                userJson
        );
    }
}