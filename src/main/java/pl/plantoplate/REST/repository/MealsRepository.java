package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.meal.Meal;

@Repository
public interface MealsRepository extends JpaRepository<Meal, Long> {
}
