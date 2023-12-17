package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeIngredient;
import pl.plantoplate.REST.entity.recipe.RecipeIngredientId;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, RecipeIngredientId> {

    List<RecipeIngredient> findAllByRecipe(Recipe recipe);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "DELETE FROM recipe_ingredient r where r.ingredient_id= :product_Id")
    void deleteFromRecipes(@Param("product_Id") Long productId);
}
