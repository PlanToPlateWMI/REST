package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.Synchronization;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;

import java.util.Optional;

@Repository
public interface SynchronizationRepository extends JpaRepository<Synchronization, Long> {

    Optional<Synchronization> getSynchronizationByGroupAndProduct(Group group, Product product);

    @Transactional
    void deleteByProduct(Product product);
}
