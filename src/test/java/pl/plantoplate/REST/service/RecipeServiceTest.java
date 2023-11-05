package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.dto.model.RecipeProductQty;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.DuplicateObject;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void shouldReturnRecipeDetails(){

        //given
        long recipeId = 1L;
        long ingredientId = 1L;
        float ingredientQtyInRecipe = 20;
        String productName = "product";
        Category category = new Category();
        Group group = new Group(1L, "name", null, null, null);
        Unit productUnit = Unit.L;
        Product ingredient = new Product(productName, category, group, productUnit);
        ingredient.setId(ingredientId);
        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).ingredients(List.of(ingredient)).build();
        when(recipeRepository.findQtyByRecipeIdAndProductId(recipeId, ingredientId)).thenReturn(ingredientQtyInRecipe);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        // when
        RecipeProductQty recipeProductQty = recipeService.findRecipeDetailById(recipeId);

        //then
        assertEquals(recipeProductQty.getRecipe().getId(), recipeId);
        assertEquals(recipeProductQty.getIngredientQuantity().size(), 1);
        assertEquals(recipeProductQty.getIngredientQuantity().get(ingredient), ingredientQtyInRecipe);
    }

    @Test
    void shouldThrowException_RecipeNotFound_TryToGetRecipeDetails(){

        //given
        long recipeId = 1L;
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        //when then
        EntityNotFound exception = assertThrows(EntityNotFound.class, () -> recipeService.findRecipeDetailById(recipeId));
        assertEquals(exception.getMessage(), "Recipe with id [" + recipeId + "] was not found.");

    }

}
