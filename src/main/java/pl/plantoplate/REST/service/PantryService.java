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

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.NoValidProductWithAmount;
import pl.plantoplate.REST.repository.PantryRepository;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class PantryService {

    private final PantryRepository pantryRepository;
    private final UserService userService;
    private final ProductService productService;


    public PantryService(PantryRepository pantryRepository, UserService userService, ProductService productService) {
        this.pantryRepository = pantryRepository;
        this.userService = userService;
        this.productService = productService;
    }

    public List<ShopProduct> findProductsFromPantry(String email) {

        Group group = userService.findGroupOfUser(email);
        return pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }

    /**
     * Transfer products from shopping list to pantry (change product state to PATRY). If at least one product not found, user try to transfer not his product or
     * product doesn't have state BOUGHT - throws exception.
     * @param email - email of user
     * @param productId - idis of products
     * @return
     */
    public List<ShopProduct> transferProductToPantry(String email, long[] productId ) {
        Group group = userService.findGroupOfUser(email);

        for(long id:productId){
            ShopProduct shopProduct = pantryRepository.findById(id).orElseThrow(() -> new EntityNotFound("Shop product not found"));

            if(shopProduct.getProductState()!= ProductState.BOUGHT || !shopProduct.getGroup().equals(group))
                throw new NoValidProductWithAmount("User try to transfer to pantry not his product or product wasn't bought ");
        }

        for(long id:productId){
            ShopProduct shopProduct = pantryRepository.findById(id).get();
            shopProduct.setProductState(ProductState.PANTRY);
            pantryRepository.save(shopProduct);
        }

        return pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }


    /**
     * Add product to pantry from base
     * @param productId - id of product in base
     * @param amount -amount of product
     * @param email - email of user to identify his group
     * @return
     */
    public List<ShopProduct> addProductToPantry(long productId, float amount, String email) {

        Group group = userService.findGroupOfUser(email);

        if(amount <= 0 ){
            throw new NoValidProductWithAmount("Product amount cannot be negative or 0");
        }

        Product product = productService.findById(productId);

        List<Product> productsOfGroup = productService.generalAndProductsOfGroup(group);

        if(productsOfGroup.stream().noneMatch(p -> p.getId() == productId)){
            log.info("User try to add product not from his list to shopping list");
            throw new NoValidProductWithAmount("User try to add product to shopping list not from his list");
        }

        // check if product with the same name nad unit already exists in pantry and
        // if it is so - sum amounts
        List<ShopProduct> toBuyProductOfGroupList = this.findProductsFromPantry(email);

        if(toBuyProductOfGroupList.stream().anyMatch(p -> p.getProduct().getName().equals(product.getName()) &&
                p.getProduct().getUnit().equals(product.getUnit()))){
            ShopProduct pantryProduct = pantryRepository.findByProductAndGroup(product, group).get();
            pantryProduct.setAmount(pantryProduct.getAmount() + amount);

            pantryRepository.save(pantryProduct);
            log.info("Product with id [" + productId + "] exists in pantry. Modified his amount.");
        }else{
            ShopProduct pantryProduct = new ShopProduct(product, group, amount, ProductState.PANTRY);
            pantryRepository.save(pantryProduct);
            log.info("Product with id [" + productId + "] added to pantry.");
        }


        return this.findProductsFromPantry(email);
    }


    /**
     * Delete product from pantry by product id and email of user
     * @param pantryProductId - pantry Product id
     * @param email - email of user to identify his group
     * @return
     */
    public List<ShopProduct> deleteProduct(long pantryProductId, String email) {

        Group group = userService.findGroupOfUser(email);

        if(pantryRepository.findByIdAndProductStateAndGroup(pantryProductId, ProductState.PANTRY, group).isEmpty()){
            log.info("User try to delete product not from his pantry or product not exists");
            throw new NoValidProductWithAmount("User try to delete product not from his pantry or product not exists in pantry");
        }


        ShopProduct productGroup = pantryRepository.findById(pantryProductId).get();
        pantryRepository.delete(productGroup);
        log.info("Product with id [" + pantryProductId + "] was deleted from pantry");


        return this.findProductsFromPantry(email);
    }


    /**
     * Modify amount of product in pantry
     * @param pantryProductId - id of pantry product
     * @param email - email of ser to identify his group
     * @param amount - new amount of product
     * @return
     */
    public List<ShopProduct> modifyAmount(long pantryProductId, String email, float amount) {

        Group group = userService.findGroupOfUser(email);

        if(amount <= 0 ){
            throw new NoValidProductWithAmount("Product amount cannot be negative or 0");
        }

        if(pantryRepository.findByIdAndProductStateAndGroup(pantryProductId, ProductState.PANTRY, group).isEmpty()){
            log.info("User try to modify amount of product not from his pantry or product not exists");
            throw new NoValidProductWithAmount("User try to modify amount of product not from his pantry or product not exists in pantry");
        }

        ShopProduct shopProduct = pantryRepository.findById(pantryProductId).get();
        shopProduct.setAmount(amount);
        pantryRepository.save(shopProduct);

        return this.findProductsFromPantry(email);
    }
}
