package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class SearchField {
    private final SelenideElement self = $("[aria-label=\"search\"]");

    public SearchField search(String query) {
        self.shouldBe(visible).val(query);
        return this;
    }

    public SearchField clearIfNotEmpty() {
        self.shouldBe(visible).clear();
        return this;
    }
}
