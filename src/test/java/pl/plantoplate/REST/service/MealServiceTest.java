package pl.plantoplate.REST.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.controller.dto.model.MealProductQty;
import pl.plantoplate.REST.controller.dto.request.PlanMealBasedOnRecipeRequestV1;
import pl.plantoplate.REST.controller.dto.request.PlanMealBasedOnRecipeRequestV2;
import pl.plantoplate.REST.controller.dto.response.MealOverviewResponse;
import pl.plantoplate.REST.controller.utils.MealType;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.meal.MealIngredient;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeIngredient;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.NotValidGroup;
import pl.plantoplate.REST.firebase.PushNotificationService;
import pl.plantoplate.REST.repository.MealIngredientRepository;
import pl.plantoplate.REST.repository.MealsRepository;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("Meal Service Test")
class MealServiceTest {

    private MealsRepository mealsRepository;
    private RecipeService recipeService;
    private RecipeIngredientRepository recipeIngredientRepository;
    private ProductService productService;
    private MealIngredientRepository mealIngredientRepository;
    private MealService mealService;
    private UserService userService;
    private PushNotificationService pushNotificationService;
    private ShoppingListService shoppingListService;
    private SynchronizationService synchronizationService;

    @BeforeEach
    void setUp(){
        mealsRepository = mock(MealsRepository.class);
        recipeService = mock(RecipeService.class);
        recipeIngredientRepository = mock(RecipeIngredientRepository.class);
        productService = mock(ProductService.class);
        mealIngredientRepository = mock(MealIngredientRepository.class);
        userService = mock(UserService.class);
        pushNotificationService = mock(PushNotificationService.class);
        synchronizationService = mock(SynchronizationService.class);
        shoppingListService = mock(ShoppingListService.class);
        mealService = new MealService(mealsRepository, recipeService,recipeIngredientRepository, productService, mealIngredientRepository, pushNotificationService, userService, shoppingListService, synchronizationService);
    }
//
//    @Test
//    void shouldThrowException_RecipeNotExist_PlanMeal(){
//
//        //given
//        Group group = new Group();
//        long recipeId = 99L;
//        PlanMealBasedOnRecipeRequest request = createPlanMeaRequest(MealType.BREAKFAST.name(),recipeId, LocalDate.now());
//        when(recipeService.findById(recipeId)).thenThrow(EntityNotFound.class);
//
//        //then when
//        assertThrows(EntityNotFound.class, () -> mealService.planMeal(request, group, "email"));
//
//    }
//
//    @Test
//    void shouldThrowException_MealTypeNotCorrect_PlanMeal(){
//
//        //given
//        Group group = new Group();
//        long recipeId = 99L;
//        Recipe recipe = Recipe.builder().id(recipeId).build();
//        PlanMealBasedOnRecipeRequest request = createPlanMeaRequest(MealType.BREAKFAST.name().toLowerCase(Locale.ROOT),recipeId, LocalDate.now());
//        when(recipeService.findById(recipeId)).thenReturn(recipe);
//
//        //then when
//        assertThrows(WrongRequestData.class, () -> mealService.planMeal(request, group, "email"));
//    }
//
//    @Test
//    void shouldThrowException_DateNotCorrect_PlanMeal(){
//
//        //given
//        Group group = new Group();
//        long recipeId = 99L;
//        Recipe recipe = Recipe.builder().id(recipeId).build();
//        PlanMealBasedOnRecipeRequest request = createPlanMeaRequest(MealType.BREAKFAST.name(),recipeId, LocalDate.now().minusDays(1));
//        when(recipeService.findById(recipeId)).thenReturn(recipe);
//
//        //then when
//        assertThrows(WrongRequestData.class, () -> mealService.planMeal(request, group, "email"));
//    }
//
    @Test
    void shouldGetMealOverviewByDate(){

        //given
        Group group = new Group();
        long mealId = 12L;
        int time = 60;
        String recipeTitle = "recipeTitle";
        String mealType = MealType.BREAKFAST.name();
        int year = 2022;
        int month = 11;
        int day = 20;

        Meal meal = new Meal();
        meal.setId(mealId);
        meal.setMealType(mealType);
        meal.setRecipe(Recipe.builder().time(time).title(recipeTitle).build());
        meal.setDate(LocalDate.of(year, month, day));
        group.setPlannedMeals(List.of(meal));

        //then
        List<MealOverviewResponse> responses = mealService.getMealOverviewByDate(LocalDate.of(year, month, day), group);

        //when
        Assertions.assertEquals(responses.size(), 1);
        Assertions.assertEquals(responses.get(0).getMealId(), mealId);
        Assertions.assertEquals(responses.get(0).getMealType(), mealType);
        Assertions.assertEquals(responses.get(0).getRecipeTitle(), recipeTitle);
        Assertions.assertEquals(responses.get(0).getTime(), time);

    }

