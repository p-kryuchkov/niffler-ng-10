package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import guru.qa.niffler.jupiter.annotation.meta.GrpcTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@GrpcTest
public class CurrencyGrpcTest extends BaseGrpcTest {
    @Test
    void allCurrenciesShouldReturnedTest() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
        assertEquals(4, allCurrenciesList.size());
    }

    @Test
    void allCurrenciesShouldBePresentTest() {
        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        List<String> allCurrencyValuesList = response.getAllCurrenciesList()
                .stream()
                .map(currency -> {
                    return currency.getCurrency().name();
                })
                .toList();

        assertTrue(allCurrencyValuesList.contains(guru.qa.niffler.model.CurrencyValues.EUR.name()));
        assertTrue(allCurrencyValuesList.contains(guru.qa.niffler.model.CurrencyValues.RUB.name()));
        assertTrue(allCurrencyValuesList.contains(guru.qa.niffler.model.CurrencyValues.KZT.name()));
        assertTrue(allCurrencyValuesList.contains(guru.qa.niffler.model.CurrencyValues.USD.name()));
    }


    static Stream<Arguments> currencyProvider() {
        return Stream.of(
                Arguments.of(CurrencyValues.EUR, CurrencyValues.RUB, 2.1),
                Arguments.of(CurrencyValues.USD, CurrencyValues.RUB, 0.1),
                Arguments.of(CurrencyValues.RUB, CurrencyValues.KZT, 7.7),
                Arguments.of(CurrencyValues.EUR, CurrencyValues.EUR, 10.9),
                Arguments.of(CurrencyValues.RUB, CurrencyValues.USD, 5.0)
        );
    }

    @ParameterizedTest
    @MethodSource("currencyProvider")
    void calculateCurrency(CurrencyValues spendCurrency, CurrencyValues desiredCurrency, Double amount) {
        Map<CurrencyValues, Double> currencyRatesMap = new HashMap<>();

        final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        response.getAllCurrenciesList()
                .forEach(currency -> {
                     currencyRatesMap.put(currency.getCurrency(), currency.getCurrencyRate());
                });
        double expectAmount = BigDecimal.valueOf(amount)
                .multiply(BigDecimal.valueOf(currencyRatesMap.get(spendCurrency)))
                .divide(
                        BigDecimal.valueOf(currencyRatesMap.get(desiredCurrency)),
                        2,
                        RoundingMode.HALF_UP
                ).doubleValue();

        CalculateResponse calculateResponse = blockingStub.calculateRate(CalculateRequest.newBuilder()
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .setAmount(amount)
                .build());

        assertEquals(expectAmount, calculateResponse.getCalculatedAmount());
    }
}
