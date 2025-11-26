package guru.qa.niffler.model.component;

import com.codeborne.selenide.SelenideElement;

public class Calendar {
    private final SelenideElement self;

    public Calendar(SelenideElement self) {
        this.self = self;
    }
}
