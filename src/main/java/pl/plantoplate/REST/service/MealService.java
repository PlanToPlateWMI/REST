package pl.plantoplate.REST.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.controller.utils.MealType;
import pl.plantoplate.REST.controller.dto.request.PlanMealBasedOnRecipeRequest;
import pl.plantoplate.REST.controller.dto.response.MealOverviewResponse;
import pl.plantoplate.REST.controller.dto.model.IngredientQtUnit;
import pl.plantoplate.REST.controller.dto.model.MealProductQty;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.meal.MealIngredient;
import pl.plantoplate.REST.entity.meal.MealIngredientId;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.NotValidGroup;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.repository.MealIngredientRepository;
import pl.plantoplate.REST.repository.MealsRepository;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
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
    private final MealIngredientRepository mealIngredientRepository;

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
            planMeal.setIngredientsId(recipe.getIngredient().stream().map(Product::getId).collect(Collectors.toList()));

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

            MealIngredient mealIngredient = new MealIngredient();
            mealIngredient.setMeal(meal);
            mealIngredient.setIngredient(productService.findById(ingredientToPlanId));
            mealIngredient.setQty(qtyPlanned);
            mealIngredient.setMealIngredientId(new MealIngredientId(mealId, ingredientToPlanId));
            mealIngredientRepository.save(mealIngredient);
        }

    }

    public List<MealOverviewResponse> getMealOverviewByDate(LocalDate localDate, Group userGroup) {

        List<Meal> mealsPlannedProvidedDate = userGroup.getPlannedMeals().stream().
                filter(m -> m.getDate().isEqual(localDate)).collect(Collectors.toList());

        return mealsPlannedProvidedDate.stream().map(m -> new MealOverviewResponse(m.getId(), m.getRecipe().getTitle(), m.getRecipe().getTime(),
                m.getMealType(), m.getRecipe().getImage_source(), m.getRecipe().isVege())).collect(Collectors.toList());
    }

    public MealProductQty findMealDetailById(long id, Group group) {

        Meal meal = findMealById(id);
        long mealGroupId = meal.getGroup().getId();
        if(mealGroupId != group.getId()){
            throw new NotValidGroup("Meal with id [" + id +"] not found in lists of meals of user's group");
        }

        Map<Product, Float> ingredientQuantity = new HashMap<>();
        List<MealIngredient> mealIngredients = mealIngredientRepository.findAllByMeal(meal);

        for(MealIngredient mealIngredient: mealIngredients){
            ingredientQuantity.put(mealIngredient.getIngredient(), mealIngredient.getQty());
        }
        return new MealProductQty(meal, ingredientQuantity);

    }

    public Meal findMealById(long mealId){
        return  mealsRepository.findById(mealId).orElseThrow(() -> new EntityNotFound("Meal with id [" + mealId + "] was not found."));
    }

    @Transactional
    public void deleteMealById(long mealId, Group group) {

        Meal meal = this.findMealById(mealId);
        if(!meal.getGroup().getId().equals(group.getId()))
            throw new NotValidGroup("Meal with id [" + mealId +"] not found in lists of meals of user's group");

        mealIngredientRepository.deleteByMeal(meal);
        mealIngredientRepository.flush();
        mealsRepository.delete(meal);
    }

}
