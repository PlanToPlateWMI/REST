package pl.plantoplate.REST.respository;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.auth.InviteCode;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.InviteCodeRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("InviteCodeRepository Test")
@Sql("/schema-test.sql")
public class InviteCodeRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private InviteCodeRepository inviteCodeRepository;
    @Autowired
    private GroupRepository groupRepository;

    private final int code = 123456;
    private InviteCode savedInviteCode;


    @Test
    void injectedComponentsAreNotNull(){
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(inviteCodeRepository).isNotNull();
        assertThat(groupRepository).isNotNull();
    }

    @BeforeEach
    void saveInviteCode(){
        InviteCode inviteCode = new InviteCode();
        inviteCode.setCode(code);
        Group group = new Group();
        groupRepository.save(group);
        inviteCode.setGroup(group);
        savedInviteCode = inviteCodeRepository.save(inviteCode);
    }

    @Test
    void shouldExistsInviteCodeByCode(){
        assertTrue(inviteCodeRepository.existsByCode(code));
    }

    @Test
    void shouldGetInviteCodeByCode(){
        InviteCode inviteCodeFromRepository = inviteCodeRepository.getByCode(code);
        assertEquals(code, inviteCodeFromRepository.getCode());
    }

}
