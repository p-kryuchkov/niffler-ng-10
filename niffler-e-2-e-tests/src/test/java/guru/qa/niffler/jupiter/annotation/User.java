package guru.qa.niffler.jupiter.annotation;

import guru.qa.niffler.jupiter.extension.CategoryExtension;
import guru.qa.niffler.jupiter.extension.SpendingExtension;
import guru.qa.niffler.jupiter.extension.UserExtension;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@ExtendWith({CategoryExtension.class, SpendingExtension.class, UserExtension.class})
public @interface User {
    String username() default "TestDefaultUser";

    String password() default "12345";

    Category[] categories() default {@Category()};

    Spending[] spendings() default {@Spending(
            category = "Тестовая Категория",
            amount = 89900,
            currency = CurrencyValues.RUB,
            description = "Категория для теста по умолчанию"
    )};
}
