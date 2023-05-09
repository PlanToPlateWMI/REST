package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.plantoplate.REST.entity.auth.InviteCode;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {

    boolean existsByCode(int code);

    InviteCode getByCode(int code);
}
