package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.parameters.P;
import pl.plantoplate.REST.controller.dto.model.RecipeProductQty;
import pl.plantoplate.REST.controller.dto.request.CreateRecipeRequest;
import pl.plantoplate.REST.controller.dto.request.IngredientQtyRequest;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.entity.recipe.RecipeIngredient;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.DeleteNotSelected;
import pl.plantoplate.REST.exception.DuplicateObject;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;
import pl.plantoplate.REST.repository.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.in;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Recipe Service Test")
class RecipeServiceTest {

    private RecipeRepository recipeRepository;
    private RecipeCategoryService recipeCategoryService;
    private RecipeService recipeService;
    private RecipeIngredientRepository recipeIngredientRepository;
    private GroupService groupService;
    private UserService userService;
    private ProductService productService;

    @BeforeEach
    void init() {
        recipeRepository = mock(RecipeRepository.class);
        recipeCategoryService = mock(RecipeCategoryService.class);
        recipeIngredientRepository = mock(RecipeIngredientRepository.class);
        groupService = mock(GroupService.class);
        userService = mock(UserService.class);
        productService = mock(ProductService.class);
        recipeService = new RecipeService(recipeRepository, recipeCategoryService, recipeIngredientRepository, groupService, userService, productService);
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
        String email = "anonymousUser";
        long recipeId = 1L;
        Group group = new Group(1L, "name", null, null, null, null, null);
        List<Group> groupsSelectedRecipe = new ArrayList<>();
        Recipe recipe = Recipe.builder().id(recipeId).groupsSelectedRecipe(groupsSelectedRecipe).build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.ofNullable(recipe));
        when(recipeRepository.findAllByOwnerGroup(group)).thenReturn(List.of(recipe));
        when(groupService.findById(1L)).thenReturn(group);

        //when
        recipeService.addRecipeToSelectedByGroup(recipeId, group, email);

