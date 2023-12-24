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
import pl.plantoplate.REST.repository.PantryRepository;
import pl.plantoplate.REST.repository.ProductRepository;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@DisplayName("PantryRepository Test")
@Sql("/schema-test.sql")
class PantryRepositoryTest {

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
    private PantryRepository pantryRepository;

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
        shopProductId = pantryRepository.save(shopProduct).getId();
    }

    @Test
    void injectedComponentsAreNotNull() {
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(productRepository).isNotNull();
        assertThat(pantryRepository).isNotNull();
    }


    @Test
    void shouldFindByProductAndGroup(){
        Optional<ShopProduct> productOptional = pantryRepository.findByProductAndGroup(savedProduct, savedGroup);
        assertTrue(productOptional.isPresent());
    }


    @Test
    void shouldFindAllByProductStateAndGroup(){
        List<ShopProduct> shopProductList = pantryRepository.findAllByProductStateAndGroup(productState, savedGroup);

        assertTrue(shopProductList.stream().anyMatch(p -> p.getProduct().getName().equals(productName)));
        assertEquals(1, shopProductList.size());

        List<ShopProduct> emptyShopProductList = pantryRepository.findAllByProductStateAndGroup(ProductState.BUY, savedGroup);
        assertTrue(emptyShopProductList.isEmpty());
    }




    @Test
    void shouldFindByIdAndProductStateAndGroup(){
        Optional<ShopProduct> optionalShopProduct = pantryRepository.findByIdAndProductStateAndGroup(shopProductId, productState, savedGroup);

        assertTrue(optionalShopProduct.isPresent());

        Optional<ShopProduct> optionalEmptyShopProduct = pantryRepository.findByIdAndProductStateAndGroup(shopProductId + 1L, ProductState.PANTRY, savedGroup);
        assertTrue(optionalEmptyShopProduct.isEmpty());
    }


}
