package pl.plantoplate.REST.respository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.auth.User;
import pl.plantoplate.REST.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("UserRepository Test")
@Sql("/schema-test.sql")
public class UserRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private UserRepository userRepository;

    private static final String email = "email@gmail.com";
    private static final String password = "password";
    private static final String username = "username";
    private User savedUser;

    @Test
    void injectedComponentsAreNotNull(){
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(userRepository).isNotNull();
    }

    @BeforeEach
    void saveUser(){
        User user = User.builder().email(email).password(password).username(username).build();
        savedUser = userRepository.save(user);
    }

    @Test
    void shouldFindUserByEmail(){
        Optional<User> userFromRepository = userRepository.findByEmail(email);
        assertTrue(userFromRepository.isPresent());
    }


    @Test
    void shouldExistsByEmail(){
        assertTrue(userRepository.existsByEmail(email));
    }


    @Test
    void shouldExistsUserByEmailAndActiveIsTrue(){
        assertFalse(userRepository.existsByEmailAndIsActiveTrue(email));
    }




}
