package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.product.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    @Query(nativeQuery = true, value = "SELECT * FROM product WHERE group_created_id = :group_id")
    List<Product> findProductsByGroup(@Param("group_id") long group);
}
