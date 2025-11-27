package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class Header {
    private final SelenideElement self = $("#root header");
    private final SelenideElement mainPageButton = self.$("a[href='/main]");
    private final SelenideElement addSpendingButton = self.$("a[href='/spending]");
    private final SelenideElement personIcon = self.$("[data-testid='PersonIcon']");
    private final SelenideElement menu = $("[role='menu']");
    private final SelenideElement profileButton = menu.$("a[href='/profile']");
    private final SelenideElement friendsButton = menu.$("a[href='/people/friends']");
    private final SelenideElement allPeopleButton = menu.$("a[href='/people/all']");
    private final SelenideElement signOutButton = menu.$(byText("Sign out"));

    public FriendsPage toFriendsPage() {
        personIcon.shouldBe(visible).click();
        friendsButton.shouldBe(visible).click();
        return new FriendsPage();
    }

    public AllPeoplePage toAllPeoplesPage() {
        personIcon.shouldBe(visible).click();
        allPeopleButton.shouldBe(visible).click();
        return new AllPeoplePage();
    }

    public ProfilePage toProfilePage() {
        personIcon.shouldBe(visible).click();
        profileButton.shouldBe(visible).click();
        return new ProfilePage();
    }

    public LoginPage signOut() {
        personIcon.shouldBe(visible).click();
        signOutButton.shouldBe(visible).click();
        return new LoginPage();
    }

    public EditSpendingPage addSpendingPage() {
        addSpendingButton.shouldBe(visible).click();
        return new EditSpendingPage();
    }

    public MainPage toMainPage() {
        mainPageButton.shouldBe(visible).click();
        return new MainPage();
    }
}