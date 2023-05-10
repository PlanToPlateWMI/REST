package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;

@Repository
public interface ShopProductGroupRepository extends JpaRepository<ShopProductGroup, Long> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value  = "DELETE FROM shop_product_group WHERE product_id = :product_id and group_owner_id = :group_id")
    void deleteProductByGroupIdAndProductId(@Param("product_id")long productID,@Param("group_id") Long GroupId);
}
