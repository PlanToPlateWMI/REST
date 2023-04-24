package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.InviteCode;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
}
