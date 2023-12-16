package pl.plantoplate.REST.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import pl.plantoplate.REST.controller.dto.model.RecipeProductQty;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.entity.recipe.RecipeIngredient;
import pl.plantoplate.REST.exception.DeleteNotSelected;
import pl.plantoplate.REST.exception.DuplicateObject;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;
import pl.plantoplate.REST.repository.RecipeRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@AllArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeCategoryService recipeCategoryService;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final GroupService groupService;
    private final UserService userService;

    /**
     * Get Recipes by category, level (then user authorized - return also his recipes)
     * @param categoryName
     * @param level
     * @param email
     * @return
     */
    public List<Recipe> getAllRecipes(String categoryName, String level, String email) {

        log.info(String.format("get all %s recipes", categoryName));
        Group adminGroup = groupService.findById(1L);

        // than user not authorized
        if(email.equals("anonymousUser")) {

            if (StringUtils.hasLength(categoryName) && StringUtils.hasLength(level))
                return recipeRepository.findAllByCategoryTitleAndLevelAndOwnerGroup(categoryName, Level.valueOf(level), adminGroup);

            if (StringUtils.hasLength(level))
                return recipeRepository.findAllByLevelAndOwnerGroup(Level.valueOf(level), adminGroup);

            if (StringUtils.hasLength(categoryName))
                return recipeRepository.findAllByCategoryTitleAndOwnerGroup(categoryName, adminGroup);

            return recipeRepository.findAllByOwnerGroup(adminGroup);
        }
        else{

            Group authorizedGroup = userService.findGroupOfUser(email);
            if (StringUtils.hasLength(categoryName) && StringUtils.hasLength(level))
                return Stream.of(recipeRepository.findAllByCategoryTitleAndLevelAndOwnerGroup(categoryName, Level.valueOf(level), adminGroup),
                        recipeRepository.findAllByCategoryTitleAndLevelAndOwnerGroup(categoryName, Level.valueOf(level), authorizedGroup))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

            if (StringUtils.hasLength(level))
                return Stream.of(recipeRepository.findAllByLevelAndOwnerGroup(Level.valueOf(level),adminGroup),
                        recipeRepository.findAllByLevelAndOwnerGroup(Level.valueOf(level), authorizedGroup))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

            if (StringUtils.hasLength(categoryName))
                return Stream.of(recipeRepository.findAllByCategoryTitleAndOwnerGroup(categoryName,adminGroup),
                        recipeRepository.findAllByCategoryTitleAndOwnerGroup(categoryName, authorizedGroup))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());

            return Stream.of(recipeRepository.findAllByOwnerGroup(adminGroup), recipeRepository.findAllByOwnerGroup(authorizedGroup))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
        }
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
        Map<Product, Float> ingredientQuantity = new HashMap<>();
        List<RecipeIngredient> recipeIngredients = recipeIngredientRepository.findAllByRecipe(recipe);

        for(RecipeIngredient recipeIngredient: recipeIngredients){
            ingredientQuantity.put(recipeIngredient.getIngredient(), recipeIngredient.getQty());
        }
        return new RecipeProductQty(recipe, ingredientQuantity);

    }

    @Transactional
    public void deleteRecipeFromSelectedByGroup(long recipeId, Group group) {

        Recipe recipe = findById(recipeId);

        List<Recipe> recipes = group.getSelectedRecipes();
        if(recipes.stream().noneMatch(r -> r.getId() == recipeId))
            throw new DeleteNotSelected("Group [" + group.getId() + "] try to delete not selected by group recipe [" + recipeId + "]");

        recipeRepository.deleteRecipeFromSelected(group.getId(), recipeId);

        log.info("Recipe [" + recipeId + "] was deleted from list of selected in group [" + group.getId() + "]");
    }

    public List<Recipe> getOwnedByGroupRecipe(String categoryName, Group group) {

        log.info(String.format("get woned by group %d %s recipes", group.getId(), categoryName));

        if (!StringUtils.hasLength(categoryName))
            return recipeRepository.findAllByOwnerGroup(group);
        RecipeCategory category = recipeCategoryService.findRecipeCategoryByName(categoryName);
        return recipeRepository.findAllByCategoryTitleAndOwnerGroup(categoryName, group);
    }
}
