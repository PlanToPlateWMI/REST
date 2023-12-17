package pl.plantoplate.REST.entity.recipe;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Setter
@Getter
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