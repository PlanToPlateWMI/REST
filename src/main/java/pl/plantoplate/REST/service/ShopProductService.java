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
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.ShopProductGroupRepository;

import java.util.List;

@Service
@Slf4j
public class ShopProductService {

    private final ShopProductGroupRepository shopProductGroupRepository;
    private final ProductService productService;


    public ShopProductService(ShopProductGroupRepository shopProductGroupRepository, ProductService productService) {
        this.shopProductGroupRepository = shopProductGroupRepository;
        this.productService = productService;
    }


    public void save(ShopProductGroup shopProductGroup){
        shopProductGroupRepository.save(shopProductGroup);
    }

    public void deleteProductByGroupIdAndProductId(Long productId, Long groupId) {
        shopProductGroupRepository.deleteProductByGroupIdAndProductId(productId, groupId);
    }

    public void addProductToList(AddShopProductRequest productRequest, Group group) throws WrongProductInShoppingList {

        if(productRequest.getAmount() <= 0 ){
            throw new WrongProductInShoppingList("Product amount cannot be negative or 0");
        }

        Product product = productService.findById(productRequest.getId());
        List<Product> productsOfGroup = productService.generalAndProductsOfGroup(group.getId());

        if(productsOfGroup.stream().noneMatch(p -> p.getId() == productRequest.getId())){
            log.info("User try to add product not from his list to shopping list");
            throw new WrongProductInShoppingList("User try to add product to shopping list not from his list");
        }

        // check if product with the same name nad unit already exists in shopping list and
        // if it is so - sum amounts
        List<ShopProductGroup> toBuyProductOfGroupList = shopProductGroupRepository.findAllByIsBoughtAndGroupId(false, group.getId());

        if(toBuyProductOfGroupList.stream().anyMatch(p -> p.getProduct().getName().equals(product.getName()) &&
                p.getProduct().getUnit().equals(product.getUnit()))){
            ShopProductGroup shopProductGroup = shopProductGroupRepository.findByProductAndGroup(product, group).get();
            shopProductGroup.setAmount(shopProductGroup.getAmount() + productRequest.getAmount());

            shopProductGroupRepository.save(shopProductGroup);
            log.info("Product with id [" + productRequest.getId() + "] exists in shopping list. Modified his amount.");
        }else{
            ShopProductGroup shopProductGroup = new ShopProductGroup(product,group, productRequest.getAmount(), false);
            shopProductGroupRepository.save(shopProductGroup);
            log.info("Product with id [" + productRequest.getId() + "] added to shopping list.");
        }



    }

    public void deleteProduct(long id, Group group) throws WrongProductInShoppingList {

        List<ShopProductGroup> shopProductsOfGroup = shopProductGroupRepository.findByGroup(group);

        if(shopProductsOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to delete product not from his shopping list or product not exists");
            throw new WrongProductInShoppingList("User try to delete product not from his shopping list");
        }

        ShopProductGroup productGroup = shopProductGroupRepository.findById(id).get();
        shopProductGroupRepository.delete(productGroup);
        log.info("Product with id [" + id + "] was deleted from shopping list");
    }

    public void modifyAmount(long id, Group group, int amount) throws WrongProductInShoppingList {

        if(amount <= 0 ){
            throw new WrongProductInShoppingList("Product amount cannot be negative or 0");
        }

        List<ShopProductGroup> toBuyProductOfGroup = shopProductGroupRepository.findAllByIsBoughtAndGroupId(false, group.getId());
        if(toBuyProductOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to modify product not from toBuy list");
            throw new WrongProductInShoppingList("User try to modify product not from toBuyList ");
        }

        ShopProductGroup productGroup = shopProductGroupRepository.findById(id).get();
        productGroup.setAmount(amount);
        shopProductGroupRepository.save(productGroup);
    }

    public void changeIsBought(long id, Group group) throws WrongProductInShoppingList {

        List<ShopProductGroup> shopProductsOfGroup = shopProductGroupRepository.findByGroup(group);

        if(shopProductsOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to change product not from his shopping list or product not exists");
            throw new WrongProductInShoppingList("User try to change product not from his shopping list");
        }

        ShopProductGroup shopProductGroup = shopProductGroupRepository.findById(id).get();

        if (shopProductGroup.isBought()) {
            shopProductGroup.setBought(false);
            log.info("Product with id [" +id + "] was moved to Trzeba kupić section");
        } else {
            shopProductGroup.setBought(true);
            log.info("Product with id [" +id + "] was moved to Kupione section");
        }
        shopProductGroupRepository.save(shopProductGroup);
    }
}
