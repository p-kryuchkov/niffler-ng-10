package guru.qa.niffler.model;

import lombok.AllArgsConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
public record TestData(String password,
                       List<UserJson> incomeInvitations,
                       List<UserJson> outcomeInvitations,
                       List<UserJson> friends,
                       List<CategoryJson> categories,
                       List<SpendJson> spendings
) {
    public TestData(@Nonnull String password) {
        this(password, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}