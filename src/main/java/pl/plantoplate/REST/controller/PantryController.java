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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.dto.request.AddShopProductRequest;
import pl.plantoplate.REST.controller.dto.request.AmountRequest;
import pl.plantoplate.REST.controller.dto.response.ShoppingProductResponse;
import pl.plantoplate.REST.controller.dto.response.SimpleResponse;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.service.PantryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller with Endpoints connected Products {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct}
 * with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY} state
 */
@RestController
@RequestMapping("api/pantry/")
public class PantryController {

    private final PantryService pantryService;

    public PantryController(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    /**
     * Returns ShopProducts {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY}
     * state of user's group
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state PANTRY of user's group
     */
    @GetMapping()
    @Operation(summary="Get list of shopProducts with PANTRY state of user's group",
            description = "Get list of shopProducts with PANTRY state of user's group")
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


    /**
     * Changes state of ShopProducts {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct}  provided by array of ids
     * with {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#BOUGHT} state of user's group to new state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY}
     * @param toPantryId arrays of idis of ShopProduct with state BOUGHT
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state PANTRY of user's group
     */
    @PostMapping("/transfer")
    @Operation(summary= "Changes state of ShopProducts from BOUGHT to PANTRY",
            description = "Change state of ShopProducts from BOUGHT to PANTRY of user's group by provided arrays of idis. Returns updated list of ShopProducts with PANTRY state.")
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

    /**
     * Adds ShopProduct {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY} to user's group list
     * by provided {@link pl.plantoplate.REST.entity.product.Product} id and amount
     * @param productRequest DTO with product id and amount to add
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state PANTRY of user's group
     */
    @PostMapping()
    @Operation(summary= "Adds ShopProduct with state PANTRY to list of user's group ShopProducts ",
            description = "Adds ShopProduct with state PANTRY to list of user's group ShopProducts by provided id of Product and amount. Amount cannot be negative or zero." +
                    " Returns updated list of ShopProducts with PANTRY state.")
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


    /**
     * Deletes ShopProduct {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY}
     * from user's group by provided id
     * @param id id of ShopProduct with state PANTRY ro delete
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state PANTRY of user's group
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary= "Deletes ShopProduct with state PANTRY from user's group list.",
            description = "Deletes ShopProduct with state PANTRY from user's group list by provided id. Returns updated list of ShopProducts with PANTRY state.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product was successfully deleted",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to delete product not of his group or product not exists",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> deleteProductFromPantry(@PathVariable long id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        List<ShoppingProductResponse> productResponses = pantryService.deleteProduct(id, email).stream()
                .map(ShoppingProductResponse::new)
                .collect(Collectors.toList());

        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }


    /**
     * Changes amount ShopProduct {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct} with state {@link pl.plantoplate.REST.entity.shoppinglist.ProductState#PANTRY}
     * of user's group list
     * @param amountRequest new amount
     * @param id id of ShopProduct to change
     * @return ResponseEntity parametrized with List of {@link ShoppingProductResponse} with id, name, category, unit and amount of products with state PANTRY of user's group
     */
    @PatchMapping("/{id}")
    @Operation(summary="Changes amount of ShopProduct with state PANTRY of user's group",
            description = "Changes amount of ShopProduct with state PANTRY of user's group by provided amount. Amount cannot be negative or zero. Returns updated list of ShopProducts with PANTRY state" +
                    "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Amount was modifies",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to modify product not from his pantry or" +
                    "amount is negative or 0",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ShoppingProductResponse>> modifyPantryProductAmount(@RequestBody AmountRequest amountRequest, @PathVariable long id) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        List<ShoppingProductResponse> productDtos = pantryService.modifyAmount(id, email, amountRequest.getAmount()).stream()
                .map(ShoppingProductResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(productDtos, HttpStatus.OK);
    }



}
