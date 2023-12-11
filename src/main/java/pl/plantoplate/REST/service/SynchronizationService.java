package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.Synchronization;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.MealIngredient;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.repository.SynchronizationRepository;

import java.util.Optional;

@Service
public class SynchronizationService {

    private final SynchronizationRepository repository;
    private final ProductService productService;

    public SynchronizationService(SynchronizationRepository repository, ProductService productService) {
        this.repository = repository;
        this.productService = productService;
    }

    public void saveSynchronizationIngredient(float qty, Group group, long productId){

        Product product = productService.findById(productId);

        Optional<Synchronization> mayBeSynchronization = repository.getSynchronizationByGroupAndProduct(group, product);
        if(mayBeSynchronization.isEmpty()) {
            Synchronization synchronization = new Synchronization();
            synchronization.setGroup(group);
            synchronization.setQty(qty);
            synchronization.setProduct(product);
            repository.save(synchronization);
        }else{
            Synchronization synchronization = mayBeSynchronization.get();
            float oldQTy = synchronization.getQty();
            synchronization.setQty(oldQTy + qty);
            repository.save(synchronization);
        }
    }

    public void deleteSynchronizationIngredient(Group group, MealIngredient mealIngredient){
        Optional<Synchronization> synchronization = getByProductAndGroup(mealIngredient.getIngredient(), group);
        if(synchronization.isPresent()){
            float synchronizationQty = synchronization.get().getQty();
            float qtyInMeal = mealIngredient.getQty();
            if(synchronizationQty <= qtyInMeal) {
                repository.delete(synchronization.get());
            }
            else{
                Synchronization entity = synchronization.get();
                entity.setQty(entity.getQty() - qtyInMeal);
                repository.save(entity);
            }
        }

    }

    public Optional<Synchronization> getByProductAndGroup(Product product, Group group){
        return repository.getSynchronizationByGroupAndProduct(group, product);
    }

    public void flush(){
        repository.flush();
    }
}
