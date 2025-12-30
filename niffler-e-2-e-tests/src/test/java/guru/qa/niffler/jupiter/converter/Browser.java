package guru.qa.niffler.jupiter.converter;

import com.codeborne.selenide.SelenideConfig;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Browser {
    CHROME(new SelenideConfig()
            .browser("chrome")
            .pageLoadStrategy("eager")
            .timeout(5000)),
    FIREFOX(new SelenideConfig()
            .browser("firefox")
               .pageLoadStrategy("normal")
            .timeout(5000));

    public final SelenideConfig selenideConfig;
}
