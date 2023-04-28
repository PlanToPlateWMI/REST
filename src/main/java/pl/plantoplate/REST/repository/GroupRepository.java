package pl.plantoplate.REST.repository;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
