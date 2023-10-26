package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.recipe.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM recipe r join recipe_recipe_category rrp on r.id = rrp.recipe_id where rrp.category_id =:categoryId ")
    List<Recipe> findAllByCategoryId(@Param("categoryId") long categoryId);
}
