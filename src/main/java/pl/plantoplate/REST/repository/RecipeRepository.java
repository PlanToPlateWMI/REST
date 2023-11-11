package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.recipe.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

    List<Recipe> findAllByCategoryTitle(String categoryTitle);

    @Query(nativeQuery = true, value = "SELECT * FROM recipe r join group_recipe gr on gr.recipe_id = r.id where r.category_id =:categoryId and gr.group_id =:groupId ")
    List<Recipe> findAllByGroupSelectedAndCategoryId(@Param("groupId") long groupId, @Param("categoryId") long categoryId);

    @Query(nativeQuery = true, value = "SELECT * FROM recipe r join group_recipe gr on gr.recipe_id = r.id where gr.group_id =:groupId ")
    List<Recipe> findAllByGroupId(long groupId);

    @Query(nativeQuery = true, value = "SELECT ri.qty FROM recipe r join recipe_ingredient ri on r.id = ri.recipe_id where ri.recipe_id =:recipeId and ri.ingredient_id=:productId")
    Float findQtyByRecipeIdAndProductId(@Param("recipeId") long recipeId, @Param("productId") long product_id);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM group_recipe gr where gr.recipe_id =:recipeId and gr.group_id=:groupId")
    void deleteRecipeFromSelected(@Param("groupId")Long groupId, @Param("recipeId")long recipeId);
}
