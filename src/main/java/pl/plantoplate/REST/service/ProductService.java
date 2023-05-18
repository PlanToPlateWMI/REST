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
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.AddTheSameProduct;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.exception.ModifyGeneralProduct;
import pl.plantoplate.REST.exception.WrongProductInShoppingList;
import pl.plantoplate.REST.repository.ProductRepository;
import pl.plantoplate.REST.repository.ShopProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopProductRepository shopProductService;
    private final CategoryService categoryService;


    public ProductService(ProductRepository productRepository, ShopProductRepository shopProductService, CategoryService categoryService) {
        this.productRepository = productRepository;
        this.shopProductService = shopProductService;
        this.categoryService = categoryService;
    }

    public void save(Product product){
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product findByName(String productName) throws EntityNotFound {
        return productRepository.findByName(productName).orElseThrow(() -> new EntityNotFound("Product [ " + productName
         + " not found."));
    }

    @Transactional(readOnly = true)
    public Product findById(long productId) throws EntityNotFound {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFound("Product [ " + productId
                + " not found."));
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsOfGroup(long groupId) {
        return productRepository.findProductsByGroup(groupId);
    }


    public void deleteById(Long productId, Long groupId) throws ModifyGeneralProduct, EntityNotFound {

        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFound("Product [ " + productId
                + " not found."));

        long groupCreatedById = product.getCreated_by().getId();

        if(groupCreatedById == 1L || groupCreatedById != groupId ){

            log.info("User try to delete general product or product not his group");
            throw new ModifyGeneralProduct("User cannot delete general products or product not of his group");
        }


        shopProductService.deleteProductByGroupIdAndProductId(productId, groupId);
        productRepository.deleteById(productId);

        log.info("Product with id [" + productId + "] was deleted");
    }

    public void save(String name, String categoryName, String unit, Group group) {

        isUnitCorrect(unit);

        List<Product> allProducts = generalAndProductsOfGroup(group.getId());

        if(allProducts.stream().anyMatch(o -> o.getName().equals(name) && o.getUnit().name().equals(unit))){
            throw new AddTheSameProduct("Product with name [" + name + "] and unit ["+unit + "] already exists.");
        }

        Category categoryOfProduct = categoryService.findByName(categoryName);
        Product product = new Product(name, categoryOfProduct, group, Unit.valueOf(unit));

        productRepository.save(product);

        log.info("Product : [ " + name +" ] , [ " + categoryName +"] was saved.");

    }


    public void updateProduct(String name, String unit, String category, Group group, long productId) {

        isUnitCorrect(unit);

        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFound("Product [ " + productId
                + " not found."));

        long groupCreatedById = product.getCreated_by().getId();

        if(groupCreatedById == 1L || groupCreatedById != group.getId() ){
            log.info("User try to update general product or product not his group");
            throw new ModifyGeneralProduct("User cannot update general products or product not of his group");
        }



        if(unit!= null && name!=null){
            existsProductWithNameAndUnit(name, unit, group.getId(), productId);
        }else if(name!=null){
            existsProductWithNameAndUnit(name, product.getUnit().name(), group.getId(), productId);
        }else if(unit != null){
            existsProductWithNameAndUnit(product.getName(), unit,group.getId(), productId);
        }

        if(name!=null)
            product.setName(name);
        if(category!= null)
            product.setCategory(categoryService.findByName(category));
        if(unit!=null)
            product.setUnit(Unit.valueOf(unit));

        productRepository.save(product);

        log.info("Product was update. New Product [" + name + "] [" + unit +"] [" +
                category + "]");
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


    private void existsProductWithNameAndUnit(String name, String unit, long groupId, long productId) {

        List<Product> allProducts = generalAndProductsOfGroup(groupId);

        for (int i = 0; i < allProducts.size(); i++) {
            if(allProducts.get(i).getId() == productId)
                allProducts.remove(i);
        }

        if (allProducts.stream().anyMatch(o -> o.getName().equals(name) && o.getUnit().name().equals(unit))) {
            throw new AddTheSameProduct("Product with name [" + name + "] and unit [" + unit + "] already exists.");
        }
    }

    private void isUnitCorrect(String unit){
        if(Arrays.stream(Unit.values()).map(Enum::name).noneMatch(u -> u.equals(unit)) && unit!=null){
            throw new WrongProductInShoppingList("Unit is not correct. Available units : " + Arrays.toString(Unit.values()));
        }
    }
}
