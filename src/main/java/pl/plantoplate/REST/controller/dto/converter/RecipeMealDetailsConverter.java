package pl.plantoplate.REST.controller.dto.converter;

import pl.plantoplate.REST.controller.dto.model.MealProductQty;
import pl.plantoplate.REST.controller.dto.model.RecipeProductQty;
import pl.plantoplate.REST.controller.dto.response.CulinaryDetailsResponse;
import pl.plantoplate.REST.controller.dto.response.IngredientResponse;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Recipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RecipeMealDetailsConverter {

    public static CulinaryDetailsResponse convertRecipeToCulinaryDetailsResponse(RecipeProductQty recipeProductQty){

        CulinaryDetailsResponse response = new CulinaryDetailsResponse();
        Recipe recipe = recipeProductQty.getRecipe();
        response.setId(recipe.getId());
        response.setTitle(recipe.getTitle());
        response.setImage(recipe.getImage_source());
        response.setTime(recipe.getTime());
        response.setLevel(recipe.getLevel().name());
        response.setPortions(recipe.getPortions());
        response.setSteps(Arrays.stream(recipe.getSteps().split("&")).collect(Collectors.toList()));
        response.setVege(recipe.isVege());
        response.setSource(recipe.getSource());
        List<IngredientResponse> ingredients = new ArrayList<>();

        for(Map.Entry<Product, Float> productQty : recipeProductQty.getIngredientQuantity().entrySet() ){
            ingredients.add(new IngredientResponse(productQty.getKey(), productQty.getValue()));
        }

        response.setIngredients(ingredients);
        return response;
    }

    public static CulinaryDetailsResponse convertMealsToCulinaryDetailsResponse(MealProductQty mealProductQty){

        CulinaryDetailsResponse response = new CulinaryDetailsResponse();
        Recipe recipe = mealProductQty.getMeal().getRecipe();
        response.setId(mealProductQty.getMeal().getId());
        response.setTitle(recipe.getTitle());
        response.setImage(recipe.getImage_source());
        response.setTime(recipe.getTime());
        response.setLevel(recipe.getLevel().name());
        response.setPortions(mealProductQty.getMeal().getPortions());
        response.setSteps(Arrays.stream(recipe.getSteps().split("&")).collect(Collectors.toList()));
        response.setVege(recipe.isVege());
        response.setSource(recipe.getSource());
        List<IngredientResponse> ingredients = new ArrayList<>();

        for(Map.Entry<Product, Float> productQty : mealProductQty.getIngredientQuantity().entrySet() ){
            ingredients.add(new IngredientResponse(productQty.getKey(), productQty.getValue()));
        }

        response.setIngredients(ingredients);
        return response;
    }
}
