package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.ShopProductRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ShopProductServiceTest {


    private ShopProductRepository shopProductRepository;
    private ProductService productService;
    private ShopProductService shopProductService;
    private UserService userService;


    @BeforeEach
    void setUp(){
        shopProductRepository = mock(ShopProductRepository.class);
        productService = mock(ProductService.class);
        userService = mock(UserService.class);
        shopProductService = new ShopProductService(shopProductRepository, productService, userService);
    }



    @Test
    void shouldSaveShopProduct(){
        //given
        ShopProduct productGroup = new ShopProduct();

        //then
        shopProductService.save(productGroup);

        //when
        verify(shopProductRepository).save(productGroup);
    }

    @ParameterizedTest
    @ValueSource(ints = { 0 , -20})
    void shouldThrowExceptionWhenUserTryToAddProductWithNegativeAmount(int amount){

        long productId = 1L;
        String email = "email";

        assertThrows(Exception.class, () -> shopProductService.addProductToList( productId, amount, email));
    }



    @Test
    void shouldThrowExceptionWhenUserTryToAddNotHisProduct() {

        long productId = 1L;
        String email = "email";
        int amount = 1;
        long groupId = 2L;
        Group group = new Group();
        group.setId(groupId);

        when(productService.findById(productId)).thenReturn(new Product());
        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.generalAndProductsOfGroup(groupId)).thenReturn(new ArrayList<>());

        assertThrows(Exception.class, () -> shopProductService.addProductToList( productId, amount, email));
    }

    @Test
    void shouldIncreaseAmountIfProductExistsInShoppingList() throws EntityNotFound, WrongProductInShoppingList {

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
        product.setCreated_by(group);
        product.setName("Name");
        product.setUnit(Unit.L);

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setProduct(product);
        shopProduct.setAmount(oldAmount);

        when(productService.findById(productId)).thenReturn(product);
        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.generalAndProductsOfGroup(groupId)).thenReturn(List.of(product));
        when(shopProductRepository.findAllByIsBoughtAndGroupId(false, groupId)).thenReturn(List.of(shopProduct));
        when(shopProductRepository.findByProductAndGroup(product, group)).thenReturn(java.util.Optional.of(shopProduct));

        //when
        shopProductService.addProductToList(productId, addAmount, email);


        //then
        ArgumentCaptor<ShopProduct> shopProductArgumentCaptor = ArgumentCaptor.forClass(ShopProduct.class);
        verify(shopProductRepository).save(shopProductArgumentCaptor.capture());
        ShopProduct saved = shopProductArgumentCaptor.getValue();

        assertEquals(saved.getAmount(), oldAmount + addAmount);
    }


    @Test
    void shouldAddProductToShoppingList() throws EntityNotFound, WrongProductInShoppingList {
        //given
        long productId = 1L;
        long groupId = 2L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);
        int addAmount = 1;

        Product product = new Product();
        product.setId(productId);
        product.setCreated_by(group);
        product.setName("Name");
        product.setUnit(Unit.L);


        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.findById(productId)).thenReturn(product);
        when(productService.generalAndProductsOfGroup(groupId)).thenReturn(List.of(product));
        when(shopProductRepository.findAllByIsBoughtAndGroupId(false, groupId)).thenReturn(new ArrayList<>());


        //when
        shopProductService.addProductToList(productId, addAmount, email);

        //then
        ArgumentCaptor<ShopProduct> shopProductArgumentCaptor = ArgumentCaptor.forClass(ShopProduct.class);
        verify(shopProductRepository).save(shopProductArgumentCaptor.capture());
        ShopProduct saved = shopProductArgumentCaptor.getValue();
        assertEquals(saved.getAmount(), addAmount);
        assertEquals(saved.isBought(), false);
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
        when(productService.getProductsOfGroup(groupId)).thenReturn(new ArrayList<>());

        assertThrows(Exception.class, () -> shopProductService.deleteProduct(productId, email));
    }


    @Test
    void shouldDeleteProductFromShoppingList() throws WrongProductInShoppingList {

        //given
        long productId = 1L;
        long groupId = 2L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);

        ShopProduct product = new ShopProduct();
        product.setId(productId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(shopProductRepository.findByGroup(group)).thenReturn(List.of(product));
        when(shopProductRepository.findById(productId)).thenReturn(java.util.Optional.of(product));


        //when
        shopProductService.deleteProduct(productId, email);


        //then
        verify(shopProductRepository).delete(product);
    }


    @ParameterizedTest
    @ValueSource(ints = { -1 , 0})
    void shouldThrowExceptionWHenAmountISNegativeWhenUserModiFyAmount(int amount){
        assertThrows(Exception.class, () -> shopProductService.modifyAmount(1L, "email", amount));
    }


    @Test
    void shouldThrowExceptionThenUserTryToModifyAMountToOfHisProduct(){
        //given
        long groupId = 2L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);
        int amount = 20;

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(shopProductRepository.findAllByIsBoughtAndGroupId(false, groupId)).thenReturn(new ArrayList<>());

        //when
        assertThrows(Exception.class, () -> shopProductService.modifyAmount(1L, email, amount));
    }

    @Test
    void shouldModifyAmount() throws WrongProductInShoppingList {

        //given
        long groupId = 2L;
        long productId = 10L;
        String email = "email";
        Group group = new Group();
        group.setId(groupId);
        int amount = 20;

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setId(productId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(shopProductRepository.findAllByIsBoughtAndGroupId(false, groupId)).thenReturn(List.of(shopProduct));
        when(shopProductRepository.findById(productId)).thenReturn(java.util.Optional.of(shopProduct));

        //when
        shopProductService.modifyAmount(productId, email, amount);

        //then
        ArgumentCaptor<ShopProduct> shopProductArgumentCaptor = ArgumentCaptor.forClass(ShopProduct.class);
        verify(shopProductRepository).save(shopProductArgumentCaptor.capture());
        ShopProduct captured = shopProductArgumentCaptor.getValue();
        assertEquals(amount, captured.getAmount());
    }


    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void shouldChangeIsBought(boolean isBought) throws WrongProductInShoppingList {

        //given
        long groupId = 2L;
        long productId = 10L;
        Group group = new Group();
        group.setId(groupId);
        String email = "email";

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setId(productId);
        shopProduct.setBought(isBought);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(shopProductRepository.findByGroup(group)).thenReturn(List.of(shopProduct));
        when(shopProductRepository.findById(productId)).thenReturn(java.util.Optional.of(shopProduct));

        //then
        shopProductService.changeIsBought(productId, email);

        //when
        ArgumentCaptor<ShopProduct> shopProductArgumentCaptor = ArgumentCaptor.forClass(ShopProduct.class);
        verify(shopProductRepository).save(shopProductArgumentCaptor.capture());
        ShopProduct captured = shopProductArgumentCaptor.getValue();
        assertEquals(!isBought, captured.isBought());
    }

}
