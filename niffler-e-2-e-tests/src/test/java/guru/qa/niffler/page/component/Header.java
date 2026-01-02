package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;

public class Header extends BaseComponent<Header> {
    private final SelenideElement mainPageButton;
    private final SelenideElement addSpendingButton;
    private final SelenideElement personIcon;
    private final SelenideElement menu;
    private final SelenideElement profileButton;
    private final SelenideElement friendsButton;
    private final SelenideElement allPeopleButton;
    private final SelenideElement signOutButton;
    private final SelenideDriver driver;

    public Header(SelenideDriver driver) {
        super(driver, driver.$("#root header"));
        this.driver = driver;
        this.mainPageButton = self.$("a[href='/main']");
        this.addSpendingButton = self.$("a[href='/spending']");
        this.personIcon = self.$("[data-testid='PersonIcon']");
        this.menu = driver.$("[role='menu']");
        this.profileButton = menu.$("a[href='/profile']");
        this.friendsButton = menu.$("a[href='/people/friends']");
        this.allPeopleButton = menu.$("a[href='/people/all']");
        this.signOutButton = menu.$(byText("Sign out"));
    }

    public FriendsPage toFriendsPage() {
        personIcon.shouldBe(visible).click();
        friendsButton.shouldBe(visible).click();
        return new FriendsPage(driver);
    }

    public AllPeoplePage toAllPeoplesPage() {
        personIcon.shouldBe(visible).click();
        allPeopleButton.shouldBe(visible).click();
        return new AllPeoplePage(driver);
    }

    public ProfilePage toProfilePage() {
        personIcon.shouldBe(visible).click();
        profileButton.shouldBe(visible).click();
        return new ProfilePage(driver);
    }

    public LoginPage signOut() {
        personIcon.shouldBe(visible).click();
        signOutButton.shouldBe(visible).click();
        return new LoginPage(driver);
    }

    public EditSpendingPage addSpendingPage() {
        addSpendingButton.shouldBe(visible).click();
        return new EditSpendingPage(driver);
    }

    public MainPage toMainPage() {
        mainPageButton.shouldBe(visible).click();
        return new MainPage(driver);
    }
}