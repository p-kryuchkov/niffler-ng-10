package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected static final Config CFG = Config.getInstance();

    protected final Header header = new Header();
    protected final SelenideElement snackbar = $(".MuiAlert-message");

    @SuppressWarnings("unchecked")
    public T checkSnackbarText(String text) {
        snackbar.shouldBe(text(text));
        return (T) this;
    }
}