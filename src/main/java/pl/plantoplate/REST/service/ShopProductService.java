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
import pl.plantoplate.REST.dto.Request.AddShopProductRequest;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.ShopProductRepository;

import java.util.List;

@Service
@Slf4j
public class ShopProductService {

    private final ShopProductRepository shopProductRepository;
    private final ProductService productService;


    public ShopProductService(ShopProductRepository shopProductRepository, ProductService productService) {
        this.shopProductRepository = shopProductRepository;
        this.productService = productService;
    }


    public void save(ShopProduct shopProduct){
        shopProductRepository.save(shopProduct);
    }


    public void addProductToList(long productId, int amount , Group group) throws WrongProductInShoppingList, EntityNotFound {

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



    }

    public void deleteProduct(long id, Group group) throws WrongProductInShoppingList {

        List<ShopProduct> shopProductsOfGroup = shopProductRepository.findByGroup(group);

        if(shopProductsOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to delete product not from his shopping list or product not exists");
            throw new WrongProductInShoppingList("User try to delete product not from his shopping list");
        }

        ShopProduct productGroup = shopProductRepository.findById(id).get();
        shopProductRepository.delete(productGroup);
        log.info("Product with id [" + id + "] was deleted from shopping list");
    }

    public void modifyAmount(long id, Group group, int amount) throws WrongProductInShoppingList {

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
    }

    public void changeIsBought(long id, Group group) throws WrongProductInShoppingList {

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
    }
}
