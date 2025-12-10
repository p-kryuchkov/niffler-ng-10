package guru.qa.niffler.page.component;

import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;

public class SearchField extends BaseComponent<SearchField> {

    public SearchField() {
        super($("[aria-label=\"search\"]"));
    }

    private SearchField clearIfNotEmpty() {
        if (!self.val().isEmpty()) {
            self.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            self.sendKeys(Keys.BACK_SPACE);
        }
        return this;
    }

    public SearchField search(String query) {
        clearIfNotEmpty();
        self.val(query).sendKeys(Keys.ENTER);
        return this;
    }
}