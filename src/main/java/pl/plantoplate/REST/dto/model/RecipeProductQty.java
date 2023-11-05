package pl.plantoplate.REST.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Recipe;

import java.util.Map;

@AllArgsConstructor
@Getter
public class RecipeProductQty {

    private Recipe recipe;
    private Map<Product, Float> ingredientQuantity;

}
