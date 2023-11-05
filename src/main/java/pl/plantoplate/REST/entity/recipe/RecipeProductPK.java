package pl.plantoplate.REST.entity.recipe;

import javax.persistence.Column;
import java.io.Serializable;

public class RecipeProductPK implements Serializable {

    @Column(name = "recipe_id")
    protected long recipe_id;

    @Column(name = "ingredients_id")
    protected long product_id;

    public RecipeProductPK() {}

    public RecipeProductPK(long recipe_id, long product_id) {
        this.recipe_id = recipe_id;
        this.product_id = product_id;
    }
}