package pl.plantoplate.REST.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.plantoplate.REST.controller.utils.MealType;
import pl.plantoplate.REST.dto.Request.PlanMealBasedOnRecipeRequest;
import pl.plantoplate.REST.dto.Response.MealOverviewResponse;
import pl.plantoplate.REST.dto.model.IngredientQtUnit;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.meal.MealProduct;
import pl.plantoplate.REST.entity.meal.MealProductId;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.repository.MealProductRepository;
import pl.plantoplate.REST.repository.MealsRepository;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MealService {

    private final MealsRepository mealsRepository;
    private final RecipeService recipeService;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final ProductService productService;
    private final MealProductRepository mealProductRepository;

    public void planMeal(PlanMealBasedOnRecipeRequest planMeal, Group group){

        long recipeId = planMeal.getRecipeId();

        //check date
        if(planMeal.getDate().isBefore(LocalDate.now()))
            throw new WrongRequestData("Wrong date");

        // check if meal type is correct
        try{
            MealType.valueOf(planMeal.getMealType());
        }catch (IllegalArgumentException e){
            throw new WrongRequestData("Meal types available - " + Arrays.stream(MealType.values()).map(Enum::name).collect(Collectors.toList()));
        }

        //check if recipe exists
        Recipe recipe = recipeService.findById(recipeId);

        //if ingredient list is empty - set All ingredients
        if(planMeal.getIngredientsId().size() == 0)
            planMeal.setIngredientsId(recipe.getIngredient().stream().map(ing -> ing.getId()).collect(Collectors.toList()));

        // create meal
        Meal meal = new Meal();
        meal.setMealType(planMeal.getMealType());
        meal.setPortions(planMeal.getPortions());
        meal.setDate(planMeal.getDate());
        meal.setRecipe(recipe);
        meal.setGroup(group);

        //save meal and get mealId
        mealsRepository.save(meal);
        long mealId = meal.getId();

        // ingredients (Map of ingredientId to qty/UNIT in original recipe
        Map<Long, IngredientQtUnit> ingredientIdToUnitQtyInOriginalRecipe= recipeIngredientRepository.findAllByRecipe(recipe).stream().collect(Collectors.toMap(
                r-> r.getIngredient().getId(), r -> new IngredientQtUnit(r.getQty(), r.getIngredient().getUnit())));
        // ingredient ids provided by user
        List<Long> ingredientIdsList = planMeal.getIngredientsId();

        long portionsInOriginalRecipe = recipe.getPortions();
        long portionsPlanned = planMeal.getPortions();
        float proportionIngredientQty = (float) portionsPlanned/portionsInOriginalRecipe;

        for(Long ingredientToPlanId: ingredientIdsList){
            IngredientQtUnit originalQtyUnit = ingredientIdToUnitQtyInOriginalRecipe.get(ingredientToPlanId);
            float qtyPlanned = proportionIngredientQty * originalQtyUnit.getQty();
            if(originalQtyUnit.getUnit().equals(Unit.SZT))
                qtyPlanned = (int) Math.ceil(qtyPlanned);

            MealProduct mealProduct = new MealProduct();
            mealProduct.setMeal(meal);
            mealProduct.setIngredient(productService.findById(ingredientToPlanId));
            mealProduct.setQty(qtyPlanned);
            mealProduct.setMealProductId(new MealProductId(mealId, ingredientToPlanId));
            mealProductRepository.save(mealProduct);
        }

    }

    public List<MealOverviewResponse> getMealOverviewByDate(LocalDate localDate, Group userGroup) {

        List<Meal> mealsPlannedProvidedDate = userGroup.getPlannedMeals().stream().
                filter(m -> m.getDate().isEqual(localDate)).collect(Collectors.toList());

        return mealsPlannedProvidedDate.stream().map(m -> new MealOverviewResponse(m.getId(), m.getRecipe().getTitle(), m.getRecipe().getTime(),
                m.getMealType())).collect(Collectors.toList());
    }
}
