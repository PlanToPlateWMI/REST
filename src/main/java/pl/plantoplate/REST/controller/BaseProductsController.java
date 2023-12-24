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
import pl.plantoplate.REST.controller.dto.request.BaseProductRequest;
import pl.plantoplate.REST.controller.dto.response.ProductResponse;
import pl.plantoplate.REST.controller.dto.response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.service.GroupService;
import pl.plantoplate.REST.service.ProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * REST controller with Endpoints connected Products {@link pl.plantoplate.REST.entity.product.Product}
 */
@RestController
@RequestMapping("api/products")
public class BaseProductsController {

    private final ProductService productService;
    private final UserService userService;
    private final GroupService groupService;


    public BaseProductsController(ProductService productService, UserService userService, GroupService groupService) {
        this.productService = productService;
        this.userService = userService;
        this.groupService = groupService;
    }


    /**
     * Returns products depends on request param ?type.
     * If type = all - returns list of general products (available for all groups) and products of group (created by members of group).
     * If type = group - return list of products of group (created by members of group)
     * Default value of request param is all
     * @param typeOfProduct type of products to return
     * @return ResponseEntity parametrized with List of {@link ProductResponse} with id, name, category and unit of products (type of products depends on request param)
     */
    @GetMapping
    @Operation(summary="Get list of products of group depends on request param ?type= ",
            description = "Get list of products depends on request param : ?type= all - list of general products (available for all groups) and products of group (created by members of group)," +
                    "?type = group - list of products of group (created by members of group). Default value of request param is all")
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
            throw new WrongRequestData("Query values available - ALL and GROUP");
        }

        return generateListOfProductDtoDependsOnTypeOfProducts(group, BaseProductType.valueOf(typeOfProduct));
    }


    /**
     * User with role ADMIN adds new product for his group by provided product name, category and unit
     * @param baseProductRequest DTO with name, category and unit of product to add
     * @return ResponseEntity parametrized with List of {@link ProductResponse} with id, name, category and unit of products of user's group
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="User with role ADMIN adds new product to list of group's products",description = "User with role ADMIN adds new product to list of group's products." +
            "If products with provided name and unit exists on list of all products of group - returns error.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product successfully added ",   content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
            @ApiResponse(responseCode = "400", description = "User try to add product what already exists (the same name and unit) or category or unit are not correct",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<ProductResponse>> addProductToGroup(@RequestBody BaseProductRequest baseProductRequest){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Group group = userService.findGroupOfUser(email);
        productService.save(baseProductRequest.getName(), baseProductRequest.getCategory(), baseProductRequest.getUnit(), group);

        return generateListOfProductDtoDependsOnTypeOfProducts(group, BaseProductType.group);
    }


    /**
     * User with role ADMIN changes product of his group by product id. User can change unit, category and name of products
     * @param id id of product to change
     * @param updateProductRequest DTO with name, category and unit to update
     * @return ResponseEntity parametrized with List of {@link ProductResponse} with id, name, category and unit of products of user's group
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="User with role ADMIN updates existing product of his group.",description = "User with role ADMIN updates existing product of his group." +
            "If products with provided name and unit exists on list of all products of group - returns error.")
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

        return generateListOfProductDtoDependsOnTypeOfProducts(group, BaseProductType.group);
    }


    /**
     * User with role ADMIN deletes product of his group by product id. Also deleted all Shopping Products {@link pl.plantoplate.REST.entity.shoppinglist.ShopProduct}
     * with deleted product parametr
     * @param id id of product to change
     * @return ResponseEntity parametrized with List of {@link ProductResponse} with id, name, category and unit of products of user's group
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="User with role ADMIN deletes existing product of his group.",description = "User with role ADMIN deletes existing product of his group." +
            "Also deleted Shopping Products with deleted Product parametr.")
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

        return generateListOfProductDtoDependsOnTypeOfProducts(group, BaseProductType.group);

    }

    /**
     * Returns ResponseEntity parametrized with List of {@link ProductResponse} with id, name, category and unit of products of provided product type
     * If group of user has id equals 1 then returns list of general prodcuts
     * @param usersGroup group of users from that product returns
     * @param productsType type of products to return
     * @return ResponseEntity parametrized with List of {@link ProductResponse} with id, name, category and unit of products of provided product type
     */
    private ResponseEntity<List<ProductResponse>> generateListOfProductDtoDependsOnTypeOfProducts(Group usersGroup, BaseProductType productsType) {

        List<Product> productsOfGroup = productService.getProductsOfGroup(usersGroup);
        List<Product> modifiedProductsOfGroup = new ArrayList<>(productsOfGroup);
        modifiedProductsOfGroup.sort(Comparator.comparing(Product::getName));

        // if group == 1 it means that it is group of moderators and return always general products
        if(usersGroup.getId() == 1L){
            return new ResponseEntity<>(modifiedProductsOfGroup.stream().map(ProductResponse::new).collect(Collectors.toList()), HttpStatus.OK);
        }

        if(productsType.equals(BaseProductType.all)){
            List<Product> generalProducts = productService.getProductsOfGroup(groupService.findById(1L));
            Stream<Product> allProductsSorted = Stream.concat(generalProducts.stream(), productsOfGroup.stream()).sorted(Comparator.comparing(Product::getName));

            return new ResponseEntity<>(allProductsSorted.map(ProductResponse::new).collect(Collectors.toList()), HttpStatus.OK);
        }
        return new ResponseEntity<>(modifiedProductsOfGroup.stream().map(ProductResponse::new).collect(Collectors.toList()), HttpStatus.OK);
    }
}
