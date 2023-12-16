package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long>, JpaSpecificationExecutor<Recipe> {

    List<Recipe> findAllByCategoryTitleAndOwnerGroup(String categoryTitle, Group group);

    List<Recipe> findAllByLevelAndOwnerGroup(Level level, Group group);

    List<Recipe> findAllByCategoryTitleAndLevelAndOwnerGroup(String categoryName, Level level, Group group);

    List<Recipe> findAllByOwnerGroup(Group group);

    @Query(nativeQuery = true, value = "SELECT * FROM recipe r join group_recipe gr on gr.recipe_id = r.id where r.category_id =:categoryId and gr.group_id =:groupId ")
    List<Recipe> findAllByGroupSelectedAndCategoryId(@Param("groupId") long groupId, @Param("categoryId") long categoryId);

    @Query(nativeQuery = true, value = "SELECT * FROM recipe r join group_recipe gr on gr.recipe_id = r.id where gr.group_id =:groupId ")
    List<Recipe> findAllByGroupId(long groupId);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM group_recipe gr where gr.recipe_id =:recipeId and gr.group_id=:groupId")
    void deleteRecipeFromSelected(@Param("groupId")Long groupId, @Param("recipeId")long recipeId);
}
