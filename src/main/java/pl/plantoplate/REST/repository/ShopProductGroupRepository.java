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
    @Query(nativeQuery = true, value  = "INSERT INTO shop_product_group(product_id, group_owner_id, amount) VALUES (:product_id, :group_id, :amount)")
    void mySafe(@Param("product_id")long productID,@Param("group_id") Long GroupId,@Param("amount") int amount);
}
