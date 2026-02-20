package guru.qa.niffler.service;

import guru.qa.niffler.data.CategoryEntity;
import guru.qa.niffler.data.repository.CategoryRepository;
import guru.qa.niffler.ex.CategoryNotFoundException;
import guru.qa.niffler.ex.InvalidCategoryNameException;
import guru.qa.niffler.ex.TooManyCategoriesException;
import guru.qa.niffler.model.CategoryJson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Test
    void categoryNotFoundExceptionShouldBeThrown(@Mock CategoryRepository categoryRepository) {
        final String username = "not_found";
        final UUID id = UUID.randomUUID();

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.empty());

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "",
                username,
                true
        );

        CategoryNotFoundException ex = Assertions.assertThrows(
                CategoryNotFoundException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t find category by id: '" + id + "'",
                ex.getMessage()
        );
    }

    @ValueSource(strings = {"Archived", "ARCHIVED", "ArchIved"})
    @ParameterizedTest
    void categoryNameArchivedShouldBeDenied(String catName, @Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                catName,
                username,
                true
        );

        InvalidCategoryNameException ex = Assertions.assertThrows(
                InvalidCategoryNameException.class,
                () -> categoryService.update(categoryJson)
        );
        Assertions.assertEquals(
                "Can`t add category with name: '" + catName + "'",
                ex.getMessage()
        );
    }

    @Test
    void onlyTwoFieldsShouldBeUpdated(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        when(categoryRepository.findByUsernameAndId(eq(username), eq(id)))
                .thenReturn(Optional.of(
                        cat
                ));
        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CategoryService categoryService = new CategoryService(categoryRepository);

        CategoryJson categoryJson = new CategoryJson(
                id,
                "Бары",
                username,
                true
        );

        categoryService.update(categoryJson);
        ArgumentCaptor<CategoryEntity> argumentCaptor = ArgumentCaptor.forClass(CategoryEntity.class);
        verify(categoryRepository).save(argumentCaptor.capture());
        assertEquals("Бары", argumentCaptor.getValue().getName());
        assertEquals("duck", argumentCaptor.getValue().getUsername());
        assertTrue(argumentCaptor.getValue().isArchived());
        assertEquals(id, argumentCaptor.getValue().getId());
    }

    @Test
    void getAllCategoriesNotReturnArchiveCategoryIfExcludeArchivedTrue(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(true);

        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.findAllByUsernameOrderByName(eq(username))).thenReturn(List.of(cat));
        List<CategoryJson> result = categoryService.getAllCategories(username, true);
        assertTrue(result.isEmpty());
    }

    @Test
    void getAllCategoriesReturnArchiveCategoryIfExcludeArchivedFalse(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(true);

        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.findAllByUsernameOrderByName(eq(username))).thenReturn(List.of(cat));
        List<CategoryJson> result = categoryService.getAllCategories(username, false);
        assertEquals(username, result.getFirst().username());
        assertTrue(result.getFirst().archived());
    }

    @Test
    void getAllCategoriesReturnNotArchiveCategoryIfExcludeArchivedFalse(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.findAllByUsernameOrderByName(eq(username))).thenReturn(List.of(cat));
        List<CategoryJson> result = categoryService.getAllCategories(username, false);
        assertEquals(username, result.getFirst().username());
        assertFalse(result.getFirst().archived());
    }

    @Test
    void getAllCategoriesReturnNotArchiveCategoryIfExcludeArchivedTrue(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.findAllByUsernameOrderByName(eq(username))).thenReturn(List.of(cat));
        List<CategoryJson> result = categoryService.getAllCategories(username, true);
        assertEquals(username, result.getFirst().username());
        assertFalse(result.getFirst().archived());
    }

    @Test
    void updateThrowTooManyCategoriesExceptionWhenNotArchivedCategoriesMoreThenMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity archiveCat = new CategoryEntity();
        archiveCat.setId(id);
        archiveCat.setUsername(username);
        archiveCat.setName("Магазины");
        archiveCat.setArchived(true);

        final CategoryEntity unArchiveCat = new CategoryEntity();
        unArchiveCat.setId(id);
        unArchiveCat.setUsername(username);
        unArchiveCat.setName("Магазины");
        unArchiveCat.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);
        when(categoryRepository.findByUsernameAndId(eq(username), eq(id))).thenReturn(Optional.of(archiveCat));
        when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(8L);
        final TooManyCategoriesException exception = assertThrows(TooManyCategoriesException.class, () -> categoryService.update(CategoryJson.fromEntity(unArchiveCat)));
        assertEquals("Can`t unarchive category for user: '" + username + "'", exception.getMessage());
    }

    @Test
    void updateReturnUnarchiveCategoryWhenNotArchivedCategoriesLessThenMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity archiveCat = new CategoryEntity();
        archiveCat.setId(id);
        archiveCat.setUsername(username);
        archiveCat.setName("Магазины");
        archiveCat.setArchived(true);

        final CategoryEntity unArchiveCat = new CategoryEntity();
        unArchiveCat.setId(id);
        unArchiveCat.setUsername(username);
        unArchiveCat.setName("Магазины");
        unArchiveCat.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);
        when(categoryRepository.findByUsernameAndId(eq(username), eq(id))).thenReturn(Optional.of(archiveCat));
        when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(7L);
        when(categoryRepository.save(unArchiveCat)).thenReturn(unArchiveCat);
        CategoryJson result = categoryService.update(CategoryJson.fromEntity(unArchiveCat));
        verify(categoryRepository, times(1))
                .save(eq(unArchiveCat));
        assertEquals(id, result.id());
        assertFalse(result.archived());
    }

    @Test
    void updateReturnUnarchiveCategoryWhenNotArchivedCategoriesMoreThenMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity archiveCat = new CategoryEntity();
        archiveCat.setId(id);
        archiveCat.setUsername(username);
        archiveCat.setName("Магазины");
        archiveCat.setArchived(false);

        final CategoryEntity unArchiveCat = new CategoryEntity();
        unArchiveCat.setId(id);
        unArchiveCat.setUsername(username);
        unArchiveCat.setName("Продукты");
        unArchiveCat.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);
        when(categoryRepository.findByUsernameAndId(eq(username), eq(id))).thenReturn(Optional.of(archiveCat));
        when(categoryRepository.save(unArchiveCat)).thenReturn(unArchiveCat);
        CategoryJson result = categoryService.update(CategoryJson.fromEntity(unArchiveCat));
        verify(categoryRepository, times(1))
                .save(eq(unArchiveCat));
        assertEquals("Продукты", result.name());
        assertFalse(result.archived());
    }

    @Test
    void updateReturnArchiveCategoryWhenNotArchivedCategoriesMoreThenMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity firstCategory = new CategoryEntity();
        firstCategory.setId(id);
        firstCategory.setUsername(username);
        firstCategory.setName("Магазины");
        firstCategory.setArchived(true);

        final CategoryEntity secondCategory = new CategoryEntity();
        secondCategory.setId(id);
        secondCategory.setUsername(username);
        secondCategory.setName("Продукты");
        secondCategory.setArchived(true);

        CategoryService categoryService = new CategoryService(categoryRepository);
        when(categoryRepository.findByUsernameAndId(eq(username), eq(id))).thenReturn(Optional.of(firstCategory));
        when(categoryRepository.save(secondCategory)).thenReturn(secondCategory);
        CategoryJson result = categoryService.update(CategoryJson.fromEntity(secondCategory));
        verify(categoryRepository, times(1))
                .save(eq(secondCategory));
        assertEquals(id, result.id());
        assertEquals("Продукты", result.name());
        assertTrue(result.archived());
    }

    @Test
    void updateReturnUpdateArchiveCategoryWhenArchivedCategoriesMoreThenMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity notUpdateCategory = new CategoryEntity();
        notUpdateCategory.setId(id);
        notUpdateCategory.setUsername(username);
        notUpdateCategory.setName("Магазины");
        notUpdateCategory.setArchived(true);

        final CategoryEntity updateCategory = new CategoryEntity();
        updateCategory.setId(id);
        updateCategory.setUsername(username);
        updateCategory.setName("Продукты");
        updateCategory.setArchived(true);

        CategoryService categoryService = new CategoryService(categoryRepository);
        when(categoryRepository.findByUsernameAndId(eq(username), eq(id))).thenReturn(Optional.of(notUpdateCategory));
        when(categoryRepository.save(updateCategory)).thenReturn(updateCategory);
        CategoryJson result = categoryService.update(CategoryJson.fromEntity(updateCategory));
        verify(categoryRepository, times(1))
                .save(eq(updateCategory));
        assertEquals(id, result.id());
        assertEquals("Продукты", result.name());
    }

    @Test
    void saveThrowTooManyCategoriesExceptionWhenNotArchivedCategoriesMoreThenMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();

        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);
        when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(8L);

        final TooManyCategoriesException exception = assertThrows(TooManyCategoriesException.class, () -> categoryService.save(CategoryJson.fromEntity(cat)));
        assertEquals("Can`t add over than 8 categories for user: '" + username + "'", exception.getMessage());
    }

    @Test
    void saveReturnCategoryWhenNotArchivedCategoriesLessThenMaxCategoriesSize(@Mock CategoryRepository categoryRepository) {
        final String username = "duck";
        final UUID id = UUID.randomUUID();
        final CategoryEntity cat = new CategoryEntity();
        cat.setId(id);
        cat.setUsername(username);
        cat.setName("Магазины");
        cat.setArchived(false);

        CategoryService categoryService = new CategoryService(categoryRepository);

        when(categoryRepository.countByUsernameAndArchived(eq(username), eq(false))).thenReturn(7L);
        when(categoryRepository.save(any(CategoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        CategoryEntity result = categoryService.save(CategoryJson.fromEntity(cat));
        verify(categoryRepository, times(1))
                .save(any(CategoryEntity.class));
        assertEquals(username, result.getUsername());
        assertEquals("Магазины", result.getName());
    }
}