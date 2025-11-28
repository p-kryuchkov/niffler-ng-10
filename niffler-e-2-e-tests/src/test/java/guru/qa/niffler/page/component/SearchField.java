package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class SearchField {
    private final SelenideElement self = $("[aria-label=\"search\"]");

    public SearchField search(String query) {
        clearIfNotEmpty();
        self.val(query).sendKeys(Keys.ENTER);
        return this;
    }

    public SearchField clearIfNotEmpty() {
        if (!self.val().isEmpty()) {
            self.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            self.sendKeys(Keys.BACK_SPACE);
        }
        return this;
    }
}
