package pl.plantoplate.REST.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

//    @Modifying
//    @Transactional
//    @Query(value = "insert into app_user (email,username,password,role) values (:email,:username,:password,:role) ",nativeQuery = true)
//    int save(@Param("email") String email, @Param("username") String username, @Param("password")String password, @Param("role")String role);
}
