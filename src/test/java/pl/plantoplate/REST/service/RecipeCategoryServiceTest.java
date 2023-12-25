package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.repository.RecipeCategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("RecipeCategory Service Test")
class RecipeCategoryServiceTest {

    private RecipeCategoryRepository recipeCategoryRepository;
    private RecipeCategoryService recipeCategoryService;


    @BeforeEach
    void setUp(){
        recipeCategoryRepository = mock(RecipeCategoryRepository.class);
        recipeCategoryService = new RecipeCategoryService(recipeCategoryRepository);
    }


    @Test
    void shouldFindRecipeCategoryByName(){
        //given
        String recipeCategoryTitle = "test";
        RecipeCategory recipeCategory = new RecipeCategory();
        when(recipeCategoryRepository.findByTitle(recipeCategoryTitle)).thenReturn(Optional.of(recipeCategory));

        //when then
        RecipeCategory actual = recipeCategoryService.findRecipeCategoryByName(recipeCategoryTitle);
        assertEquals(recipeCategory, actual);
    }

    @Test
    void shouldFindRecipeCategoryById(){
        //given
        long recipeCategoryId = 1L;
        RecipeCategory recipeCategory = new RecipeCategory();
        when(recipeCategoryRepository.findById(recipeCategoryId)).thenReturn(Optional.of(recipeCategory));

        //when then
        RecipeCategory actual = recipeCategoryService.findRecipeCategoryById(recipeCategoryId);
        assertEquals(recipeCategory, actual);
    }

    @Test
    void shouldFindAll(){
        //given
        RecipeCategory recipeCategory = new RecipeCategory();
        when(recipeCategoryRepository.findAll()).thenReturn(List.of(recipeCategory));

        //when then
        List<RecipeCategory> actual = recipeCategoryService.findAll();
        assertEquals(actual.size(), 1);
    }
}
