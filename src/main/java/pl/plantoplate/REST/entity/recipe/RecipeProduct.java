package pl.plantoplate.REST.entity.recipe;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@IdClass(RecipeProductPK.class)
@Table(name = "recipe_ingredients")
public class RecipeProduct {

    @Id
    @Column(name = "recipe_id")
    private long recipe_id;

    @Id
    @Column(name = "ingredients_id")
    private long product_id;

    private float qty;
}
