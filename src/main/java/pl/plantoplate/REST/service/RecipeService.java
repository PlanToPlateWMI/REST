package pl.plantoplate.REST.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pl.plantoplate.REST.dto.model.RecipeProductQty;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.exception.DuplicateObject;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.RecipeRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return recipeRepository.findAllByCategoryTitle(category.getTitle());
    }

    public List<Recipe> getSelectedByGroupRecipes(String categoryName, Group group) {

        log.info(String.format("get selected by group %d %s recipes", group.getId(), categoryName));

        if (!StringUtils.hasLength(categoryName))
            return recipeRepository.findAllByGroupId(group.getId());
        RecipeCategory category = recipeCategoryService.findRecipeCategoryByName(categoryName);
        return recipeRepository.findAllByGroupSelectedAndCategoryId(group.getId(), category.getId());
    }

    public void addRecipeToSelectedByGroup(long recipeId, Group group) {

        Recipe recipe = findById(recipeId);

        if (recipe.getGroupsSelectedRecipe().stream().anyMatch(g -> g.getId().equals(group.getId())))
            throw new DuplicateObject("Recipe [" + recipeId + "] was already added to selected of group [" + group.getId() + "]");

        recipe.addGroupSelected(group);
        recipeRepository.save(recipe);
    }

    public Recipe findById(long recipeId){

        return  recipeRepository.findById(recipeId).orElseThrow(() -> new EntityNotFound("Recipe with id [" + recipeId + "] was not found."));
    }

    public RecipeProductQty findRecipeDetailById(long recipeId){

        Recipe recipe = findById(recipeId);
        List<Product> ingredients = recipe.getIngredients();
        Map<Product, Float> ingredientQuantity = new HashMap<>();
        for(Product pr:ingredients){
            Float qty = recipeRepository.findQtyByRecipeIdAndProductId(recipeId, pr.getId());
            ingredientQuantity.put(pr, qty);
        }
        return new RecipeProductQty(recipe, ingredientQuantity);

    }
}
