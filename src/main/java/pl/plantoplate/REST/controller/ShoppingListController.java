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

package pl.plantoplate.REST.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.dto.request.AddRecipeToShoppingList;
import pl.plantoplate.REST.controller.dto.request.AddShopProductRequest;
import pl.plantoplate.REST.controller.dto.request.AmountRequest;
import pl.plantoplate.REST.controller.dto.response.ShoppingProductResponse;
import pl.plantoplate.REST.controller.dto.response.ShoppingProductsResponse;
import pl.plantoplate.REST.controller.dto.response.SimpleResponse;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.service.ShoppingListService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller with Endpoints connected Products {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct}
 * with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY} and {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT} states
 */
@RestController
@RequestMapping("/api/shopping")
public class ShoppingListController {


    private final ShoppingListService shoppingListService;
    private final UserService userService;


    @Autowired
    public ShoppingListController(ShoppingListService shoppingListService, UserService userService) {
        this.shoppingListService = shoppingListService;
        this.userService = userService;
    }


    /**
     * Returns ShopProducts {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT} or {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY}
     * depends on request param bought of user's group list.
     * If ?bought=true - returns with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT}
     * If ?bought=false - returns with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY}
     * Default value of request param is true
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state depends on request param ?bought
     */
    @GetMapping("")
    @Operation(summary="Get list of shopProducts with BUY or BOUGHT state of user's group",
            description = "Get list of shopProducts with BUY or BOUGHT state of user's group depends on request param ?bought." +
                    "If ?bought=true - returns with state BOUGHT." +
                    "If ?bought=false - returns with state BUY. Default value of request param is true")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products depends on query param ",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> shoppingListOfGroup(@RequestParam(value = "bought", defaultValue = "true") @Parameter(schema = @Schema(description = "type of products",
            type = "string", allowableValues = {"false", "true"}))  String typeOfProduct){

        if(!typeOfProduct.equals("true") && !typeOfProduct.equals("false"))
            throw new WrongRequestData("Query param values available : true and false");

        ProductState productState = Boolean.parseBoolean(typeOfProduct) ? ProductState.BOUGHT : ProductState.BUY;

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> shopProductList = shoppingListService.getProducts(email, productState).stream()
                .map(ShoppingProductResponse::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(shopProductList, HttpStatus.OK);
    }

    /**
     * Adds ShopProduct {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY} to user's group list
     * by provided {@link pl.plantoplate.REST.entity.product.Product} id and amount
     * @param productRequest DTO with product id and amount to add
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state BUY of user's group
     */
    @PostMapping()
    @Operation(summary= "Adds ShopProduct with state BUY to list of user's group ShopProducts ",
            description = "Adds ShopProduct with state BUY to list of user's group ShopProducts by provided id of Product and amount. Amount cannot be negative or zero." +
                    " Returns updated list of ShopProducts with BUY state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully added",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to add product not of his group " +
                    "or amount is negative or 0",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> addProductToShoppingListFromBase(@RequestBody AddShopProductRequest productRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> shopProductList =
                shoppingListService.addProductToShoppingList(productRequest.getId(), productRequest.getAmount(), email).stream()
                .map(ShoppingProductResponse::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(shopProductList, HttpStatus.OK);
    }

    @PostMapping("/recipe")
    @Operation(summary= "Add ingredients of recipe based on number of portions with state BUY to list of user's group ShopProducts",
            description = "Add ingredients of recipe based on number of portions with state BUY to list of user's group ShopProducts)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products were successfully added",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Recipe with provided id not found/number of portions less than 1",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> addProductToShoppingListBasedOnRecipeIdAndPortions(
            @RequestBody AddRecipeToShoppingList request, @PathVariable(value = "synchronize", required = false) boolean synchronize) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> shopProductList = shoppingListService.addProductsToShoppingList(request, email).stream()
                        .map(ShoppingProductResponse::new)
                        .collect(Collectors.toList());

        return new ResponseEntity<>(shopProductList, HttpStatus.OK);
    }


    /**
     * Changes amount ShopProduct {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY}
     * of user's group list
     * @param amountRequest new amount
     * @param id id of ShopProduct to change
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state BUY of user's group
     */
    @PatchMapping("/{id}")
    @Operation(summary="Changes amount of ShopProduct with state BUY of user's group",
            description = "Changes amount of ShopProduct with state BUY of user's group by provided amount. Amount cannot be negative or zero. Returns updated list of ShopProducts with BUY state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount was modifies",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to modify product not from toBuy list or" +
                    "amount is negative or 0",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> modifyShopProductAmount(@RequestBody AmountRequest amountRequest, @PathVariable long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> productDtos = shoppingListService.modifyAmount(id, email, amountRequest.getAmount()).stream()
                .map(ShoppingProductResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }


    /**
     * Changes state of ShopProduct {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct}. Is state was {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY} - changes
     * to {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT} and vice versa
     * @param id id of ShopProduct to change state
     * @return ResponseEntity parametrized with 2 Lists bought and buy - of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state BUY and BOUGH of user's group
     */
    @PutMapping("/{id}")
    @Operation(summary= "Changes state of ShopProduct of user's group from BUY to BOUGHT and vice versa.",
            description = "Changes state of ShopProduct of user's group from BUY to BOUGHT and vice versa. Returns updated list of ShopProducts with BUY and BOUGHT state")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully change is_bought parametr",  content = @Content(
                    schema = @Schema(implementation =  ShoppingProductsResponse.class))),
            @ApiResponse(responseCode = "400", description = "User try to change product not of his group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<ShoppingProductsResponse> changeIsBoughtOfProductInShoppingList(@PathVariable long id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ShopProduct> shopProductList = shoppingListService.changeProductStateOnShoppingList(id, email);

        Map<ProductState, List<ShopProduct>> mapOfBoughtAndToBuyProducts = shopProductList.stream().
                collect(Collectors.groupingBy(ShopProduct::getProductState));

        ShoppingProductsResponse response = new ShoppingProductsResponse(mapOfBoughtAndToBuyProducts.get(ProductState.BOUGHT),
                mapOfBoughtAndToBuyProducts.get(ProductState.BUY));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    /**
     * Deletes ShopProduct {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BUY} or {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT}
     * from user's group by provided id
     * @param id id of ShopProduct with state BUY or BOUGHT ro delete
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with the same state as state of deleted ShopProduct"
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary= "Deletes ShopProduct with state BUY or BOUGH from user's group list.",
            description = "Deletes ShopProduct with state BUY or BOUGH  from user's group list by provided id. " +
                    "Returns updated list of ShopProducts with the same state as state of deleted ShopProduct")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully deleted",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to delete product not of his group or product not exists",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> deleteProductFromShoppingList(@PathVariable long id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> productResponses = shoppingListService.deleteProduct(id, email).stream()
                .map(ShoppingProductResponse::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }


}
