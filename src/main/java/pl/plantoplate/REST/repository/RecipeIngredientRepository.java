package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeIngredient;
import pl.plantoplate.REST.entity.recipe.RecipeIngredientId;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

    List<RecipeIngredient> findAllByRecipe(Recipe recipe);
}
