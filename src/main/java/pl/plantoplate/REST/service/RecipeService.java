package pl.plantoplate.REST.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.repository.RecipeRepository;

import java.util.List;

@Service
@Slf4j
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeCategoryService recipeCategoryService;

    public RecipeService(RecipeRepository recipeRepository, RecipeCategoryService recipeCategoryService) {
        this.recipeRepository = recipeRepository;
        this.recipeCategoryService = recipeCategoryService;
    }

    public List<Recipe> getAllRecipesByCategory(String categoryName){

        log.info("get all recipes by category");

        if(StringUtils.isEmpty(categoryName))
            return recipeRepository.findAll();
        RecipeCategory category = recipeCategoryService.findRecipeCategoryByName(categoryName);
        return recipeRepository.findAllByCategoryId(category.getId());
    }
}
