package pl.plantoplate.REST.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.plantoplate.REST.entity.auth.Group;
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

    public List<Recipe> getAllRecipes(String categoryName) {

        log.info(String.format("get all %s recipes", categoryName));

        if (!StringUtils.hasLength(categoryName))
            return recipeRepository.findAll();
        RecipeCategory category = recipeCategoryService.findRecipeCategoryByName(categoryName);
        return recipeRepository.findAllByCategoryId(category.getId());
    }

    public List<Recipe> getSelectedByGroupRecipes(String categoryName, Group group) {

        log.info(String.format("get selected by group %d %s recipes", group.getId(), categoryName));

        if (!StringUtils.hasLength(categoryName))
            return recipeRepository.findAllByGroupId(group.getId());
        RecipeCategory category = recipeCategoryService.findRecipeCategoryByName(categoryName);
        return recipeRepository.findAllByGroupAndCategoryId(category.getId(), group.getId());
    }
}
