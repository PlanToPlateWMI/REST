package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface PantryRepository extends JpaRepository<ShopProduct, Long> {

    @EntityGraph(attributePaths = {"product" })
    List<ShopProduct> findAllByProductStateAndGroup(ProductState state, Group group);

    @EntityGraph(attributePaths = {"product" })
    Optional<ShopProduct> findByProductAndGroup(Product product, Group group);

    @EntityGraph(attributePaths = {"product" })
    Optional<ShopProduct> findByIdAndProductStateAndGroup(long pantryId, ProductState productState, Group group);

}
