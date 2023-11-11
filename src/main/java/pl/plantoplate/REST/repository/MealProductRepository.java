package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.meal.MealProduct;
import pl.plantoplate.REST.entity.meal.MealProductId;

@Repository
public interface MealProductRepository extends JpaRepository<MealProduct, MealProductId> {
}
