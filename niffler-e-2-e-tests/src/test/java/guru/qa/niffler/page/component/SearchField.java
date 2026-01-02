package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import org.openqa.selenium.Keys;


public class SearchField extends BaseComponent<SearchField> {

    public SearchField(SelenideDriver driver) {
        super(driver, driver.$("[aria-label=\"search\"]"));
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