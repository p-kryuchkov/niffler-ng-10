package guru.qa.niffler.service;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient extends  RestClient implements SpendClient {
    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        this.spendApi = create(SpendApi.class);
    }

    @Override
    public @Nonnull SpendJson createSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.createSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());
        return requireNonNull(response.body());
    }

    @Override
    public @Nonnull SpendJson updateSpend(SpendJson spend) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteSpend(@Nonnull SpendJson spend) {
        final Response<SpendJson> response;
        try {
            spendApi.deleteSpend(spend.username(), List.of(String.valueOf(spend.id())))
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    @Override
    public @Nonnull CategoryJson createCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.addCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Override
    public void deleteCategory(@Nonnull CategoryJson category) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public Optional<CategoryJson> findCategoryByNameAndUsername(@Nonnull String categoryName,
                                                                @Nonnull String username) {
        final Response<List<CategoryJson>> response;
        try {
            response = spendApi.getAllCategories(username, false).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body()).stream()
                .filter(c -> c.name().equals(categoryName))
                .findFirst();
    }

    @Step("Find spend by username '{username}' and id '{id}'")
    public @Nullable SpendJson findSpendByUsernameAndId(String username, String id) {
        final Response<SpendJson> response;
        try {
            response = spendApi.getSpendById(username, id).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Get all spends for user '{username}' with filters: currency={filterCurrency}, from={from}, to={to}")
    @Nonnull
    public List<SpendJson> getAllSpends(String username,
                                        @Nullable CurrencyValues filterCurrency,
                                        @Nullable Date from,
                                        @Nullable Date to) {
        final Response<List<SpendJson>> response;
        try {
            response = spendApi.getAllSpends(username, filterCurrency, from, to).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body()) != null ? response.body() : Collections.emptyList();
    }

    @Step("Edit spend: {spend}")
    @Nullable
    public SpendJson editSpend(SpendJson spend) {
        final Response<SpendJson> response;
        try {
            response = spendApi.editSpend(spend).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }

    @Step("Delete spends for user '{username}' with ids={ids}")
    public void deleteSpend(String username, List<String> ids) {
        final Response<Void> response;
        try {
            response = spendApi.deleteSpend(username, ids).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    public @Nonnull CategoryJson updateCategory(@Nonnull CategoryJson category) {
        final Response<CategoryJson> response;
        try {
            response = spendApi.updateCategory(category).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return requireNonNull(response.body());
    }
}
