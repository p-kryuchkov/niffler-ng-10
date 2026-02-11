package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.ex.SpendNotFoundException;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class SpendServiceTest {
    @Test
    void getSpendForUserShouldThrowExceptionInCaseThatIdIsIncorrectFormat(@Mock SpendRepository spendRepository,
                                                                          @Mock CategoryService categoryService) {
        final String incorrectId = "incorrectId";

        SpendService spendService = new SpendService(spendRepository, categoryService);
        SpendNotFoundException exception = assertThrows(SpendNotFoundException.class, () -> spendService.getSpendForUser(incorrectId, "pupu"));
        assertEquals("Can`t find spend by given id: " + incorrectId, exception.getMessage());
    }

    @Test
    void getSpendForUserShouldThrowExceptionInCaseThatSpendIsNotFoundInDb(@Mock SpendRepository spendRepository,
                                                                          @Mock CategoryService categoryService) {
        final String correctId = UUID.randomUUID().toString();
        final String correctUsername = "pupupu";

        Mockito.when(spendRepository.findByIdAndUsername(eq(UUID.fromString(correctId)), eq(correctUsername)))
                .thenReturn(Optional.empty());
        SpendService spendService = new SpendService(spendRepository, categoryService);
        SpendNotFoundException exception = assertThrows(SpendNotFoundException.class, () -> spendService.getSpendForUser(correctId, correctUsername));
        assertEquals("Can`t find spend by given id: " + correctId, exception.getMessage());
    }

    @Test
    void getSpendForUserShouldReturnCorrectJsonObject(@Mock SpendRepository spendRepository,
                                                                          @Mock CategoryService categoryService) {
        final UUID correctId = UUID.randomUUID();
        final String correctUsername = "pupupu";
        final SpendEntity spendEntity = new SpendEntity();
        final CategoryEntity categoryEntity = new CategoryEntity();

        spendEntity.setId(correctId);
        spendEntity.setUsername(correctUsername);
        spendEntity.setCurrency(CurrencyValues.USD);
        spendEntity.setAmount(150.15);
        spendEntity.setDescription("unit-test description");
        spendEntity.setSpendDate(new Date(0));

        categoryEntity.setUsername(correctUsername);
        categoryEntity.setName("unit-test category");
        categoryEntity.setArchived(false);
        categoryEntity.setId(UUID.randomUUID());
        spendEntity.setCategory(categoryEntity);

        Mockito.when(spendRepository.findByIdAndUsername(eq((correctId)), eq(correctUsername)))
                .thenReturn(Optional.of(spendEntity));
        SpendService spendService = new SpendService(spendRepository, categoryService);
        final SpendJson result = spendService.getSpendForUser(correctId.toString(), correctUsername);
        Mockito.verify(spendRepository, Mockito.times(1)).findByIdAndUsername(eq((correctId)), eq(correctUsername));
        assertEquals("unit-test description", result.description());
    }
}