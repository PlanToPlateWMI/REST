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
import pl.plantoplate.REST.dto.Request.AddShopProductRequest;
import pl.plantoplate.REST.dto.Request.AmountRequest;
import pl.plantoplate.REST.dto.Response.ShoppingProductResponse;
import pl.plantoplate.REST.dto.Response.ShoppingProductsResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.exception.WrongQueryParam;
import pl.plantoplate.REST.service.ShopProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingListController {


    private final ShopProductService shopProductService;
    private final UserService userService;


    @Autowired
    public ShoppingListController(ShopProductService shopProductService, UserService userService) {
        this.shopProductService = shopProductService;
        this.userService = userService;
    }


    @GetMapping("")
    @Operation(summary="Get shopping products depends on query param. ?bought=true - bought products. ?bought=false to buy products." +
            " Default value is true",
            description = "User can get list of products he wants to buy or he bought ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products depends on query param ",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> shoppingListOfGroup(@RequestParam(value = "bought", defaultValue = "true") @Parameter(schema = @Schema(description = "type of products",
            type = "string", allowableValues = {"false", "true"}))  String typeOfProduct){

        if(!typeOfProduct.equals("true") && !typeOfProduct.equals("false"))
            throw new WrongQueryParam("Query param values available : true and false");

        boolean type = Boolean.parseBoolean(typeOfProduct);

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> shopProductList = shopProductService.getProducts(email, type).stream()
                .map(ShoppingProductResponse::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(shopProductList, HttpStatus.OK);
    }

    @PostMapping()
    @Operation(summary= "Add Product to Shopping list (to buy type) by product id and amount.Return to buy shopping product list. ",
            description = "User can add product to shopping list ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully added",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to add product not of his group " +
                    "or amount is negative or 0",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> addProductToShoppingListFromBase(@RequestBody AddShopProductRequest productRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> shopProductList =
                shopProductService.addProductToList(productRequest.getId(), productRequest.getAmount(), email).stream()
                .map(ShoppingProductResponse::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(shopProductList, HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    @Operation(summary="Modify product amount in toBuy shopping list section. Return to buy shopping product list.",
            description = "User modify amount of product of toBuy list ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount was modifies",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to modify product not from toBuy list or" +
                    "amount is negative or 0",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> modifyShopProductAmount(@RequestBody AmountRequest amountRequest, @PathVariable long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> productDtos = shopProductService.modifyAmount(id, email, amountRequest.getAmount()).stream()
                .map(ShoppingProductResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }


    @PutMapping("/{id}")
    @Operation(summary= "Change is_bought parameter of product in shopping list. Return 2 lists of " +
            "shopping product list - toBuy and bought ",description = "User can change is_bought parameter of product in shopping list ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully change is_bought parametr",  content = @Content(
                    schema = @Schema(implementation =  ShoppingProductsResponse.class))),
            @ApiResponse(responseCode = "400", description = "User try to change product not of his group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<ShoppingProductsResponse> changeIsBoughtOfProductInShoppingList(@PathVariable long id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ShopProduct> shopProductList = shopProductService.changeIsBought(id, email);

        Map<Boolean, List<ShopProduct>> mapOfBoughtAndToBuyProducts = shopProductList.stream().
                collect(Collectors.partitioningBy(ShopProduct::isBought));

        ShoppingProductsResponse response = new ShoppingProductsResponse(mapOfBoughtAndToBuyProducts.get(true),
                mapOfBoughtAndToBuyProducts.get(false));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary= "Delete product from shopping list. If product was deleted from toBuy list - return shopping list of toBuy products." +
            "If product was deleted from bought list - return shopping list of bought products. ",description = "User can add delete to shopping list ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully deleted",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to delete product not of his group " +
                    "or amount is negative or 0",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> deleteProductFromShoppingList(@PathVariable long id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> productResponses = shopProductService.deleteProduct(id, email).stream()
                .map(ShoppingProductResponse::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }


}
