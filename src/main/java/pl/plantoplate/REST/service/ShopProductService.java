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
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.ShopProductRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ShopProductService {

    private final ShopProductRepository shopProductRepository;
    private final ProductService productService;
    private final UserService userService;


    public ShopProductService(ShopProductRepository shopProductRepository, ProductService productService, UserService userService) {
        this.shopProductRepository = shopProductRepository;
        this.productService = productService;
        this.userService = userService;
    }


    public void save(ShopProduct shopProduct){
        shopProductRepository.save(shopProduct);
    }


    /**
     * add product from base to shopping list ( to buy section) . Return to buy product shopping list.
     * @param productId - id of product from base
     * @param amount - amount of product
     * @param email - email of user to find his group
     * @return
     */
    public List<ShopProduct> addProductToList(long productId, int amount , String email) {

        Group group = userService.findGroupOfUser(email);

        if(amount <= 0 ){
            throw new WrongProductInShoppingList("Product amount cannot be negative or 0");
        }

        Product product = productService.findById(productId);
        List<Product> productsOfGroup = productService.generalAndProductsOfGroup(group.getId());

        if(productsOfGroup.stream().noneMatch(p -> p.getId() == productId)){
            log.info("User try to add product not from his list to shopping list");
            throw new WrongProductInShoppingList("User try to add product to shopping list not from his list");
        }

        // check if product with the same name nad unit already exists in shopping list and
        // if it is so - sum amounts
        List<ShopProduct> toBuyProductOfGroupList = shopProductRepository.findAllByIsBoughtAndGroupId(false, group.getId());

        if(toBuyProductOfGroupList.stream().anyMatch(p -> p.getProduct().getName().equals(product.getName()) &&
                p.getProduct().getUnit().equals(product.getUnit()))){
            ShopProduct shopProduct = shopProductRepository.findByProductAndGroup(product, group).get();
            shopProduct.setAmount(shopProduct.getAmount() + amount);

            shopProductRepository.save(shopProduct);
            log.info("Product with id [" + productId + "] exists in shopping list. Modified his amount.");
        }else{
            ShopProduct shopProduct = new ShopProduct(product, group, amount, false);
            shopProductRepository.save(shopProduct);
            log.info("Product with id [" + productId + "] added to shopping list.");
        }


        return this.getProducts(email, false);
    }

    /**
     * Delete product from shopping list. If product was deleted from toBuy list - return shopping list of toBuy products.
     * If product was deleted from bought list - return shopping list of bought products
     * @param id - shopping product id
     * @param email - email of user to find his group
     * @return
     */
    public List<ShopProduct> deleteProduct(long id, String email) {

        Group group = userService.findGroupOfUser(email);

        List<ShopProduct> shopProductsOfGroup = shopProductRepository.findByGroup(group);

        if(shopProductsOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to delete product not from his shopping list or product not exists");
            throw new WrongProductInShoppingList("User try to delete product not from his shopping list");
        }

        ShopProduct productGroup = shopProductRepository.findById(id).get();
        boolean productType = productGroup.isBought();
        shopProductRepository.delete(productGroup);
        log.info("Product with id [" + id + "] was deleted from shopping list");

        return getProducts(email, productType);
    }

    public List<ShopProduct> modifyAmount(long id, String email, int amount) {

        Group group = userService.findGroupOfUser(email);

        if(amount <= 0 ){
            throw new WrongProductInShoppingList("Product amount cannot be negative or 0");
        }

        List<ShopProduct> toBuyProductOfGroup = shopProductRepository.findAllByIsBoughtAndGroupId(false, group.getId());
        if(toBuyProductOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to modify product not from toBuy list");
            throw new WrongProductInShoppingList("User try to modify product not from toBuyList ");
        }

        ShopProduct productGroup = shopProductRepository.findById(id).get();
        productGroup.setAmount(amount);
        shopProductRepository.save(productGroup);

        return getProducts(email, false);
    }

    /**
     * Change is bought parametr of product. Return shopping products list.
     * @param id - id of product to change type
     * @param email - email of user to identify group
     * @return
     */
    public List<ShopProduct> changeIsBought(long id, String email) {

        Group group = userService.findGroupOfUser(email);

        List<ShopProduct> shopProductsOfGroup = shopProductRepository.findByGroup(group);

        if(shopProductsOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to change product not from his shopping list or product not exists");
            throw new WrongProductInShoppingList("User try to change product not from his shopping list");
        }

        ShopProduct shopProduct = shopProductRepository.findById(id).get();

        if (shopProduct.isBought()) {
            shopProduct.setBought(false);
            log.info("Product with id [" +id + "] was moved to Trzeba kupić section");
        } else {
            shopProduct.setBought(true);
            log.info("Product with id [" +id + "] was moved to Kupione section");
        }
        shopProductRepository.save(shopProduct);

        return Stream.concat(getProducts(email, true).stream(), getProducts(email, false)
                .stream()).collect(Collectors.toList());
    }

    public List<ShopProduct> getProducts(String email, boolean type) {
        Group group = userService.findGroupOfUser(email);
        return group.getShopProductList().stream()
                .filter(p -> p.isBought() == type)
                .collect(Collectors.toList());
    }
}
