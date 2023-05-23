package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopProductRepository extends JpaRepository<ShopProduct, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value  = "DELETE FROM shop_product_group WHERE product_id = :product_id and group_owner_id = :group_id")
    void deleteProductByGroupIdAndProductId(@Param("product_id")long productID,@Param("group_id") Long GroupId);

    List<ShopProduct> findAllByIsBought(boolean bought);

    Optional<ShopProduct> findByProductAndGroup(Product product, Group group);

    List<ShopProduct> findByGroup(Group group);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value  = "SELECT * FROM shop_product_group WHERE is_bought = :is_bought and group_owner_id = :group_id")
    List<ShopProduct> findAllByIsBoughtAndGroupId(@Param("is_bought") boolean isBought, @Param("group_id") Long groupId);
}