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
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.dto.Request.AddShopProductRequest;
import pl.plantoplate.REST.dto.Response.ShoppingProductResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.service.PantryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/pantry/")
public class PantryController {

    private final PantryService pantryService;

    public PantryController(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    @GetMapping()
    @Operation(summary= "Get all product of pantry ",description = "User get list of product he has at home ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products of pantry",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation =  ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User not found",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> getProductsFromPantry(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ShopProduct> shopProductList = pantryService.findProductsFromPantry(email);

        return new ResponseEntity<>(shopProductList.stream().map(ShoppingProductResponse::new).collect(Collectors.toList()),
                HttpStatus.OK);
    }


    @PostMapping("/transfer")
    @Operation(summary= "Transfer products from shopping list to pantry ",description = "User can move product from shopping list bought to pantry ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products of pantry",  content = @Content(
                    schema = @Schema(implementation =  Long.class))),
            @ApiResponse(responseCode = "400", description = "Product not found, User try to move not his product or product wasn't bought",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> transferProductsFromShoppingListBoughtToPantry(@RequestBody long[] toPantryId){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ShopProduct> shopProductList = pantryService.transferProductToPantry(email, toPantryId);

        return new ResponseEntity<>(shopProductList.stream().map(ShoppingProductResponse::new).collect(Collectors.toList()),
                HttpStatus.OK);
    }

    @PostMapping()
    @Operation(summary= "Add Product to pantry by product id and amount. Return list of product in pantry. ",
            description = "User can add product to pantry ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully added",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to add product not of his group " +
                    "or amount is negative or 0",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> addProductToPantryFromBase(@RequestBody AddShopProductRequest productRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ShoppingProductResponse> pantryProductList =
                pantryService.addProductToPantry(productRequest.getId(), productRequest.getAmount(), email).stream()
                        .map(ShoppingProductResponse::new)
                        .collect(Collectors.toList());

        return new ResponseEntity<>(pantryProductList, HttpStatus.OK);
    }



}
