package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.controller.utils.MealType;
import pl.plantoplate.REST.dto.Request.PlanMealBasedOnRecipeRequest;
import pl.plantoplate.REST.dto.Response.MealOverviewResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.repository.MealProductRepository;
import pl.plantoplate.REST.repository.MealsRepository;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Meal Service Test")
public class MealServiceTest {

    private MealsRepository mealsRepository;
    private RecipeService recipeService;
    private RecipeIngredientRepository recipeIngredientRepository;
    private ProductService productService;
    private MealProductRepository mealProductRepository;
    private MealService mealService;

    @BeforeEach
    void setUp(){
        mealsRepository = mock(MealsRepository.class);
        recipeService = mock(RecipeService.class);
        recipeIngredientRepository = mock(RecipeIngredientRepository.class);
        productService = mock(ProductService.class);
        mealProductRepository = mock(MealProductRepository.class);
        mealService = new MealService(mealsRepository, recipeService,recipeIngredientRepository, productService, mealProductRepository);
    }

    @Test
    void shouldThrowException_RecipeNotExist_PlanMeal(){

        //given
        Group group = new Group();
        long recipeId = 99L;
        PlanMealBasedOnRecipeRequest request = createPlanMeaRequest(MealType.BREAKFAST.name(),recipeId, LocalDate.now());
        when(recipeService.findById(recipeId)).thenThrow(EntityNotFound.class);

        //then when
        assertThrows(EntityNotFound.class, () -> mealService.planMeal(request, group));

    }

    @Test
    void shouldThrowException_MealTypeNotCorrect_PlanMeal(){

        //given
        Group group = new Group();
        long recipeId = 99L;
        Recipe recipe = Recipe.builder().id(recipeId).build();
        PlanMealBasedOnRecipeRequest request = createPlanMeaRequest(MealType.BREAKFAST.name().toLowerCase(Locale.ROOT),recipeId, LocalDate.now());
        when(recipeService.findById(recipeId)).thenReturn(recipe);

        //then when
        assertThrows(WrongRequestData.class, () -> mealService.planMeal(request, group));
    }

    @Test
    void shouldThrowException_DateNotCorrect_PlanMeal(){

        //given
        Group group = new Group();
        long recipeId = 99L;
        Recipe recipe = Recipe.builder().id(recipeId).build();
        PlanMealBasedOnRecipeRequest request = createPlanMeaRequest(MealType.BREAKFAST.name(),recipeId, LocalDate.now().minusDays(1));
        when(recipeService.findById(recipeId)).thenReturn(recipe);

        //then when
        assertThrows(WrongRequestData.class, () -> mealService.planMeal(request, group));
    }

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
        assertEquals(responses.size(), 1);
        assertEquals(responses.get(0).getMealId(), mealId);
        assertEquals(responses.get(0).getMealType(), mealType);
        assertEquals(responses.get(0).getRecipeTitle(), recipeTitle);
        assertEquals(responses.get(0).getTime(), time);

    }



    private PlanMealBasedOnRecipeRequest createPlanMeaRequest(String mealType, long recipeId, LocalDate date){
        return PlanMealBasedOnRecipeRequest.builder().mealType(mealType).recipeId(recipeId).date(date).portions(2).ingredientsId(List.of()).build();
    }

}
