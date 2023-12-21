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

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Primary Key of Many to Many relation between Recipe {@link pl.plantoplate.REST.entity.recipe.Recipe}
 *  * and Product {@link pl.plantoplate.REST.entity.product.Product}
 */
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