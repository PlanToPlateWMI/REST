package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.auth.Group;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
