package pl.plantoplate.REST.respository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.repository.CategoryRepository;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.ProductRepository;
import pl.plantoplate.REST.repository.ShopProductRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("ShopProductRepository Test")
@Sql("/schema.sql")
public class ShopProductRepositoryTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ShopProductRepository shopProductRepository;

    private long shopProductId;
    private static String productName  = "name";
    private static ProductState productState = ProductState.PANTRY;
    private ShopProduct shopProduct;
    private Product savedProduct;
    private Group savedGroup;
    private Category category;



    @BeforeEach
    void initShopProductWithStatePantry(){
        shopProduct = new ShopProduct();
        Group group = new Group();
        savedGroup = groupRepository.save(group);
        category = new Category();
        category.setCategory("category");
        categoryRepository.save(category);
        Product product = Product.builder().name(productName).category(category).createdBy(group).unit(Unit.L).build();
        savedProduct = productRepository.save(product);
        shopProduct.setProduct(savedProduct);
        shopProduct.setGroup(savedGroup);
        shopProduct.setAmount(20f);
        shopProduct.setProductState(productState);
        shopProductId = shopProductRepository.save(shopProduct).getId();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(productRepository).isNotNull();
        assertThat(shopProductRepository).isNotNull();
    }

    @Test
    void shouldDeleteProductByGroupIdAndProductId(){
        shopProductRepository.deleteProductByGroupIdAndProductId(savedProduct.getId(), savedGroup.getId());
        List<ShopProduct> shopProductList = shopProductRepository.findByGroup(savedGroup);
        assertTrue(shopProductList.isEmpty());
    }

    @Test
    void shouldFindByProductAndProductStateAndGroup(){
        Optional<ShopProduct> shopProductOptional = shopProductRepository.findByProductAndProductStateAndGroup(savedProduct, productState, savedGroup);
        assertTrue(shopProductOptional.isPresent());
    }

    @Test
    void shouldFindByGroup(){
        List<ShopProduct> list = shopProductRepository.findByGroup(savedGroup);
        assertEquals(1, list.size());
        assertTrue(list.stream().anyMatch(p -> p.getProduct().getName().equals(productName)));
    }

    @Test
    void shouldFindAllByProductStateAndGroup(){
        List<ShopProduct> list = shopProductRepository.findAllByProductStateAndGroup(productState, savedGroup);
        assertEquals(1, list.size());
        assertTrue(list.stream().anyMatch(p -> p.getProduct().getName().equals(productName)));
    }

}
