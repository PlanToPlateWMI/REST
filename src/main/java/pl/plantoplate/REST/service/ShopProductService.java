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
import pl.plantoplate.REST.exception.AddToShoppingListWrongProduct;
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

    public void addProductToList(AddShopProductRequest productRequest, Group group) throws AddToShoppingListWrongProduct {

        Product product = productService.findById(productRequest.getId());
        List<Product> productsOfGroup = productService.generalAndProductsOfGroup(group.getId());

        if(productsOfGroup.stream().noneMatch(p -> p.getId() == productRequest.getId())){
            log.info("User try to add product not from his list to shopping list");
            throw new AddToShoppingListWrongProduct("User try to add product to shopping list not from his list");
        }

        // check if product with the same name nad unit already exists in shopping list and
        // if it is so - sum amounts
        List<ShopProductGroup> toBuyProductOfGroupList = shopProductGroupRepository.findAllByIsBoughtAndGroupId(false, group.getId());

        if(toBuyProductOfGroupList.stream().anyMatch(p -> p.getProduct().getName().equals(product.getName()) &&
                p.getProduct().getUnit().equals(product.getUnit()))){
            ShopProductGroup shopProductGroup = shopProductGroupRepository.findByProductAndGroup(product, group).get();
            shopProductGroup.setAmount(shopProductGroup.getAmount() + productRequest.getAmount());

            shopProductGroupRepository.save(shopProductGroup);
        }else{
            ShopProductGroup shopProductGroup = new ShopProductGroup(product,group, productRequest.getAmount(), false);
            shopProductGroupRepository.save(shopProductGroup);
        }

    }
}
