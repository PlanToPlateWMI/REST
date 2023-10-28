package pl.plantoplate.REST.respository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.repository.CategoryRepository;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.ProductRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("ProductRepository Test")
@Sql("/schema-test.sql")
public class ProductRepositoryTest {


    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void injectedComponentsAreNotNull(){
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(productRepository).isNotNull();
    }

    @Test
    void shouldFindProductByName(){
        String productName = "name";
        Group group = new Group();
        groupRepository.save(group);
        Category category = new Category();
        category.setCategory("category");
        categoryRepository.save(category);
        Product product = Product.builder().name(productName).category(category).createdBy(group).unit(Unit.L).build();
        productRepository.save(product);

        Optional<Product> productOptional = productRepository.findByName(productName);

        assertTrue(productOptional.isPresent());
    }


    @Test
    void shouldFindByCreatedBy(){
        String productName = "name";
        Group groupWithProduct = new Group();
        Group savedGroupWithProduct = groupRepository.save(groupWithProduct);
        Category category = new Category();
        category.setCategory("category");
        categoryRepository.save(category);
        Product product = Product.builder().name(productName).category(category).createdBy(groupWithProduct).unit(Unit.L).build();
        productRepository.save(product);

        Group groupWithoutProduct = new Group();
        Group savedGroupWithoutProduct = groupRepository.save(groupWithoutProduct);

        List<Product> productListWithProduct = productRepository.findAllByCreatedBy(savedGroupWithProduct);
        List<Product> productListEmpty = productRepository.findAllByCreatedBy(savedGroupWithoutProduct);

        assertTrue(productListWithProduct.stream().anyMatch(p -> p.getName().equals(productName)));
        assertTrue(productListEmpty.isEmpty());
    }
}
