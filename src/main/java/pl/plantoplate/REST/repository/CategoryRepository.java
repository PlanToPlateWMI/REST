package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.plantoplate.REST.entity.product.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {


    Optional<Category> findByCategory(String categoryName);
}
