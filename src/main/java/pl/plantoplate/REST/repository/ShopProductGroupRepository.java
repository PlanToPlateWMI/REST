package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;

@Repository
public interface ShopProductGroupRepository extends JpaRepository<ShopProductGroup, Long> {
}
