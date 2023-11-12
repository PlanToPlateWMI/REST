package pl.plantoplate.REST.entity.meal;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class MealIngredientId implements Serializable {

    @Column(name = "meal_id")
    protected long mealId;

    @Column(name = "ingredient_id")
    protected long ingredientId;

    public MealIngredientId() {}

    public MealIngredientId(long recipe_id, long ingredientId) {
        this.mealId = recipe_id;
        this.ingredientId = ingredientId;
    }
}