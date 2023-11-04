package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.exception.DuplicateObject;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Recipe Service Test")
public class RecipeServiceTest {

    private RecipeRepository recipeRepository;
    private RecipeCategoryService recipeCategoryService;
    private RecipeService recipeService;

    @BeforeEach
    void init() {
        recipeRepository = mock(RecipeRepository.class);
        recipeCategoryService = mock(RecipeCategoryService.class);
        recipeService = new RecipeService(recipeRepository, recipeCategoryService);
    }

    @Test
    void shouldThrowException_RecipeWithGivenIdNotExist() {

        //given
        long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        //when then
        EntityNotFound exception = assertThrows(EntityNotFound.class, () -> recipeService.findById(recipeId));
        assertEquals(exception.getMessage(), "Recipe with id [" + recipeId + "] was not found.");
    }

    @Test
    void shouldAddRecipeToSelected() {

        //given
        long recipeId = 1L;
        Group group = new Group(1L, "name", null, null, null);
        List<Group> groupsSelectedRecipe = new ArrayList<>();
        Recipe recipe = Recipe.builder().id(recipeId).groupsSelectedRecipe(groupsSelectedRecipe).build();
        when(recipeRepository.findById(recipeId)).thenReturn(java.util.Optional.ofNullable(recipe));

        //when
        recipeService.addRecipeToSelectedByGroup(recipeId, group);

        //then
        assertThat(recipe.getGroupsSelectedRecipe().size()).isEqualTo(1);
    }

    @Test
    void shouldThrowException_RecipeNotFound_TryToAddToSelected() {

        //given
        long recipeId = 1L;
        Group group = new Group(1L, "name", null, null, null);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        //when then
        EntityNotFound exception = assertThrows(EntityNotFound.class, () -> recipeService.addRecipeToSelectedByGroup(recipeId, group));
        assertEquals(exception.getMessage(), "Recipe with id [" + recipeId + "] was not found.");
    }


    @Test
    void shouldThrowException_RecipeAlreadyAddedToSelected() {

        //given
        long recipeId = 1L;
        Group group = new Group(1L, "name", null, null, null);
        List<Group> groupsSelectedRecipe = new ArrayList<>(List.of(group));
        Recipe recipe = Recipe.builder().id(recipeId).groupsSelectedRecipe(groupsSelectedRecipe).build();
        when(recipeRepository.findById(recipeId)).thenReturn(java.util.Optional.ofNullable(recipe));

        //when then
        DuplicateObject exception = assertThrows(DuplicateObject.class, () -> recipeService.addRecipeToSelectedByGroup(recipeId, group));
        assertEquals(exception.getMessage(), "Recipe [" + recipeId + "] was already added to selected of group [" + group.getId() + "]");
    }

}
