package pl.plantoplate.REST.entity.recipe;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class RecipeIngredientId implements Serializable {

    @Column(name = "recipe_id")
    protected long recipeId;

    @Column(name = "ingredient_id")
    protected long ingredientId;

    public RecipeIngredientId() {}

    public RecipeIngredientId(long recipeId, long ingredientId) {
        this.recipeId = recipeId;
        this.ingredientId = ingredientId;
    }
}