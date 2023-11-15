package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.meal.MealIngredient;
import pl.plantoplate.REST.entity.meal.MealIngredientId;

import java.util.List;

@Repository
public interface MealIngredientRepository extends JpaRepository<MealIngredient, MealIngredientId> {

    List<MealIngredient> findAllByMeal(Meal meal);

    void deleteByMeal(Meal meal);
}