    @Test
    void shouldThrowException_MealNotFound_TryToGetMealDetails(){

        //given
        Group group = new Group();
        long mealId = 1L;
        when(mealsRepository.findById(mealId)).thenReturn(Optional.empty());

        //when then
        EntityNotFound exception = assertThrows(EntityNotFound.class, () -> mealService.findMealDetailById(mealId, group));
        Assertions.assertEquals(exception.getMessage(), "Meal with id [" + mealId + "] was not found.");
    }

    @Test
    void shouldThrowException_MealNotOfGroup_TryToGetMealDetails(){

        //given
        long groupId = 1L;
        long usersGroupId = 2L;
        Group ownerGroup = new Group();
        ownerGroup.setId(groupId);
        Group usersGroup = new Group();
        usersGroup.setId(usersGroupId);
        long mealId = 1L;
        Meal meal = new Meal();
        meal.setGroup(ownerGroup);
        when(mealsRepository.findById(mealId)).thenReturn(Optional.of(meal));

        //when then
        NotValidGroup exception = assertThrows(NotValidGroup.class, () -> mealService.findMealDetailById(mealId, usersGroup));
        Assertions.assertEquals(exception.getMessage(), "Meal with id [" + mealId +"] not found in lists of meals of user's group");
    }

    @Test
    void shouldReturnMealDetails(){

        //given
        long recipeId = 2L;
        long mealId = 1L;
        long ingredientId = 3L;
        float ingredientQtyInRecipe = 20;
        int mealPortions = 10;
        String productName = "product";
        Category category = new Category();
        Group group = new Group(1L, "name", null, null, null, null, null);
        Unit productUnit = Unit.L;
        Product ingredient = new Product(productName, category, group, productUnit);
        ingredient.setId(ingredientId);
        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).ingredient(List.of(ingredient)).build();
        Meal meal = new Meal();
        meal.setGroup(group);
        meal.setRecipe(recipe);
        meal.setId(mealId);
        meal.setPortions(mealPortions);
        when(mealsRepository.findById(mealId)).thenReturn(Optional.of(meal));
        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setQty(ingredientQtyInRecipe);
        mealIngredient.setIngredient(ingredient);
        when(mealIngredientRepository.findAllByMeal(meal)).thenReturn(List.of(mealIngredient));

        // when
        MealProductQty mealProductQty = mealService.findMealDetailById(mealId, group);

