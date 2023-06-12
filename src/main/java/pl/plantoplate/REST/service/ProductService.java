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
import pl.plantoplate.REST.exception.NoValidProductWithAmount;
import pl.plantoplate.REST.repository.GroupRepository;
import pl.plantoplate.REST.repository.ProductRepository;
import pl.plantoplate.REST.repository.ShopProductRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service Layer of Product JPA Repository
 */
@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ShopProductRepository shopProductService;
    private final CategoryService categoryService;
    private final GroupRepository groupRepository;


    public ProductService(ProductRepository productRepository, ShopProductRepository shopProductService, CategoryService categoryService, GroupRepository groupRepository) {
        this.productRepository = productRepository;
        this.shopProductService = shopProductService;
        this.categoryService = categoryService;
        this.groupRepository = groupRepository;
    }

    public void save(Product product){
        productRepository.save(product);
    }

    /**
     * Returns Product with provided name
     * @param productName name of product
     * @return Product {@link pl.plantoplate.REST.entity.product.Product} with provided name
     * @throws EntityNotFound if product with provided name doesn't exist
     */
    @Transactional(readOnly = true)
    public Product findByName(String productName) throws EntityNotFound {
        return productRepository.findByName(productName).orElseThrow(() -> new EntityNotFound("Product [ " + productName
         + " not found."));
    }

    /**
     * Returns Product with provided id
     * @param productId id of product
     * @return Product {@link pl.plantoplate.REST.entity.product.Product} with provided id
     * @throws EntityNotFound if product with provided id doesn't exist
     */
    @Transactional(readOnly = true)
    public Product findById(long productId) throws EntityNotFound {
        return productRepository.findById(productId).orElseThrow(() -> new EntityNotFound("Product [ " + productId
                + " not found."));
    }

    /**
     * Returns list of {@link pl.plantoplate.REST.entity.product.Product} of user's group
     * @param group user's group
     * @return list of {@link pl.plantoplate.REST.entity.product.Product}
     */
    @Transactional(readOnly = true)
    public List<Product> getProductsOfGroup(Group group) {
        return productRepository.findAllByCreatedBy(group);
    }


    /**
     * Delete {@link pl.plantoplate.REST.entity.product.Product} by product id if it is product of user's group and delete
     * {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with this product
     * @param productId id of product to delete
     * @param groupId group of user
     * @throws ModifyGeneralProduct user try to delete general product or product not his group
     * @throws EntityNotFound product with id not found
     */
    public void deleteById(Long productId, Long groupId) throws ModifyGeneralProduct, EntityNotFound {

        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFound("Product [ " + productId
                + " not found."));

        long groupCreatedById = product.getCreatedBy().getId();

        if(groupCreatedById != groupId ){

            log.info("User try to delete general product or product not his group");
            throw new ModifyGeneralProduct("User cannot delete general products (only moderators) or product not of his group");
        }


        shopProductService.deleteProductByGroupIdAndProductId(productId, groupId);
        productRepository.deleteById(productId);

        log.info("Product with id [" + productId + "] was deleted");
    }

    /**
     * Saves product {@link pl.plantoplate.REST.entity.product.Product} with provided name, categoryName, unit to group
     * If Product with the same name and unit exists in list of user's product throws {@link pl.plantoplate.REST.exception.AddTheSameProduct}
     * @param name product name
     * @param categoryName product category
     * @param unit product unit
     * @param group group of user
     */
    public void save(String name, String categoryName, String unit, Group group) {

        isUnitCorrect(unit);

        List<Product> allProducts = generalAndProductsOfGroup(group);

        if(allProducts.stream().anyMatch(o -> o.getName().equals(name) && o.getUnit().name().equals(unit))){
            throw new AddTheSameProduct("Product with name [" + name + "] and unit ["+unit + "] already exists.");
        }

        Category categoryOfProduct = categoryService.findByName(categoryName);
        Product product = new Product(name, categoryOfProduct, group, Unit.valueOf(unit));

        productRepository.save(product);

        log.info("Product : [ " + name +" ] , [ " + categoryName +"] was saved.");

    }


    /**
     * Update product {@link pl.plantoplate.REST.entity.product.Product} with product id and change provided name, categoryName, unit
     * If Product with the same name and unit exists in list of user's product throws {@link pl.plantoplate.REST.exception.AddTheSameProduct}
     * @param name new product name
     * @param unit new product unit
     * @param category new product category
     * @param group group of user
     * @param productId id of updated product
     */
    public void updateProduct(String name, String unit, String category, Group group, long productId) {

        isUnitCorrect(unit);

        Product product = productRepository.findById(productId).orElseThrow(() -> new EntityNotFound("Product [ " + productId
                + " not found."));

        long groupCreatedById = product.getCreatedBy().getId();

        if(groupCreatedById != group.getId() ){
            log.info("User try to update general product or product not his group");
            throw new ModifyGeneralProduct("User cannot update general products (only moderators) or product not of his group");
        }


        if(unit!= null && name!=null){
            existsProductWithNameAndUnit(name, unit, group, productId);
        }else if(name!=null){
            existsProductWithNameAndUnit(name, product.getUnit().name(), group, productId);
        }else if(unit != null){
            existsProductWithNameAndUnit(product.getName(), unit,group, productId);
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
     * Returns list of general products (products with group id = 1)  and products of group
     * @param userGroup id of group
     * @return list of general anf group products
     */
    public List<Product> generalAndProductsOfGroup(Group userGroup){
        List<Product> products = productRepository.findAllByCreatedBy(userGroup);
        List<Product> productsGeneral = productRepository.findAllByCreatedBy(groupRepository.getById(1L));
        return Stream.concat(products.stream(), productsGeneral.stream()).collect(Collectors.toList());
    }


    private void existsProductWithNameAndUnit(String name, String unit, Group userGroup, long productId) {

        List<Product> allProducts = generalAndProductsOfGroup(userGroup);

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
            throw new NoValidProductWithAmount("Unit is not correct. Available units : " + Arrays.toString(Unit.values()));
        }
    }
}
