package pl.plantoplate.REST.repository;

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

    List<ShopProduct> findAllByProductStateAndGroup(ProductState state, Group group);

    Optional<ShopProduct> findByProductAndGroup(Product product, Group group);

    Optional<ShopProduct> findByIdAndProductStateAndGroup(long pantryId, ProductState productState
    , Group group);

}