        //then
        assertThat(recipe.getGroupsSelectedRecipe().size()).isEqualTo(1);
    }

    @Test
    void shouldThrowException_RecipeNotFound_TryToAddToSelected() {

        //given
        String email = "test";
        long recipeId = 1L;
        Group group = new Group(1L, "name", null, null, null, null,null);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        //when then
        EntityNotFound exception = assertThrows(EntityNotFound.class, () -> recipeService.addRecipeToSelectedByGroup(recipeId, group, email));
        assertEquals(exception.getMessage(), "Recipe with id [" + recipeId + "] was not found.");
    }


    @Test
    void shouldThrowException_RecipeAlreadyAddedToSelected() {

        //given
        String email = "test";
        long recipeId = 1L;
        Group group = new Group(1L, "name", null, null, null, null, null);
        List<Group> groupsSelectedRecipe = new ArrayList<>(List.of(group));
        Recipe recipe = Recipe.builder().id(recipeId).groupsSelectedRecipe(groupsSelectedRecipe).build();
        when(recipeRepository.findById(recipeId)).thenReturn(java.util.Optional.ofNullable(recipe));

        //when then
        DuplicateObject exception = assertThrows(DuplicateObject.class, () -> recipeService.addRecipeToSelectedByGroup(recipeId, group, email));
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
        Group group = new Group(1L, "name", null, null, null, null, null);
        Unit productUnit = Unit.L;
        Product ingredient = new Product(productName, category, group, productUnit);
        ingredient.setId(ingredientId);
        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).ingredient(List.of(ingredient)).build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setQty(ingredientQtyInRecipe);
        recipeIngredient.setIngredient(ingredient);
        when(recipeIngredientRepository.findAllByRecipe(recipe)).thenReturn(List.of(recipeIngredient));

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

    @Test
    void shouldThrowException_RecipeNotFound_TryToDeleteSelectedRecipe(){

        //given
        long recipeId = 1L;
        Group group = new Group(1L, "name", null, null, null, null, null);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.empty());

        //when then
        EntityNotFound exception = assertThrows(EntityNotFound.class, () -> recipeService.deleteRecipeFromSelectedByGroup(recipeId, group));
        assertEquals(exception.getMessage(), "Recipe with id [" + recipeId + "] was not found.");

    }

    @Test
    void shouldThrowException_DeleteNotSelectedByGroupRecipe(){

        //given
        long recipeId = 1L;
        List<Recipe> emptyListOfRecipes = new ArrayList<>();
        Group group = new Group(1L, "name", null, null, emptyListOfRecipes, null, null);
        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).build();
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        //when then
        DeleteNotSelected exception = assertThrows(DeleteNotSelected.class, () -> recipeService.deleteRecipeFromSelectedByGroup(recipeId, group));
        assertEquals(exception.getMessage(), "Group [" + group.getId() + "] try to delete not selected by group recipe [" + recipeId + "]");

    }

    @Test
    void shouldDeleteSelectedRecipe(){

        //given
        long recipeId = 1L;
        long recipeId2 = 2L;
        Group group = new Group(1L, "name", null, null, new ArrayList<>(), null, null);
        List<Group> selectedGroups = new ArrayList<>();
        selectedGroups.add(group);

        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).groupsSelectedRecipe(selectedGroups).build();

        Recipe recipe2 = Recipe.builder().id(recipeId2).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).groupsSelectedRecipe(selectedGroups).build();

        List<Recipe> selectedRecipes = new ArrayList<>();
        selectedRecipes.add(recipe);
        selectedRecipes.add(recipe2);
        group.setSelectedRecipes(selectedRecipes);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        //when
        recipeService.deleteRecipeFromSelectedByGroup(recipeId, group);

    }

    @Test
    void shouldGetAllRecipesByCategoryNameAuthorize(){

        //given
        long groupId = 1L;
        long userGroupId = 2L;
        Group group = new Group();
        group.setId(groupId);
        Group userGroup = new Group();
        userGroup.setId(userGroupId);
        String email = "email";
        when(groupService.findById(groupId)).thenReturn(group);
        when(groupService.findById(userGroupId)).thenReturn(userGroup);
        when(userService.findGroupOfUser(email)).thenReturn(userGroup);
        String categoryName = "test";
        String level = Level.HARD.name();
        List<Recipe> recipes = List.of(new Recipe(), new Recipe());
        when(recipeRepository.findAllByCategoryTitleAndLevelAndOwnerGroup(categoryName,  Level.valueOf(level), group)).thenReturn(recipes);
        when(recipeRepository.findAllByCategoryTitleAndLevelAndOwnerGroup(categoryName,  Level.valueOf(level), userGroup)).thenReturn(recipes);

        //then
        List<Recipe> actualRecipes = recipeService.getAllRecipes(categoryName, level, email);

        //when
        assertEquals(actualRecipes.size(), recipes.size() + recipes.size());
    }


    @Test
    void shouldGetAllRecipesByLevelAuthorize(){

        //given
        long groupId = 1L;
        long userGroupId = 2L;
        Group group = new Group();
        group.setId(groupId);
        Group userGroup = new Group();
        userGroup.setId(userGroupId);
        String email = "email";
        when(groupService.findById(groupId)).thenReturn(group);
        when(groupService.findById(userGroupId)).thenReturn(userGroup);
        when(userService.findGroupOfUser(email)).thenReturn(userGroup);
        String categoryName = "";
        String level = Level.HARD.name();
        List<Recipe> recipes = List.of(new Recipe(), new Recipe());
        when(recipeRepository.findAllByLevelAndOwnerGroup(Level.valueOf(level), group)).thenReturn(recipes);
        when(recipeRepository.findAllByLevelAndOwnerGroup(Level.valueOf(level), userGroup)).thenReturn(recipes);

        //then
        List<Recipe> actualRecipes = recipeService.getAllRecipes(categoryName, level, email);

        //when
        assertEquals(actualRecipes.size(), recipes.size() + recipes.size());
    }


    @Test
    void shouldGetAllRecipesByLevelAndCategoryNameAuthorize(){

        //given
        long groupId = 1L;
        long userGroupId = 2L;
        Group group = new Group();
        group.setId(groupId);
        Group userGroup = new Group();
        userGroup.setId(userGroupId);
        String email = "email";
        when(groupService.findById(groupId)).thenReturn(group);
        when(groupService.findById(userGroupId)).thenReturn(userGroup);
        when(userService.findGroupOfUser(email)).thenReturn(userGroup);
        String categoryName = "test";
        String level = "";
        List<Recipe> recipes = List.of(new Recipe(), new Recipe());
        when(recipeRepository.findAllByCategoryTitleAndOwnerGroup(categoryName, group)).thenReturn(recipes);
        when(recipeRepository.findAllByCategoryTitleAndOwnerGroup(categoryName, userGroup)).thenReturn(recipes);

        //then
        List<Recipe> actualRecipes = recipeService.getAllRecipes(categoryName, level, email);

        //when
        assertEquals(actualRecipes.size(), recipes.size() + recipes.size());

    }

    @Test
    void shouldGetSelectedByGroupRecipes(){

        //given
        String categoryName = "";
        long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);
        List<Recipe> recipes = List.of( new Recipe());
        when(recipeRepository.findAllByGroupId(groupId)).thenReturn(recipes);

        //then
        List<Recipe> actual = recipeService.getSelectedByGroupRecipes(categoryName, group);

        //when
        assertEquals(actual.size(), recipes.size());

    }


    @Test
    void shouldGetSelectedByGroupAndCategoryNameRecipes(){

        //given
        String categoryName = "category";
        long groupId = 1L;
        long recipeCategory = 1L;
        Group group = new Group();
        group.setId(groupId);
        List<Recipe> recipes = List.of( new Recipe());
        RecipeCategory category = new RecipeCategory();
        category.setId(recipeCategory);
        when(recipeCategoryService.findRecipeCategoryByName(categoryName)).thenReturn(category);
        when(recipeRepository.findAllByGroupSelectedAndCategoryId(groupId, recipeCategory)).thenReturn(recipes);

        //then
        List<Recipe> actual = recipeService.getSelectedByGroupRecipes(categoryName, group);

        //when
        assertEquals(actual.size(), recipes.size());

    }

    @Test
    void shouldCreateRecipe(){

        Group group = new Group();
        String title = "test";
        long recipeId = 1L;
        String level = Level.HARD.name();
        int time = 20;
        String steps = "steps";
        int portions = 2;
        boolean isVege = false;
        long category = 1L;
        IngredientQtyRequest ingredientQtyRequest = new IngredientQtyRequest(1L, 20);
        List<IngredientQtyRequest> ingredients = List.of(ingredientQtyRequest);
        CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest(
                title, level, time, steps,portions, isVege, category, ingredients);
        RecipeCategory recipeCategory = new RecipeCategory();
        Product product = new Product();
        long productId = 1L;
        product.setId(productId);
        when(recipeCategoryService.findRecipeCategoryById(category)).thenReturn(recipeCategory);
        when(productService.findById(productId)).thenReturn(product);


        recipeService.createRecipe(createRecipeRequest, group);

    }


    @Test
    void shouldDeleteRecipe(){

        //given
        long recipeId = 1L;
        long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);
        Recipe recipe = new Recipe();
        recipe.setOwnerGroup(group);
        recipe.setId(recipeId);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        when(recipeIngredientRepository.findAllByRecipe(recipe)).thenReturn(recipeIngredients);

        //then
        recipeService.deleteRecipe(recipeId, group);

        //when
        verify(recipeIngredientRepository).deleteAll(recipeIngredients);
        verify(recipeRepository).deleteMealIngredientsByRecipe(recipeId);
        verify(recipeRepository).deleteMealByRecipe(recipeId);
        verify(recipeRepository).delete(recipe);

    }

}
