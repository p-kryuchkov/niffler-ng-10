package guru.qa.niffler.jupiter.converter;


import com.codeborne.selenide.SelenideDriver;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;


public class BrowserDriverConverter implements ArgumentConverter {

    @Override
    public Object convert(Object source, ParameterContext context)
            throws ArgumentConversionException {
        Browser browser = (Browser) source;
       final SelenideDriver selenideDriver = new SelenideDriver(browser.selenideConfig);
        return selenideDriver;
    }
}