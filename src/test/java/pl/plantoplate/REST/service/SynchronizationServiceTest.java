package pl.plantoplate.REST.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.entity.Synchronization;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.MealIngredient;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.repository.SynchronizationRepository;

import java.util.Optional;

import static org.mockito.Mockito.*;

@DisplayName("Synchronization Service Test")
public class SynchronizationServiceTest {

    private SynchronizationRepository repository;
    private ProductService productService;
    private SynchronizationService synchronizationService;

    @BeforeEach
    void setUp() {
        repository = mock(SynchronizationRepository.class);
        productService = mock(ProductService.class);
        synchronizationService = new SynchronizationService(repository, productService);
    }

    @Test
    void shouldSaveSynchronizationIngredientEmpty(){

        //given
        float qty = 10L;
        Group group = new Group();
        long productId = 1L;
        Product product = new Product();
        when(productService.findById(productId)).thenReturn(product);
        when(repository.getSynchronizationByGroupAndProduct(group, product)).thenReturn(Optional.empty());

        //then when
        synchronizationService.saveSynchronizationIngredient(qty, group, productId);
    }

    @Test
    void shouldSaveSynchronizationIngredient(){

        //given
        float qty = 10L;
        Group group = new Group();
        long productId = 1L;
        Product product = new Product();
        when(productService.findById(productId)).thenReturn(product);
        Synchronization synchronization = new Synchronization();
        synchronization.setQty(qty);
        when(repository.getSynchronizationByGroupAndProduct(group, product)).thenReturn(Optional.of(synchronization));

        //then when
        synchronizationService.saveSynchronizationIngredient(qty, group, productId);
    }

    @Test
    void shouldDeleteSynchronizationQtyLess(){

        //given
        Product product = new Product();
        Group group = new Group();
        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setIngredient(product);
        Synchronization synchronization = new Synchronization();
        float qty = 2;
        float mealIngredientQty = 10;
        mealIngredient.setQty(mealIngredientQty);
        synchronization.setQty(qty);
        when(repository.getSynchronizationByGroupAndProduct(group, product)).thenReturn(Optional.of(synchronization));

        //then when
        synchronizationService.deleteSynchronizationIngredient(group, mealIngredient);
        verify(repository).delete(synchronization);

    }

    @Test
    void shouldDeleteSynchronization(){

        //given
        Product product = new Product();
        Group group = new Group();
        MealIngredient mealIngredient = new MealIngredient();
        mealIngredient.setIngredient(product);
        Synchronization synchronization = new Synchronization();
        float qty = 2;
        float mealIngredientQty = 1;
        mealIngredient.setQty(mealIngredientQty);
        synchronization.setQty(qty);
        when(repository.getSynchronizationByGroupAndProduct(group, product)).thenReturn(Optional.of(synchronization));

        //then when
        synchronizationService.deleteSynchronizationIngredient(group, mealIngredient);
        verify(repository).save(any());
    }
}