        //then
        Assertions.assertEquals(mealProductQty.getMeal().getId(), mealId);
        Assertions.assertEquals(mealProductQty.getMeal().getRecipe().getId(), recipeId);
        Assertions.assertEquals(mealProductQty.getIngredientQuantity().size(), 1);
        Assertions.assertEquals(mealProductQty.getMeal().getPortions(), mealPortions);
        Assertions.assertEquals(mealProductQty.getIngredientQuantity().get(ingredient), ingredientQtyInRecipe);
    }

    @Test
    void shouldDeleteMealWithIngredients(){

        //given
        long recipeId = 2L;
        long mealId = 1L;
        long ingredientId = 3L;
        float ingredientQtyInRecipe = 20;
        String productName = "product";
        Category category = new Category();
        Group group = new Group(1L, "name", null, null, null, null, null);
        Unit productUnit = Unit.L;
        Product ingredient = new Product(productName, category, group, productUnit);
        ingredient.setId(ingredientId);
        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).ingredient(List.of(ingredient)).build();
        Meal meal = new Meal();
        meal.setGroup(group);
        meal.setRecipe(recipe);
        meal.setId(mealId);
        when(mealsRepository.findById(mealId)).thenReturn(Optional.of(meal));
        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setQty(ingredientQtyInRecipe);
        mealIngredient.setIngredient(ingredient);
        when(mealIngredientRepository.findAllByMeal(meal)).thenReturn(List.of(mealIngredient));

        //then
        mealService.deleteMealById(mealId, group);

        //when
        verify(mealsRepository).delete(meal);
    }

    @Test
    void shouldThrowException_MealNotFound(){

        //given
        long mealId = 1L;
        Meal meal = new Meal();
        meal.setId(mealId);
        when(mealsRepository.findById(mealId)).thenReturn(Optional.empty());
        Group group = new Group(1L, "name", null, null, null, null, null);

        //then
         assertThrows(EntityNotFound.class, () -> mealService.deleteMealById(mealId, group));
    }

    @Test
    void shouldThrowException_DeleteMealNotFromGroup(){

        //given
        long mealId = 1L;
        long mealGroupId = 1L;
        long usersGroupId = 2L;
        Meal meal = new Meal();
        meal.setId(mealId);
        Group group = new Group(mealGroupId, "name", null, null, null, null, null);
        meal.setGroup(group);
        when(mealsRepository.findById(mealId)).thenReturn(Optional.of(meal));
        Group usersGroup = new Group(usersGroupId, "name", null, null, null, null, null);

        //then
        assertThrows(NotValidGroup.class, () -> mealService.deleteMealById(mealId, usersGroup));
    }


    @Test
    void shouldPrepareMealPantryLessThanInMeal(){

        //given
        long mealId = 1L;
        long groupId = 1L;
        float mealQty = 20;
        float shopProductQty = 10;
        long shoProductId = 1L;
        long productId = 1L;
        Group group = new Group();
        group.setId(groupId);
        String email = "email";
        Product product = new Product();
        product.setId(productId);
        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setQty(mealQty);
        mealIngredient.setIngredient(product);
        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setId(shoProductId);
        shopProduct.setAmount(shopProductQty);
        shopProduct.setProduct(product);
        Meal meal = Meal.builder().group(group).isPrepared(false).build();
        when(mealsRepository.findById(mealId)).thenReturn(Optional.of(meal));
        when(mealIngredientRepository.findAllByMeal(meal)).thenReturn(List.of(mealIngredient));
        when(shoppingListService.getProducts(email, ProductState.PANTRY)).thenReturn(List.of(shopProduct));

        //when
        mealService.prepareMeal(mealId, group, email);

        //then
        assertTrue(meal.isPrepared());
        verify(mealsRepository).save(meal);
        verify(synchronizationService).deleteSynchronizationIngredient(group, mealIngredient);
        verify(shoppingListService).deleteProduct(shoProductId, email);
    }

    @Test
    void shouldPlanMealV1(){

        //given
        long recipeId = 1L;
        int portions = 2;
        long ingredientId = 1L;
        Unit unit = Unit.KG;
        float qtyInRecipe = 10;
        Group group = new Group();
        String email = "email";
        List<Long> ingredients = List.of(ingredientId);
        String mealType = MealType.LUNCH.name();
        LocalDate date = LocalDate.now().plusDays(1);

        PlanMealBasedOnRecipeRequestV1 recipeRequestV1 = new PlanMealBasedOnRecipeRequestV1(
                recipeId, portions, ingredients, mealType, date);
        Recipe recipe = new Recipe();
        Product product = new Product();
        product.setId(ingredientId);
        product.setUnit(unit);
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(product);
        recipeIngredient.setQty(qtyInRecipe);

        when(recipeService.findById(recipeId)).thenReturn(recipe);
        when( recipeIngredientRepository.findAllByRecipe(recipe)).thenReturn(List.of(recipeIngredient));

        //then
        mealService.planMealV1(recipeRequestV1,group, email );

        //when
        verify(mealIngredientRepository).save(any());
    }


    @Test
    void shouldPlanMealV2(){

        //given
        long recipeId = 1L;
        int portions = 2;
        long ingredientId = 1L;
        Unit unit = Unit.KG;
        float qtyInRecipe = 10;
        Group group = new Group();
        String email = "email";
        List<Long> ingredients = List.of(ingredientId);
        String mealType = MealType.LUNCH.name();
        LocalDate date = LocalDate.now().plusDays(1);

        PlanMealBasedOnRecipeRequestV2 recipeRequestV2 = new PlanMealBasedOnRecipeRequestV2(
                recipeId, portions, mealType, date, ingredients, true, true);
        Recipe recipe = new Recipe();
        Product product = new Product();
        product.setId(ingredientId);
        product.setUnit(unit);
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(product);
        recipeIngredient.setQty(qtyInRecipe);

        when(recipeService.findById(recipeId)).thenReturn(recipe);
        when( recipeIngredientRepository.findAllByRecipe(recipe)).thenReturn(List.of(recipeIngredient));

        //then
        mealService.planMealV2(recipeRequestV2,group, email );

        //when
        verify(mealIngredientRepository).save(any());
    }

}
