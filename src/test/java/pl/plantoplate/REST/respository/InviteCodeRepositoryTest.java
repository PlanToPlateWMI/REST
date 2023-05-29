package pl.plantoplate.REST.respository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import pl.plantoplate.REST.repository.InviteCodeRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class InviteCodeRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private InviteCodeRepository inviteCodeRepository;


    @Test
    void injectedComponentsAreNotNull(){
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(inviteCodeRepository).isNotNull();
    }

    @Test
    void existsCodeByCode(){
        int code = 123456;
        assertTrue(inviteCodeRepository.existsByCode(code));
    }

}
