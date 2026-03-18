package guru.qa.niffler.controller;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.Currency;
import guru.qa.niffler.grpc.CurrencyResponse;
import guru.qa.niffler.grpc.CurrencyValues;
import guru.qa.niffler.grpc.NifflerCurrencyServiceGrpc;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.wiremock.grpc.Jetty12GrpcExtensionFactory;
import org.wiremock.grpc.dsl.WireMockGrpcService;

import java.io.File;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.wiremock.grpc.dsl.WireMockGrpc.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CurrencyControllerTest {
    @Value("${grpc.client.grpcCurrencyClient.address:NOT_SET}")
    private String grpcCurrencyAddress;
    @Autowired
    private MockMvc mockMvc;
    private final WireMockServer wm = new WireMockServer(wireMockConfig()
            .port(8092)
            .withRootDirectory("src/test/resources/wiremock")
            .extensions(new Jetty12GrpcExtensionFactory())
    );

    @BeforeEach
    void beforeEach() {
        wm.start();
    }

    @AfterEach
    void afterEach() {
        wm.shutdown();
    }

    @Test
    @WithMockUser
    void currenciesListShouldReturned() throws Exception {
        final WireMockGrpcService currencyGrpcService =
                new WireMockGrpcService(
                        new WireMock("localhost", wm.port()),
                        "guru.qa.grpc.niffler.NifflerCurrencyService"
                );

        currencyGrpcService.stubFor(
                method("GetAllCurrencies")
                        .withRequestMessage(equalToMessage(Empty.getDefaultInstance()))
                        .willReturn(
                                message(
                                        CurrencyResponse.newBuilder()
                                                .addAllCurrencies(Currency.newBuilder()
                                                        .setCurrency(CurrencyValues.RUB)
                                                        .setCurrencyRate(1.2)
                                                        .build())
                                                .addAllCurrencies(Currency.newBuilder()
                                                        .setCurrency(CurrencyValues.USD)
                                                        .setCurrencyRate(2.7)
                                                        .build())
                                                .addAllCurrencies(Currency.newBuilder()
                                                        .setCurrency(CurrencyValues.EUR)
                                                        .setCurrencyRate(9.1)
                                                        .build())
                                                .addAllCurrencies(Currency.newBuilder()
                                                        .setCurrency(CurrencyValues.KZT)
                                                        .setCurrencyRate(5.5)
                                                        .build())
                                                .build()
                                )
                        )
        );

        System.out.println("SERVICE = " + NifflerCurrencyServiceGrpc.getServiceDescriptor().getName());
        System.out.println("WM URL = " + wm.baseUrl());
        System.out.println("gRPC address = " + grpcCurrencyAddress);
        System.out.println(new File("src/test/resources/wiremock/grpc").getAbsolutePath());
        System.out.println(Arrays.toString(new File("src/test/resources/wiremock/grpc").list()));

        mockMvc.perform(get("/api/currencies/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$[0].currency").value("RUB"))
                .andExpect(jsonPath("$[0].currencyRate").value("1.2"))
                .andExpect(jsonPath("$[1].currency").value("USD"))
                .andExpect(jsonPath("$[1].currencyRate").value("2.7"))
                .andExpect(jsonPath("$[2].currency").value("EUR"))
                .andExpect(jsonPath("$[2].currencyRate").value("9.1"))
                .andExpect(jsonPath("$[3].currency").value("KZT"))
                .andExpect(jsonPath("$[3].currencyRate").value("5.5"));

    }
}