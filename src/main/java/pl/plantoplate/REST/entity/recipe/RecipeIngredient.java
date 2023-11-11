package pl.plantoplate.REST.entity.recipe;

import lombok.Getter;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;

import javax.persistence.*;

@Getter
@Setter
@Entity(name = "RecipeIngredient")
@Table(name = "recipe_ingredient")
public class RecipeIngredient {

    @EmbeddedId
    private RecipeIngredientId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("ingredientId")
    private Product ingredient;

    private float qty;
}
