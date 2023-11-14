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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.plantoplate.REST.controller.dto.model.IngredientQtUnit;
import pl.plantoplate.REST.controller.dto.request.AddRecipeToShoppingList;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.NoValidProductWithAmount;
import pl.plantoplate.REST.repository.RecipeIngredientRepository;
import pl.plantoplate.REST.repository.ShopProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service Layer of ShopProduct JPA Repository
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ShoppingListService {

    private final ShopProductRepository shopProductRepository;
    private final ProductService productService;
    private final UserService userService;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final RecipeService recipeService;

    public void save(ShopProduct shopProduct){
        shopProductRepository.save(shopProduct);
    }


    /**
     * Save {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct}
     * @param productId id of product from base
     * @param amount amount of product
     * @param email email of user to find his group
     * @return list of {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY}
     */
    public List<ShopProduct> addProductToShoppingList(long productId, float amount , String email) {

        ShopProduct shopProduct = addProductToShoppingListLogic(productId, amount, email);
        shopProductRepository.saveAndFlush(shopProduct);

        return this.getProducts(email, ProductState.BUY);
    }

    /**
     * Add ingredients of provided  recipe to shopping list based on portions
     * @param request - information about recipe id, portions and ingredient to add
     * @param email
     * @return
     */
    public List<ShopProduct> addProductsToShoppingList(AddRecipeToShoppingList request, String email) {

        long recipeId = request.getRecipeId();
        Recipe recipe = recipeService.findById(recipeId);

        // ingredients (Map of ingredientId to qty/UNIT in original recipe)
        Map<Long, IngredientQtUnit> ingredientIdToUnitQtyInOriginalRecipe= recipeIngredientRepository.
                findAllByRecipe(recipe).stream().collect(Collectors.toMap(
                r-> r.getIngredient().getId(), r -> new IngredientQtUnit(r.getQty(), r.getIngredient().getUnit())));
        // ingredient ids provided by user
        List<Long> ingredientIdsList = request.getIngredientsId();

        long portionsInOriginalRecipe = recipe.getPortions();
        long portionsPlanned = request.getPortions();
        float proportionIngredientQty = (float) portionsPlanned/portionsInOriginalRecipe;

        List<ShopProduct> shopProductList = new ArrayList<>();

        for(Long ingredientToPlanId: ingredientIdsList){
            IngredientQtUnit originalQtyUnit = ingredientIdToUnitQtyInOriginalRecipe.get(ingredientToPlanId);
            float qtyPlanned = proportionIngredientQty * originalQtyUnit.getQty();
            if(originalQtyUnit.getUnit().equals(Unit.SZT))
                qtyPlanned = (int) Math.ceil(qtyPlanned);

            shopProductList.add(addProductToShoppingListLogic(ingredientToPlanId, qtyPlanned, email));

        }
        shopProductRepository.saveAllAndFlush(shopProductList);
        return this.getProducts(email, ProductState.BUY);
    }

    /**
      * Algorithm return ShopProduct to save with product {@link pl.plantoplate.REST.entity.product.Product} with id productId
     * and amount to group of user email parametrs. Set {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY}
     * Throws {@link pl.plantoplate.REST.exception.NoValidProductWithAmount} if amount is negative or zero or user try to add not his product
     * If the same product exists with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY}  - increase amount
     * @param productId - productId to save
     * @param amount - amount of product to save
     * @param email - email of user to identify group
     * @return  {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} to save
     */
    private ShopProduct addProductToShoppingListLogic(long productId, float amount , String email){

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

        // check if product with the same name nad unit already exists in shopping list and
        // if it is so - sum amounts
        List<ShopProduct> toBuyProductOfGroupList = shopProductRepository.findAllByProductStateAndGroup(ProductState.BUY, group);

        if(toBuyProductOfGroupList.stream().anyMatch(p -> p.getProduct().getName().equals(product.getName()) &&
                p.getProduct().getUnit().equals(product.getUnit()))){
            ShopProduct shopProduct = shopProductRepository.findByProductAndProductStateAndGroup(product, ProductState.BUY, group).get();
            shopProduct.setAmount(shopProduct.getAmount() + amount);
            log.info("Product with id [" + productId + "] exists in shopping list. Modified his amount.");
            return shopProduct;
        }else{
            ShopProduct shopProduct = new ShopProduct(product, group, amount, ProductState.BUY);
            log.info("Product with id [" + productId + "] added to shopping list.");
            return shopProduct;
        }
    }

    /**
     * Delete {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} by id.
     * Throws {@link pl.plantoplate.REST.exception.NoValidProductWithAmount} then user try to product not from hist group
     * @param id shopping product id
     * @param email email of user to identify his group
     * @return list of {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY} of deleted product
     */
    public List<ShopProduct> deleteProduct(long id, String email) {

        Group group = userService.findGroupOfUser(email);

        List<ShopProduct> shopProductsOfGroup = shopProductRepository.findByGroup(group);

        if(shopProductsOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to delete product not from his shopping list or product not exists");
            throw new NoValidProductWithAmount("User try to delete product not from his shopping list");
        }

        ShopProduct productGroup = shopProductRepository.findById(id).get();
        ProductState productType = productGroup.getProductState();
        shopProductRepository.delete(productGroup);
        log.info("Product with id [" + id + "] was deleted from shopping list");

        return getProducts(email, productType);
    }

    /**
     * Modify amount of {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} by id with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY}
     * Throws {@link pl.plantoplate.REST.exception.NoValidProductWithAmount} if amount is negative or zero or user try to modify product not with required state
     * @param id id of shopping list product
     * @param email email of ser to identify his group
     * @param amount new amount of product
     * @return list of {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY} state
     */
    public List<ShopProduct> modifyAmount(long id, String email, float amount) {

        Group group = userService.findGroupOfUser(email);

        if(amount <= 0 ){
            throw new NoValidProductWithAmount("Product amount cannot be negative or 0");
        }

        List<ShopProduct> toBuyProductOfGroup = shopProductRepository.findAllByProductStateAndGroup(ProductState.BUY, group);
        if(toBuyProductOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to modify product not from toBuy list");
            throw new NoValidProductWithAmount("User try to modify product not from toBuyList ");
        }

        ShopProduct productGroup = shopProductRepository.findById(id).get();
        productGroup.setAmount(amount);
        shopProductRepository.save(productGroup);

        return getProducts(email, ProductState.BUY);
    }

    /**
     * Change {@link pl.plantoplate.REST.entity.shoppinglist.ProductState} from BUY to BOUGHT or from BOUGHT to BUY of {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct}
     * @param id id of product to change type
     * @param email email of user to identify group
     * @return list of {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} of user's group with state BOUGHT and BUY
     */
    public List<ShopProduct> changeProductStateOnShoppingList(long id, String email) {

        Group group = userService.findGroupOfUser(email);

        List<ShopProduct> shopProductsOfGroup = shopProductRepository.findByGroup(group);

        if(shopProductsOfGroup.stream().noneMatch(p -> p.getId() == id)){
            log.info("User try to change product not from his shopping list or product not exists");
            throw new NoValidProductWithAmount("User try to change product not from his shopping list");
        }

        ShopProduct shopProduct = shopProductRepository.findById(id).get();
        Product productOfShopProduct = shopProduct.getProduct();

        if (shopProduct.getProductState() == ProductState.BOUGHT) {

            Optional<ShopProduct> shopProductFromBase = shopProductRepository.findByProductAndProductStateAndGroup(productOfShopProduct, ProductState.BUY, group);
            if(shopProductFromBase.isPresent()){
                shopProductFromBase.get().setAmount(shopProductFromBase.get().getAmount() + shopProduct.getAmount());
                shopProductRepository.delete(shopProduct);
            }else {
                shopProduct.setProductState(ProductState.BUY);
                shopProductRepository.save(shopProduct);
            }

            log.info("Product with id [" +id + "] was moved to Trzeba kupić section");
        } else {

            Optional<ShopProduct> shopProductFromBase = shopProductRepository.findByProductAndProductStateAndGroup(productOfShopProduct, ProductState.BOUGHT, group);
            if(shopProductFromBase.isPresent()){
                shopProductFromBase.get().setAmount(shopProductFromBase.get().getAmount() + shopProduct.getAmount());
                shopProductRepository.delete(shopProduct);
            }else {
                shopProduct.setProductState(ProductState.BOUGHT);
                shopProductRepository.save(shopProduct);
            }

            log.info("Product with id [" +id + "] was moved to Kupione section");
        }

        return Stream.concat(getProducts(email, ProductState.BUY).stream(), getProducts(email, ProductState.BOUGHT)
                .stream()).collect(Collectors.toList());
    }

    public List<ShopProduct> getProducts(String email, ProductState state) {
        Group group = userService.findGroupOfUser(email);
        return group.getShopProductList().stream()
                .filter(p -> p.getProductState() == state)
                .collect(Collectors.toList());
    }
}
