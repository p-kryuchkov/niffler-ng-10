package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import com.codeborne.selenide.ex.UIAssertionError;
import com.codeborne.selenide.impl.CollectionSource;
import org.apache.commons.lang.ArrayUtils;
import org.jspecify.annotations.Nullable;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;
import static java.lang.System.lineSeparator;

@Nonnull
public class StatConditions {
    public static WebElementCondition color(Color expectedColor) {
        return new WebElementCondition("color") {
            @Override
            public CheckResult check(Driver driver, WebElement element) {
                final String rgba = element.getCssValue("background-color");
                return new CheckResult(expectedColor.rgb.equals(rgba), rgba);
            }
        };
    }

    @Nonnull
    public static WebElementsCondition color(Color... expectedColors) {
        if (ArrayUtils.isEmpty(expectedColors)) {
            throw new IllegalArgumentException("No expected colors given");
        }
        final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();
        return new WebElementsCondition() {
            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                int expectedValuesSize = expectedColors.length;
                int actualValuesSize = elements.size();
                if (actualValuesSize != expectedValuesSize) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedValuesSize, actualValuesSize);
                    return rejected(message, elements);
                }
                boolean passed = true;
                List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < actualValuesSize; i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba);
                    }
                }
                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format("List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba);
                    return rejected(message, actualRgba);
                }
                return accepted();
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

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    @Nonnull
    public static WebElementsCondition containsColors(Color... expectedColors) {
        if (ArrayUtils.isEmpty(expectedColors)) {
            throw new IllegalArgumentException("No expected colors given");
        }
        final List<String> expectedRgbaList = Arrays.stream(expectedColors).map(c -> c.rgb).toList();
        return new WebElementsCondition() {
            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                List<String> actualRgbaList = new ArrayList<>();
                for (WebElement element : elements) {
                    actualRgbaList.add(element.getCssValue("background-color"));
                }

                boolean passed = true;
                for (String expectedRgba : expectedRgbaList) {
                    if (passed) {
                        passed = actualRgbaList.contains(expectedRgba);
                    }
                }
                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format("List colors mismatch (expected: %s, actual: %s)", expectedRgbaList.toString(), actualRgba);
                    return rejected(message, actualRgba);
                }
                return accepted();
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

            @Override
            public String toString() {
                return expectedRgbaList.toString();
            }
        };
    }

    @Nonnull
    public static WebElementsCondition colorsInAnyOrder(Color... expectedColors) {
        if (ArrayUtils.isEmpty(expectedColors)) {
            throw new IllegalArgumentException("No expected colors given");
        }
        final List<String> expectedRgbaList = Arrays.stream(expectedColors).map(c -> c.rgb).toList();
        return new WebElementsCondition() {
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                int expectedValuesSize = expectedColors.length;
                int actualValuesSize = elements.size();
                if (actualValuesSize != expectedValuesSize) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedValuesSize, actualValuesSize);
                    return rejected(message, elements);
                }
                List<String> actualRgbaList = new ArrayList<>();
                for (WebElement element : elements) {
                    actualRgbaList.add(element.getCssValue("background-color"));
                }

                boolean passed = true;
                for (String expectedRgba : expectedRgbaList) {
                    if (passed) {
                        passed = actualRgbaList.contains(expectedRgba);
                    }
                }
                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format("List colors mismatch (expected: %s, actual: %s)", expectedRgbaList.toString(), actualRgba);
                    return rejected(message, actualRgba);
                }
                return accepted();
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

            @Override
            public String toString() {
                return expectedRgbaList.toString();
            }
        };
    }
}
