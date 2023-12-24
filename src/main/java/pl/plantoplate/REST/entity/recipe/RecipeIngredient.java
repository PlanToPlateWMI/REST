/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */

package pl.plantoplate.REST.entity.recipe;

import lombok.Getter;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;

import javax.persistence.*;

/**
 *  Entity that represents Many to Many relation between Recipe {@link pl.plantoplate.REST.entity.recipe.Recipe}
 *  and Product {@link pl.plantoplate.REST.entity.product.Product}
 */
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
