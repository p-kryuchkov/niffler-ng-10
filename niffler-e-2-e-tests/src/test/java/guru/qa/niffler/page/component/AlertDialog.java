package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

import static com.codeborne.selenide.Condition.exist;


public class AlertDialog extends BaseComponent {
    public AlertDialog(SelenideDriver driver) {
        super(driver, driver.$("div[aria-describedby='alert-dialog-slide-description']"));
    }

    private final SelenideElement submitDeleteButton = self.$(By.xpath(".//button[contains(text(),'Delete')]"));
    private final SelenideElement submitDeclineButton = self.$(By.xpath(".//button[contains(text(),'Decline')]"));
    private final SelenideElement cancelButton = self.$(By.xpath(".//button[contains(text(),'Cancel')]"));

    @Step("Submit decline")
    public BaseComponent submitDecline() {
        submitDeclineButton.shouldBe(exist).click();
        return this;
    }

    @Step("Submit delete")
    public BaseComponent submitDelete() {
        submitDeleteButton.shouldBe(exist).click();
        return this;
    }

    @Step("Cancel")
    public BaseComponent cancel() {
        cancelButton.shouldBe(exist).click();
        return this;
    }
}
