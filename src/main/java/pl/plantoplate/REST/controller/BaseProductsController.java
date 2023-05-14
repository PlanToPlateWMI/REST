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
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.dto.Request.BaseProductRequest;
import pl.plantoplate.REST.dto.Response.BaseOfProductsResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.exception.*;
import pl.plantoplate.REST.service.ProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/products")
public class BaseProductsController {

    private final ProductService productService;
    private final UserService userService;


    public BaseProductsController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary="Get all products from base",description = "User can get list of all product in base - general and group custom products ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2 lists of products - general and group products",  content = @Content(
                    schema = @Schema(implementation = BaseOfProductsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<Object> getAllProduct(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = null;

        try{
            group = userService.findGroupOfUser(email);
        }catch (UserNotFound e){
            return new ResponseEntity<>(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        long groupId = group.getId();

        List<Product> productsOfGroup = productService.getProductsOfGroup(groupId);
        List<Product> generalProducts = productService.getProductsOfGroup(1L);

        BaseOfProductsResponse baseOfProductsResponse = new BaseOfProductsResponse(generalProducts, productsOfGroup);

        return new ResponseEntity<Object>(baseOfProductsResponse, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully updated ",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "User try to update product but it already exists (the same name and unit) or category or unit are not correct or user try to update" +
                    " general product of product not of his group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity updateProduct(@PathVariable long id, @RequestBody BaseProductRequest updateProductRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = null;
        try{
            group = userService.findGroupOfUser(email);
        }catch (UserNotFound e){
            return new ResponseEntity<>(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        if(Arrays.stream(Unit.values()).map(Enum::name).noneMatch(u -> u.equals(updateProductRequest.getUnit())) && updateProductRequest.getUnit()!=null){
            return new ResponseEntity<>(
                    new SimpleResponse("Unit is not correct. Available units : " + Arrays.toString(Unit.values())), HttpStatus.BAD_REQUEST);
        }

        try {
            productService.updateProduct(updateProductRequest, group, id);
        } catch (CategoryNotFound | AddTheSameProduct |ModifyGeneralProduct e) {
            return new ResponseEntity<>(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return ResponseEntity.ok().body(new SimpleResponse("Product was updated"));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Add new product to group",description = "User with Role ADMIN can add new product to group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully added ",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "User try to add product what already exists (the same name and unit) or category or unit are not correct",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity addProductToGroup(@RequestBody BaseProductRequest baseProductRequest){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Group group = null;
        try{
            group = userService.findGroupOfUser(email);
        }catch (UserNotFound e){
            return new ResponseEntity<>(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        try {
            productService.save(baseProductRequest.getName(), baseProductRequest.getCategory(), baseProductRequest.getUnit(), group);
        } catch (AddTheSameProduct | CategoryNotFound | WrongProductInShoppingList e) {
            return new ResponseEntity<>(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(new SimpleResponse("Product was saved"), HttpStatus.OK);

    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Delete group product - form list of group products and from shopping list",description = "User with Role ADMIN can delete group product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully deleted ",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "User try to delete general product or delete product not from his group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> deleteProductFromGroupBase(@PathVariable Long id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = null;
        try{
            group = userService.findGroupOfUser(email);
        }catch (UserNotFound e) {
            return new ResponseEntity<>(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
        long groupId = group.getId();

        try {
            productService.deleteById(id, groupId);
        } catch (ModifyGeneralProduct modifyGeneralProduct) {
            return new ResponseEntity<>(new SimpleResponse(modifyGeneralProduct.getMessage()), HttpStatus.BAD_REQUEST);

        }
        return ResponseEntity.ok(new SimpleResponse("Product with id [" + id + "] was deleted"));

    }
}
