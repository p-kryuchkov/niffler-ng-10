package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected final Header header;
    protected final SelenideElement snackbar;
    protected final SelenideDriver driver;

    protected BasePage(SelenideDriver driver) {
        this.driver = driver;
        this.header = new Header(driver);
        this.snackbar = driver.$(".MuiAlert-message");
    }

    @SuppressWarnings("unchecked")
    public T checkSnackbarText(String text) {
        snackbar.shouldBe(text(text));
        return (T) this;
    }
}