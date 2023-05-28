package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.AddTheSameProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.ModifyGeneralProduct;
import pl.plantoplate.REST.exception.NoValidProductWithAmount;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.ProductRepository;
import pl.plantoplate.REST.repository.ShopProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Product Service Test")
public class ProductServiceTest {

    private ProductRepository productRepository;
    private ShopProductRepository shopProductRepository;
    private CategoryService categoryService;
    private ProductService productService;
    private GroupRepository groupRepository;


    @BeforeEach
    void setUp(){
        productRepository = mock(ProductRepository.class);
        shopProductRepository = mock(ShopProductRepository.class);
        categoryService = mock(CategoryService.class);
        groupRepository = mock(GroupRepository.class);
        productService = new ProductService(productRepository, shopProductRepository, categoryService, groupRepository);
    }


    @Test
    void shouldSaveProduct(){
        //given
        Product product = new Product();

        //when
        productService.save(product);

        //then
        verify(productRepository).save(product);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void shouldFindProductByName() throws EntityNotFound {
        //given
        String productName = "Mleko";
        Product product = new Product();
        product.setName(productName);

        when(productRepository.findByName(productName)).thenReturn(java.util.Optional.of(product));

        //when
        productService.findByName(productName);

        //then
        verify(productRepository).findByName(productName);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void shouldThrowExceptionWhenProductNotExistsByName() {
        //given
        String productName = "Mleko";
        when(productRepository.findByName(productName)).thenReturn(Optional.empty());

        //when
        assertThrows(EntityNotFound.class,() -> productService.findByName(productName));

        //then
        verify(productRepository).findByName(productName);
        verifyNoMoreInteractions(productRepository);
    }


    @Test
    void shouldFindProductById() throws EntityNotFound {
        //given
        long productId = 1L;
        Product product = new Product();
        product.setId(productId);

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(product));

        //when
        productService.findById(productId);

        //then
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void shouldThrowExceptionWhenProductNotExistsById() {
        //given
        long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        //when
        assertThrows(EntityNotFound.class,() -> productService.findById(productId));

        //then
        verify(productRepository).findById(productId);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void shouldFindProductOfGroup(){
        //given
        long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);

        //when
        productService.getProductsOfGroup(group);

        verify(productRepository).findAllByCreatedBy(group);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void shouldReturnProductOfGroupAndGeneralProducts(){
        //given
        List<Product> general = new ArrayList<>();
        List<Product> group = new ArrayList<>();

        long generalGroupId = 1L;
        long groupId = 2L;

        Group userGroup = new Group();
        userGroup.setId(groupId);

        Group generalGroup = new Group();
        generalGroup.setId(generalGroupId);

        when(productRepository.findAllByCreatedBy(userGroup)).thenReturn(group);

        //when
        List<Product> allProducts = productService.generalAndProductsOfGroup(userGroup);
        assertEquals( Stream.concat(general.stream(),
                group.stream()).collect(Collectors.toList()), allProducts);

        verify(productRepository).findAllByCreatedBy(userGroup);
    }


    @Test
    void shouldThrowExceptionWhenUserTryToDeleteGeneralProduct(){

        //given
        long productId = 20L;
        long groupId = 2L;
        long generalGroupId = 1L;

        Group group = new Group();
        group.setId(generalGroupId);

        Product product = new Product();
        product.setCreatedBy(group);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //when / then
        assertThrows(Exception.class, () ->  productService.deleteById(productId, groupId));

    }


    @Test
    void shouldThrowExceptionWhenUserTryToDeleteProductNotFromHisGroup(){

        long productId = 20L;
        long groupId = 2L;

        Group group = new Group();
        group.setId(groupId + 1L);
        Product product = new Product();
        product.setCreatedBy(group);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(Exception.class, () ->  productService.deleteById(productId, groupId));
    }


    @Test
    void shouldDeleteGroupProduct() throws EntityNotFound, ModifyGeneralProduct {

        //given
        long productId = 20L;
        long groupId = 2L;

        Group group = new Group();
        group.setId(groupId);
        Product product = new Product();
        product.setCreatedBy(group);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //when
        productService.deleteById(productId, groupId);


        //then
        verify(shopProductRepository).deleteProductByGroupIdAndProductId(productId, groupId);
        verify(productRepository).deleteById(productId);
    }

    @Test
    void shouldThrowExceptionWHenUnitIsWrong(){

        //given
        String wrongUnit = "WrongUnit";
        String name = "Mleko";
        String categoryName = "Inne";
        Group group = new Group();

        //when
        assertThrows(Exception.class, () -> productService.save(name, categoryName, wrongUnit, group));
    }


    @Test
    void shouldThrowExceptionWhenUserTryToAddTheSameProduct(){

        //given
        String unit = Unit.L.name();
        String name = "Mleko";
        String categoryName = "Inne";
        Group group = new Group();
        group.setId(2L);

        Product product = new Product();
        product.setUnit(Unit.valueOf(unit));
        product.setName(name);

        when(productRepository.findAllByCreatedBy(group)).thenReturn(List.of(product));

        //when
        assertThrows(Exception.class,() -> productService.save(name, categoryName, unit, group));
    }


    @Test
    void shouldAddNewProduct() throws EntityNotFound, AddTheSameProduct, NoValidProductWithAmount {

        //given
        String unit = Unit.L.name();
        String name = "Mleko";
        String categoryName = "Inne";

        Category category = new Category();
        category.setCategory(categoryName);

        Group group = new Group();
        group.setId(2L);

        Product product = new Product();
        product.setUnit(Unit.valueOf(unit));
        product.setName(name);

        when(productRepository.findAllByCreatedBy(group)).thenReturn(new ArrayList<>());
        when(categoryService.findByName(name)).thenReturn(category);

        //when
        productService.save(name, categoryName, unit, group);


        //then
        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(argumentCaptor.capture());
        Product productSaved = argumentCaptor.getValue();

        assertEquals(name, productSaved.getName());
        assertEquals(unit, productSaved.getUnit().name());
    }

    @Test
    void shouldThrowExceptionWhenUserTryToUpdateGeneralProductOrUpdateProductNotFromHisGroup(){

        //given
        long productId = 20L;
        long generalGroupId = 1L;
        long notProductGroupId = 2L;

        String name = "name";
        String unit = Unit.L.name();
        String categoryName = "Inne";

        Group group = new Group();
        group.setId(generalGroupId);

        Group notProductGroup = new Group();
        notProductGroup.setId(notProductGroupId);

        Category category = new Category();
        category.setCategory(categoryName);

        Product product = new Product();
        product.setId(productId);
        product.setName(name);
        product.setCategory(category);
        product.setCreatedBy(group);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        //when / then
        assertThrows(Exception.class, () ->  productService.updateProduct (name, unit, categoryName, notProductGroup, productId));
    }


    @Test
    void shouldThrowExceptionWhenUserSentIncorrectUnit(){
        String name = "name";
        String unit = "not unit";
        String category = "Inne";

        long groupId = 2L;
        Group group = new Group();
        group.setId(groupId);

        long productID = 2L;

        assertThrows(Exception.class, () ->  productService.updateProduct(name, unit, category, group, productID));
    }

    @ParameterizedTest
    @CsvSource({"Muller, KG, Inne","Muller, , Inne",", KG, Inne"})
    void shouldUpdateProduct(String updateName, String updateUnit, String updateCategoryName) throws EntityNotFound, ModifyGeneralProduct, AddTheSameProduct, NoValidProductWithAmount {

        //given
        long productId = 20L;
        long productGroupId = 2L;

        Group group = new Group();
        group.setId(productGroupId);

        String categoryName = "Inne";
        Category category = new Category();
        category.setCategory(categoryName);

        String productName = "Mleko";
        String productUnit = Unit.L.name();

        Product product = new Product();
        product.setUnit(Unit.valueOf(productUnit));
        product.setId(productId);
        product.setName(productName);
        product.setCategory(category);
        product.setCreatedBy(group);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.findAllByCreatedBy(group)).thenReturn(List.of(product));
        when(categoryService.findByName(updateCategoryName)).thenReturn(category);

        //when
       productService.updateProduct(updateName, updateUnit, updateCategoryName, group, productId);


       //then

        if(updateName!=null)
            product.setName(updateName);
        if(updateCategoryName!= null)
            product.setCategory(categoryService.findByName(updateCategoryName));
        if(updateUnit!=null)
            product.setUnit(Unit.valueOf(updateUnit));

        productRepository.save(product);
    }


//    @ParameterizedTest
//    @CsvSource({"Mleko, L, Inne","Mleko, , Inne",", L, inne"})
//    void shouldThrowExceptionWhenUserTryToUpdateTheSameProduct(String updateName, String updateUnit, String updateCategoryName){
//
//        //given
//        long productId = 20L;
//        long productGroupId = 2L;
//
//        Group group = new Group();
//        group.setId(productGroupId);
//
//        String categoryName = "Inne";
//        Category category = new Category();
//        category.setCategory(categoryName);
//
//        String productName = "Mleko";
//        String productUnit = Unit.L.name();
//
//        Product product = new Product();
//        product.setUnit(Unit.valueOf(productUnit));
//        product.setId(productId);
//        product.setName(productName);
//        product.setCategory(category);
//        product.setCreated_by(group);
//
//        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
//        when(productRepository.findProductsByGroup(productGroupId)).thenReturn(List.of(product));
//
//        //when/ then
//        assertThrows(Exception.class, () -> productService.updateProduct(updateName, updateUnit, updateCategoryName, group, productId));
//    }

}
