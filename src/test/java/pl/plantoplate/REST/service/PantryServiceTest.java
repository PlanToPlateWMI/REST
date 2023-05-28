package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.NoValidProductWithAmount;
import pl.plantoplate.REST.repository.PantryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Pantry Service Test")
public class PantryServiceTest {

    private PantryRepository pantryRepository;
    private UserService userService;
    private PantryService pantryService;
    private ProductService productService;

    @BeforeEach
    void init(){
        pantryRepository = mock(PantryRepository.class);
        userService = mock(UserService.class);
        productService = mock(ProductService.class);
        pantryService = new PantryService(pantryRepository, userService, productService);
    }


    @Test
    void shouldReturnProductsFromPantry(){
        String email = "email";
        Group  group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);

        pantryService.findProductsFromPantry(email);

        verify(pantryRepository).findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }

    @Test
    void shouldThrowExceptionWhenProductNotExists(){
        long[] products = new long[] {1L,2L};
        String email = "email";
        Group  group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(pantryRepository.findById(products[0])).thenReturn(Optional.empty());


        assertThrows(EntityNotFound.class, () -> pantryService.transferProductToPantry(email, products));
    }


    @Test
    void shouldThrowExceptionWhenProductNotBought(){
        long[] products = new long[] {1L,2L};
        String email = "email";

        Group  group = new Group();
        ShopProduct bought = new ShopProduct();
        bought.setGroup(group);
        bought.setProductState(ProductState.BOUGHT);

        ShopProduct toBuy = new ShopProduct();
        toBuy.setGroup(group);
        toBuy.setProductState(ProductState.BUY);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(pantryRepository.findById(products[0])).thenReturn(Optional.of(bought));
        when(pantryRepository.findById(products[1])).thenReturn(Optional.of(toBuy));

        assertThrows(NoValidProductWithAmount.class, () -> pantryService.transferProductToPantry(email, products));
    }


    @Test
    void shouldChangeProductState(){
        long[] products = new long[] {1L,2L};
        String email = "email";

        Group  group = new Group();
        ShopProduct bought1 = new ShopProduct();
        bought1.setGroup(group);
        bought1.setProductState(ProductState.BOUGHT);

        ShopProduct bought2 = new ShopProduct();
        bought2.setGroup(group);
        bought2.setProductState(ProductState.BOUGHT);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(pantryRepository.findById(products[0])).thenReturn(Optional.of(bought1));
        when(pantryRepository.findById(products[1])).thenReturn(Optional.of(bought2));

        pantryService.transferProductToPantry(email, products);


        assertEquals(ProductState.PANTRY, bought1.getProductState());
        assertEquals(ProductState.PANTRY, bought2.getProductState());
        verify(pantryRepository).findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }


    @ParameterizedTest
    @ValueSource(ints = {0,-20})
    void shouldThrowExceptionWhenAmountIsZeroOrNegative(int amount){
        long productId = 1L;
        String email = "email";
        when(userService.findGroupOfUser(email)).thenReturn(new Group());

        assertThrows(NoValidProductWithAmount.class, () -> pantryService.addProductToPantry(productId, amount, email));
    }


    @Test
    void shouldThrowExceptionWhenProductNotOfUsersGroup(){
        long productId = 1L;
        String email = "email";
        int amount = 40;

        long groupId = 2L;
        Group group = new Group();
        group.setId(groupId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.generalAndProductsOfGroup(group)).thenReturn(new ArrayList<>());

        assertThrows(NoValidProductWithAmount.class, () -> pantryService.addProductToPantry(productId, amount, email));
    }

    @Test
    void shouldIncreaseAmountIfProductExists(){

        //given
        long productId = 1L;
        long groupId = 2L;
        Group group = new Group();
        group.setId(groupId);
        String email = "email";
        int addAmount = 1;
        int oldAmount = 20;

        Product product = new Product();
        product.setId(productId);
        product.setCreatedBy(group);
        product.setName("Name");
        product.setUnit(Unit.L);

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setProduct(product);
        shopProduct.setAmount(oldAmount);

        when(productService.findById(productId)).thenReturn(product);
        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.generalAndProductsOfGroup(group)).thenReturn(List.of(product));
        when(pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group)).thenReturn(List.of(shopProduct));
        when(pantryRepository.findByProductAndGroup(product, group)).thenReturn(java.util.Optional.of(shopProduct));

        //when
        pantryService.addProductToPantry(productId, addAmount, email);

        //then
        ArgumentCaptor<ShopProduct> shopProductArgumentCaptor = ArgumentCaptor.forClass(ShopProduct.class);
        verify(pantryRepository).save(shopProductArgumentCaptor.capture());
        ShopProduct saved = shopProductArgumentCaptor.getValue();

        assertEquals(saved.getAmount(), oldAmount + addAmount);
    }


    @Test
    void shouldAddProductToPantry() throws EntityNotFound, NoValidProductWithAmount {
        //given
        long productId = 1L;
        long groupId = 2L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);
        float addAmount = 1;

        Product product = new Product();
        product.setId(productId);
        product.setCreatedBy(group);
        product.setName("Name");
        product.setUnit(Unit.L);


        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.findById(productId)).thenReturn(product);
        when(productService.generalAndProductsOfGroup(group)).thenReturn(List.of(product));
        when(pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group)).thenReturn(new ArrayList<>());


        //when
        pantryService.addProductToPantry(productId, addAmount, email);

        //then
        ArgumentCaptor<ShopProduct> shopProductArgumentCaptor = ArgumentCaptor.forClass(ShopProduct.class);
        verify(pantryRepository).save(shopProductArgumentCaptor.capture());
        ShopProduct saved = shopProductArgumentCaptor.getValue();
        assertEquals(saved.getAmount(), addAmount);
        assertEquals(saved.getProductState(), ProductState.PANTRY);
    }


    @Test
    void shouldThrowExceptionWhenUserTryToDeleteProductNotFromHistGroup(){

        //given
        long productId = 1L;
        long groupId = 2L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(pantryRepository.findByIdAndProductStateAndGroup(productId, ProductState.PANTRY, group)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> pantryService.deleteProduct(productId, email));
    }


    @Test
    void shouldDeleteProductFromShoppingList() throws NoValidProductWithAmount {

        //given
        long productId = 1L;
        long groupId = 2L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);

        ShopProduct product = new ShopProduct();
        product.setId(productId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(pantryRepository.findByIdAndProductStateAndGroup(productId, ProductState.PANTRY, group))
                                            .thenReturn(Optional.of(product));
        when(pantryRepository.findById(productId)).thenReturn(java.util.Optional.of(product));


        //when
        pantryService.deleteProduct(productId, email);

        //then
        verify(pantryRepository).delete(product);
    }


    @ParameterizedTest
    @ValueSource(floats = { -1.0f , 0.0f})
    void shouldThrowExceptionWHenAmountISNegativeWhenUserModiFyAmount(float amount){
        assertThrows(Exception.class, () -> pantryService.modifyAmount(1L, "email", amount));
    }


    @Test
    void shouldThrowExceptionThenUserTryToModifyAMountNotOfHisProduct(){
        //given
        long groupId = 2L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);
        float amount = 20;
        long pantryProductId = 20L;

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(pantryRepository.findByIdAndProductStateAndGroup(pantryProductId, ProductState.PANTRY, group)).thenReturn(Optional.empty());

        //when
        assertThrows(NoValidProductWithAmount.class, () -> pantryService.modifyAmount(pantryProductId, email, amount));
    }

    @Test
    void shouldModifyAmount() throws NoValidProductWithAmount {

        //given
        long groupId = 2L;
        long pantryProductId = 10L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);
        float amount = 20;

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setId(pantryProductId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(pantryRepository.findByIdAndProductStateAndGroup(pantryProductId, ProductState.PANTRY, group)).thenReturn(Optional.of(shopProduct));
        when(pantryRepository.findById(pantryProductId)).thenReturn(java.util.Optional.of(shopProduct));

        //when
        pantryService.modifyAmount(pantryProductId, email, amount);

        //then
        ArgumentCaptor<ShopProduct> shopProductArgumentCaptor = ArgumentCaptor.forClass(ShopProduct.class);
        verify(pantryRepository).save(shopProductArgumentCaptor.capture());
        ShopProduct captured = shopProductArgumentCaptor.getValue();
        assertEquals(amount, captured.getAmount());
    }




}
