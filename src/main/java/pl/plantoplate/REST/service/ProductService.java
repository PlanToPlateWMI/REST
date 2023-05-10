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
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.exception.DeleteGeneralProduct;
import pl.plantoplate.REST.repository.ProductRepository;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopProductService shopProductService;


    public ProductService(ProductRepository productRepository, ShopProductService shopProductService) {
        this.productRepository = productRepository;
        this.shopProductService = shopProductService;
    }

    public void save(Product product){
        productRepository.save(product);
    }

    public Product findByName(String productName){
        return productRepository.findByName(productName).orElseThrow(RuntimeException::new);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsOfGroup(long groupId) {
        return productRepository.findProductsByGroup(groupId);
    }


    public void deleteById(Long productId, Long groupId) throws DeleteGeneralProduct {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException());

        long groupCreatedById = product.getCreated_by().getId();

        if(groupCreatedById == 1L || groupCreatedById != groupId ){

            log.info("User try to delete general product or product not his group");
            throw new DeleteGeneralProduct("User cannot delete general products or product not of his group");
        }


        shopProductService.deleteProductByGroupIdAndProductId(productId, groupId);
        productRepository.deleteById(productId);

        log.info("Product with id [" + productId + "] was deleted");
    }
}
