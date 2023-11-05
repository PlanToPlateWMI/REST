package pl.plantoplate.REST.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.dto.model.RecipeProductQty;
import pl.plantoplate.REST.entity.product.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeDetailsResponse {

    private long id;
    private String title;
    private String image;
    private String source;
    private int time;
    private String level;
    private long portions;
    private List<String> steps;
    private boolean isVege;
    private List<IngredientResponse> ingredients;


    public RecipeDetailsResponse(RecipeProductQty recipe) {

        this.id = recipe.getRecipe().getId();
        this.title = recipe.getRecipe().getTitle();
        this.image = recipe.getRecipe().getImage_source();
        this.time = recipe.getRecipe().getTime();
        this.level = recipe.getRecipe().getLevel().name();
        this.portions = recipe.getRecipe().getPortions();
        this.steps = Arrays.stream(recipe.getRecipe().getSteps().split("&")).collect(Collectors.toList());
        this.isVege = recipe.getRecipe().isVege();
        this.source = recipe.getRecipe().getSource();
        this.ingredients = new ArrayList<>();

        for(Map.Entry<Product, Float> productQty : recipe.getIngredientQuantity().entrySet() ){
            ingredients.add(new IngredientResponse(productQty.getKey(), productQty.getValue()));
        }
    }
}
