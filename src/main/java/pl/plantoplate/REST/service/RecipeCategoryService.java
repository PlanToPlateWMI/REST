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

package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.RecipeCategoryRepository;

import java.util.List;

/**
 * Service Layer of Recipe Category JPA Repository {@link pl.plantoplate.REST.repository.RecipeCategoryRepository}
 */
@Service
public class RecipeCategoryService {

    private final RecipeCategoryRepository recipeCategoryRepository;

    public RecipeCategoryService(RecipeCategoryRepository recipeCategoryRepository) {
        this.recipeCategoryRepository = recipeCategoryRepository;
    }

    /**
     * Find RecipeCategory {@link pl.plantoplate.REST.entity.recipe.RecipeCategory} based on category name
     * @param categoryName - category name
     * @return found {@link pl.plantoplate.REST.entity.recipe.RecipeCategory}
     */
    @Transactional(readOnly = true)
    public RecipeCategory findRecipeCategoryByName(String categoryName){
        return recipeCategoryRepository.findByTitle(categoryName).orElseThrow(() -> new EntityNotFound("Category [ " + categoryName
                + " ] not found."));
    }

    /**
     * Find RecipeCategory {@link pl.plantoplate.REST.entity.recipe.RecipeCategory} based on category id
     * @param categoryId - category id
     * @return found {@link pl.plantoplate.REST.entity.recipe.RecipeCategory}
     */
    @Transactional(readOnly = true)
    public RecipeCategory findRecipeCategoryById(long categoryId){
        return recipeCategoryRepository.findById(categoryId).orElseThrow(() -> new EntityNotFound("Category [ " + categoryId
                + " ] not found."));
    }

    /**
     * Find all recipe categories
     * @return list of recipe categories
     */
    @Transactional(readOnly = true)
    public List<RecipeCategory> findAll(){
        return recipeCategoryRepository.findAll();
    }


}
