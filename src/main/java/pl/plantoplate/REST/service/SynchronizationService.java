/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */


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

    /**
     * Save synchronization {@link pl.plantoplate.REST.entity.Synchronization}
     * @param qty - qty of saved product
     * @param group - user's group
     * @param productId - product to save
     */
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

    /**
     * Delete or reduce amount of Synchronization {@link pl.plantoplate.REST.entity.Synchronization} by Product {@link pl.plantoplate.REST.entity.product.Product}
     * and group {@link pl.plantoplate.REST.entity.auth.Group}
     * @param group - user's group
     * @param mealIngredient - product of meal to delete
     */
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
}
