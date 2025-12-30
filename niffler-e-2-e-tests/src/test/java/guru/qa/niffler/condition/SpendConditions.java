package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang.ArrayUtils;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.lang.System.lineSeparator;

@Nonnull
public class SpendConditions {
    public static WebElementsCondition spends(SpendJson... expectedSpends) {
        if (ArrayUtils.isEmpty(expectedSpends)) {
            throw new IllegalArgumentException("No expected spends given");
        }
        final String expectedSpendsString = Arrays.stream(expectedSpends).map(Record::toString).toList().toString();
        return new WebElementsCondition() {
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                int expectedValuesSize = expectedSpends.length;
                int actualValuesSize = elements.size() - 1;
                if (actualValuesSize != expectedValuesSize) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedValuesSize, actualValuesSize);
                    return rejected(message, elements.size());
                }

                for (int i = 0; i < actualValuesSize; i++) {
                    final WebElement elementToCheck = elements.get(i + 1);
                    final SpendJson spendJsonToCheck = expectedSpends[i];
                    CheckResult result = checkSpendElement(spendJsonToCheck, elementToCheck);
                    if (result.verdict().equals(CheckResult.Verdict.REJECT)) return result;

                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedSpendsString;
            }

            @Override
            public void fail(CollectionSource collection, CheckResult lastCheckResult, @Nullable Exception cause, long timeoutMs) {
                throw new UIAssertionError(
                        lastCheckResult.message() +
                                lineSeparator() + "Actual: " + lastCheckResult.getActualValue() +
                                lineSeparator() + "Expected: " + expectedValue() +
                                (explanation == null ? "" : lineSeparator() + "Because: " + explanation) +
                                lineSeparator() + "Collection: " + collection.description(),
                        toString(), lastCheckResult.getActualValue()
                );
            }
        };
    }

    private static CheckResult checkSpendElement(SpendJson expectedSpendJson, WebElement actualSpendElement) {
        final List<WebElement> cells = actualSpendElement.findElements(By.cssSelector("td"));
        String message;
        final String categoryName = expectedSpendJson.category().name();
        DecimalFormat df = new DecimalFormat("0.#");
        final String amount = String.format("%s %s", df.format(expectedSpendJson.amount()), expectedSpendJson.currency().currencySign);
        final String description = expectedSpendJson.description();
        if (!cells.get(1).getText().equals(categoryName)) {
            message = String.format(
                    "Spend category mismatch (expected: %s, actual: %s)",
                    expectedSpendJson.category().name(), cells.get(1).getText()
            );
            return rejected(message, actualSpendElement);
        }
        if (!cells.get(2).getText().equals(amount)) {
            message = String.format(
                    "Spend amount mismatch (expected: %s, actual: %s)",
                    amount, cells.get(2).getText()
            );
            return rejected(message, actualSpendElement);
        }
        if (!cells.get(3).getText().equals(description)) {
            message = String.format(
                    "Spend description mismatch (expected: %s, actual: %s)",
                    description, cells.get(3).getText()
            );
            return rejected(message, actualSpendElement);
        }
        return accepted();
    }
}
