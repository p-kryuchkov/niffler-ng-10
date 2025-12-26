package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.model.Bubble;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.*;

public class Statistics extends BaseComponent<Statistics> {
    public Statistics() {
        super($("#stat"));
    }

    private final ElementsCollection legendRows = self.$("#legend-container").$$("li");
    private final SelenideElement diagram = $("canvas[role='img']");
    public Statistics waitLoadingDiagram() {
        Selenide.sleep(4000);
        diagram.shouldBe(visible);
        return this;
    }

    public File screenshotDiagram() {
        return diagram.screenshot();
    }

    public Statistics checkLegendContainsCategory(String category) {
        legendRows.findBy(text(category)).should(visible);
        return this;
    }

    public Statistics checkLegendNotContainsCategory(String category) {
        legendRows.findBy(text(category)).shouldNot(visible);
        return this;
    }

    public Statistics checkBubbles(Bubble... bubbles){
        Color[] colors = new Color[bubbles.length];
        String[] texts = new String[bubbles.length];

        for (int i = 0; i < bubbles.length; i++) {
            colors[i] = bubbles[i].color();
            texts[i] = bubbles[i].text();
        }

        legendRows.should(color(colors));
        legendRows.should(texts(texts));
        return this;
    }
    public Statistics checkBubblesInAnyOrder(Bubble... bubbles){
        Color[] colors = new Color[bubbles.length];
        String[] texts = new String[bubbles.length];

        for (int i = 0; i < bubbles.length; i++) {
            colors[i] = bubbles[i].color();
            texts[i] = bubbles[i].text();
        }

        legendRows.should(colorsInAnyOrder(colors));
        legendRows.should(textsInAnyOrder(texts));
        return this;
    }

    public Statistics checkBubblesContains(Bubble... bubbles){
        Color[] colors = new Color[bubbles.length];
        String[] texts = new String[bubbles.length];

        for (int i = 0; i < bubbles.length; i++) {
            colors[i] = bubbles[i].color();
            texts[i] = bubbles[i].text();
        }

        legendRows.should(containsColors(colors));
        legendRows.should(containExactTextsCaseSensitive(texts));
        return this;
    }
}
