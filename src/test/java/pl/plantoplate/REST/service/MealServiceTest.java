package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.controller.utils.MealType;
import pl.plantoplate.REST.dto.Request.PlanMealBasedOnRecipeRequest;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.repository.MealProductRepository;
import pl.plantoplate.REST.repository.MealsRepository;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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



    private PlanMealBasedOnRecipeRequest createPlanMeaRequest(String mealType, long recipeId, LocalDate date){
        return PlanMealBasedOnRecipeRequest.builder().mealType(mealType).recipeId(recipeId).date(date).portions(2).ingredientsId(List.of()).build();
    }

}
