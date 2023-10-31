package pl.plantoplate.REST.respository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.repository.CategoryRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("CategoryRepository Test ")
@Sql("/schema-test.sql")
public class CategoryRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private CategoryRepository categoryRepository;


    @Test
    void injectedComponentsAreNotNull(){
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(categoryRepository).isNotNull();
    }


    @Test
    void shouldFindCategoryByName(){
        String categoryName = "Inne";
        Category category = new Category();
        category.setCategory(categoryName);
        categoryRepository.save(category);

        Optional<Category> categoryFromRepository = categoryRepository.findByCategory(categoryName);
        assertTrue(categoryFromRepository.isPresent());
    }
}
