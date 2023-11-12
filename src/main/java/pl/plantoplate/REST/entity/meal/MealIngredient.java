package pl.plantoplate.REST.entity.meal;

import lombok.Getter;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "meal_ingredient")
public class MealIngredient {

    @EmbeddedId
    private MealIngredientId mealIngredientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mealId")
    private Meal meal;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredientId")
    private Product ingredient;

    private float qty;
}
