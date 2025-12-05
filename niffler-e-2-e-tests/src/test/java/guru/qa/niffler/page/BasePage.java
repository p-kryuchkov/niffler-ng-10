package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {
    protected final Header header = new Header();
    protected final SelenideElement snackbar = $(".MuiAlertMessage");

    @SuppressWarnings("unchecked")
    public T checkSnackbarText(String text) {
        snackbar.shouldBe(text(text));
        return (T) this;
    }
}
