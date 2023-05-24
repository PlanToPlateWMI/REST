package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.PantryRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Pantry Service Test")
public class PantryServiceTest {

    private PantryRepository pantryRepository;
    private UserService userService;
    private PantryService pantryService;

    @BeforeEach
    void init(){
        pantryRepository = mock(PantryRepository.class);
        userService = mock(UserService.class);
        pantryService = new PantryService(pantryRepository, userService);
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

        assertThrows(WrongProductInShoppingList.class, () -> pantryService.transferProductToPantry(email, products));
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



}
