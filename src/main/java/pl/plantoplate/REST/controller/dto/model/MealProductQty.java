package pl.plantoplate.REST.controller.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.product.Product;

import java.util.Map;

@AllArgsConstructor
@Getter
public class MealProductQty {

    private Meal meal;
    private Map<Product, Float> ingredientQuantity;

}
