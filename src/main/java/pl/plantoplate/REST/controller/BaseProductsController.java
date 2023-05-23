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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.utils.BaseProductType;
import pl.plantoplate.REST.dto.Request.BaseProductRequest;
import pl.plantoplate.REST.dto.Response.ProductResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.exception.WrongQueryParam;
import pl.plantoplate.REST.service.ProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Operation(summary="Get products of base depends on query param (default value without query param - all): type = all - all products, " +
            "type = group - product of group",description = "User can get list of all products or group products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of products depends on query param value",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ProductResponse>> getAllProduct(@RequestParam(value = "type", defaultValue = "all") @Parameter(schema = @Schema(description = "type of products : all - list of all products, group - list of groups products",
                            type = "string", allowableValues = {"all", "group"}))  String typeOfProduct){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);

        try{
            BaseProductType.valueOf(typeOfProduct);
        }
        catch (IllegalArgumentException e){
            throw new WrongQueryParam("Query values available - ALL and GROUP");
        }

        return generateListOfProductDtoDependsOnTypeOfProducts(group.getId(), BaseProductType.valueOf(typeOfProduct));
    }


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Add new product to group. Return list of products of group",description = "User with Role ADMIN can add new product to group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully added ",   content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to add product what already exists (the same name and unit) or category or unit are not correct",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ProductResponse>> addProductToGroup(@RequestBody BaseProductRequest baseProductRequest){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Group group = userService.findGroupOfUser(email);
        productService.save(baseProductRequest.getName(), baseProductRequest.getCategory(), baseProductRequest.getUnit(), group);

        return generateListOfProductDtoDependsOnTypeOfProducts(group.getId(), BaseProductType.group);
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Update product of group. Return list of products of group.",description = "User can update product of his group")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully updated ",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to update product but it already exists (the same name and unit) or category or unit are not correct or user try to update" +
                    " general product of product not of his group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ProductResponse>> updateProduct(@PathVariable long id, @RequestBody BaseProductRequest updateProductRequest) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);

        productService.updateProduct(updateProductRequest.getName(), updateProductRequest.getUnit(), updateProductRequest.getCategory(), group, id);

        return generateListOfProductDtoDependsOnTypeOfProducts(group.getId(), BaseProductType.group);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Delete group product - from list of group products and from shopping list. Return list of products of group.",description = "User with Role ADMIN can delete group product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully deleted ",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to delete general product or delete product not from his group",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ProductResponse>> deleteProductFromGroupBase(@PathVariable Long id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);

        long groupId = group.getId();

        productService.deleteById(id, groupId);

        return generateListOfProductDtoDependsOnTypeOfProducts(group.getId(), BaseProductType.group);

    }

    private ResponseEntity<List<ProductResponse>> generateListOfProductDtoDependsOnTypeOfProducts(long groupId, BaseProductType productsType) {

        List<Product> productsOfGroup = productService.getProductsOfGroup(groupId);

        // if group == 1 it means that it is group of moderators and return always  general products
        if(groupId == 1L){
            return new ResponseEntity<>(productsOfGroup.stream().map(ProductResponse::new).collect(Collectors.toList()), HttpStatus.OK);
        }

        if(productsType.equals(BaseProductType.all)){
            List<Product> generalProducts = productService.getProductsOfGroup(1L);

            return new ResponseEntity<>(Stream.concat(productsOfGroup.stream(), generalProducts.stream()).map(ProductResponse::new).collect(Collectors.toList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(productsOfGroup.stream().map(ProductResponse::new).collect(Collectors.toList()), HttpStatus.OK);
    }
}
