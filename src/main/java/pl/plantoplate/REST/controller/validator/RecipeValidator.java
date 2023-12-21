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


package pl.plantoplate.REST.controller.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.plantoplate.REST.controller.dto.request.CreateRecipeRequest;
import pl.plantoplate.REST.controller.dto.request.IngredientQtyRequest;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.RecipeIngredient;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.service.ProductService;
import pl.plantoplate.REST.service.RecipeCategoryService;

import java.util.List;

/**
 * Validator of requests for Recipe endpoint {@link pl.plantoplate.REST.controller.recipe.RecipeController}
 */
@Component
@RequiredArgsConstructor
public class RecipeValidator {

    private final RecipeCategoryService recipeCategoryService;
    private final ProductService productService;

    public void validateRecipeSortValues(String categoryName, String level) {

        if (StringUtils.hasLength(categoryName))
            recipeCategoryService.findAll().stream().
                    filter(rc -> rc.getTitle().equals(categoryName)).findFirst().orElseThrow(() -> new WrongRequestData("Wrong category name"));

        validateRecipLevel(level);
    }

    public void validateCreateRecipe(CreateRecipeRequest request, Group groupOfRequested) {

        try {
            recipeCategoryService.findRecipeCategoryById(request.getCategory());
        } catch (EntityNotFound e) {
            throw new WrongRequestData("Wrong category name");
        }
        validateRecipLevel(request.getLevel());

        request.getIngredients().forEach(r -> validateIngredientQtyRequest(r, groupOfRequested));
    }
    private void validateIngredientQtyRequest(IngredientQtyRequest request, Group groupOfRequested){

        Product product = productService.findById(request.getId());

        if(request.getQty() < 0){
            throw new WrongRequestData("Qty of " + product.getName() + " was less than zero");
        }

        long groupIdOfCreatedProduct = product.getCreatedBy().getId();
        long groupIdOfRequested = groupOfRequested.getId();
        if(groupIdOfCreatedProduct != 1L && groupIdOfCreatedProduct!= groupIdOfRequested){
            throw new WrongRequestData("Product " + product.getName() + " not created of user's group");
        }

    }

    private void validateRecipLevel(String level) {

        if (StringUtils.hasLength(level)) {
            try {
                Level.valueOf(level);
            } catch (IllegalArgumentException e) {
                throw new WrongRequestData("Wrong level");
            }
        }

    }
}
