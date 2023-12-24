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
import java.util.Optional;

/**
 * Service Layer of Pantry JPA Repository
 */
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
     * Change state of shop products {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with ids and state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT}
     * to {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY}
     * If the same product exists in pantry - increase amount
     * If product not from user's group or doesn't have required state throws {@link pl.plantoplate.REST.exception.NoValidProductWithAmount}
     * @param email - email of user
     * @param productId - idis of products with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT} state
     * @return list of products in user's pantry
     */
    public List<ShopProduct> transferProductToPantry(String email, long[] productId ) {
        Group group = userService.findGroupOfUser(email);

        for(long id:productId){
            ShopProduct shopProduct = pantryRepository.findById(id).orElseThrow(() -> new EntityNotFound("Shop product not found"));

            if(shopProduct.getProductState()!= ProductState.BOUGHT || !shopProduct.getGroup().equals(group))
                throw new NoValidProductWithAmount("User try to transfer to pantry not his product or product wasn't bought ");
        }

        List<ShopProduct> pantryProduct = this.findProductsFromPantry(email);
        for(long id:productId){
            ShopProduct shopProduct = pantryRepository.findById(id).get();

            Optional<ShopProduct> productFromPantryTheSame = pantryProduct.stream().filter(p -> p.getProduct().getName().equals(shopProduct.getProduct().getName())
                    && p.getProduct().getUnit().equals(shopProduct.getProduct().getUnit())).findFirst();

            if(productFromPantryTheSame.isPresent()){
                ShopProduct productFromPantry = productFromPantryTheSame.get();
                productFromPantry.setAmount(productFromPantry.getAmount() + shopProduct.getAmount());
                pantryRepository.save(productFromPantry);
                pantryRepository.delete(shopProduct);
            }else{
                shopProduct.setProductState(ProductState.PANTRY);
                pantryRepository.save(shopProduct);
            }
        }

        return pantryRepository.findAllByProductStateAndGroup(ProductState.PANTRY, group);
    }


    /**
     * Save {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with product {@link pl.plantoplate.REST.entity.product.Product} with id productId
     * and amount to group of user email parametrs. Set {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY}
     * Throws {@link pl.plantoplate.REST.exception.NoValidProductWithAmount} if amount is negative or zero or user try to add not his product
     * If the same product exists in pantry - increase amount
     * @param productId id of product {@link pl.plantoplate.REST.entity.product.Product}
     * @param amount amount of product to add
     * @param email email of user to identify his group
     * @return list of products in user's pantry
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
        List<ShopProduct> pantryProducts = this.findProductsFromPantry(email);

        if(pantryProducts.stream().anyMatch(p -> p.getProduct().getName().equals(product.getName()) &&
                p.getProduct().getUnit().equals(product.getUnit()))){
            ShopProduct pantryProduct = pantryRepository.findByProductAndGroupAndProductState(product, group, ProductState.PANTRY).get();
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
     * Delete {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} by id parametr
     * Throws {@link pl.plantoplate.REST.exception.NoValidProductWithAmount} if user try to add not his product
     * @param pantryProductId pantry Product id
     * @param email email of user to identify his group
     * @return list of products in user's pantry
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
     * Modify amount of {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} by id
     * Throws {@link pl.plantoplate.REST.exception.NoValidProductWithAmount} if amount is negative or zero or user try to add not his product
     * @param pantryProductId id of pantry product
     * @param email email of ser to identify his group
     * @param amount new amount of product
     * @return list of products in user's pantry
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
