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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.dto.Request.BaseProductRequest;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.AddTheSameProduct;
import pl.plantoplate.REST.exception.CategoryNotFound;
import pl.plantoplate.REST.exception.ModifyGeneralProduct;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.ProductRepository;
import pl.plantoplate.REST.repository.ShopProductGroupRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopProductGroupRepository shopProductService;
    private final CategoryService categoryService;


    public ProductService(ProductRepository productRepository, ShopProductGroupRepository shopProductService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.shopProductService = shopProductService;
        this.categoryService = categoryService;
    }

    public void save(Product product){
        productRepository.save(product);
    }

    public Product findByName(String productName){
        return productRepository.findByName(productName).orElseThrow(RuntimeException::new);
    }

    public Product findById(long productId){
        return productRepository.findById(productId).orElseThrow(RuntimeException::new);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsOfGroup(long groupId) {
        return productRepository.findProductsByGroup(groupId);
    }


    public void deleteById(Long productId, Long groupId) throws ModifyGeneralProduct {
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException());

        long groupCreatedById = product.getCreated_by().getId();

        if(groupCreatedById == 1L || groupCreatedById != groupId ){

            log.info("User try to delete general product or product not his group");
            throw new ModifyGeneralProduct("User cannot delete general products or product not of his group");
        }


        shopProductService.deleteProductByGroupIdAndProductId(productId, groupId);
        productRepository.deleteById(productId);

        log.info("Product with id [" + productId + "] was deleted");
    }

    public void save(String name, String categoryName, String unit, Group group) throws AddTheSameProduct, CategoryNotFound, WrongProductInShoppingList {

        if(Arrays.stream(Unit.values()).map(Enum::name).noneMatch(u -> u.equals(unit))){
            throw new WrongProductInShoppingList("Unit is not correct. Available units : " + Arrays.toString(Unit.values()));
        }

        List<Product> allProducts = generalAndProductsOfGroup(group.getId());

        if(allProducts.stream().anyMatch(o -> o.getName().equals(name) && o.getUnit().name().equals(unit))){
            throw new AddTheSameProduct("Product with name [" + name + "] and unit ["+unit + "] already exists.");
        }

        Category categoryOfProduct = categoryService.findByName(categoryName);
        Product product = new Product(name, categoryOfProduct, group, Unit.valueOf(unit));

        productRepository.save(product);

        log.info("Product : [ " + name +" ] , [ " + categoryName +"] was saved.");

    }

    //TODO - CustomException
    public void updateProduct(BaseProductRequest updateProductRequest, Group group, long productId) throws CategoryNotFound, AddTheSameProduct, ModifyGeneralProduct {
        List<Product> productsGeneral = productRepository.findProductsByGroup(1L);

        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException());

        long groupCreatedById = product.getCreated_by().getId();

        if(groupCreatedById == 1L || groupCreatedById != group.getId() ){
            log.info("User try to update general product or product not his group");
            throw new ModifyGeneralProduct("User cannot update general products or product not of his group");
        }


        if(updateProductRequest.getUnit()!= null && updateProductRequest.getName()!=null){
            existsProductWithNameAndUnit(updateProductRequest.getName(), updateProductRequest.getUnit(), group.getId());
        }else if(updateProductRequest.getName()!=null && updateProductRequest.getUnit()==null ){
            existsProductWithNameAndUnit(updateProductRequest.getName(), product.getUnit().name(), group.getId());
        }else if(updateProductRequest.getUnit()!= null && updateProductRequest.getName() == null){
            existsProductWithNameAndUnit(product.getName(), updateProductRequest.getUnit(),group.getId());
        }

        if(updateProductRequest.getName()!=null)
            product.setName(updateProductRequest.getName());
        if(updateProductRequest.getCategory()!= null)
            product.setCategory(categoryService.findByName(updateProductRequest.getCategory()));
        if(updateProductRequest.getUnit()!=null)
            product.setUnit(Unit.valueOf(updateProductRequest.getUnit()));

        productRepository.save(product);

        log.info("Product was update. New Product [" + updateProductRequest.getName() + "] [" + updateProductRequest.getUnit() +"] [" +
                updateProductRequest.getCategory() + "]");
    }


    /**
     * Return general products nad products of group
     * @param groupId - id of group
     * @return
     */
    public List<Product> generalAndProductsOfGroup(long groupId){
        List<Product> products = productRepository.findProductsByGroup(groupId);
        List<Product> productsGeneral = productRepository.findProductsByGroup(1L);
        return Stream.concat(products.stream(), productsGeneral.stream()).collect(Collectors.toList());
    }


    private void existsProductWithNameAndUnit(String name, String unit, long groupId) throws AddTheSameProduct {
        List<Product> allProducts = generalAndProductsOfGroup(groupId);
        if (allProducts.stream().anyMatch(o -> o.getName().equals(name) && o.getUnit().name().equals(unit))) {
            throw new AddTheSameProduct("Product with name [" + name + "] and unit [" + unit + "] already exists.");
        }
    }
}
