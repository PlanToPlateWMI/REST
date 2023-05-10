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
import pl.plantoplate.REST.dto.Response.BaseOfProductsDto;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.exception.DeleteGeneralProduct;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.service.ProductService;
import pl.plantoplate.REST.service.UserService;

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
                    schema = @Schema(implementation = BaseOfProductsDto.class))),
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

        BaseOfProductsDto baseOfProductsDto = new BaseOfProductsDto(generalProducts, productsOfGroup);

        return new ResponseEntity<Object>(baseOfProductsDto, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary="Delete group product",description = "User with Role ADMIN can delete group product")
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
        } catch (DeleteGeneralProduct deleteGeneralProduct) {
            return new ResponseEntity<>(new SimpleResponse(deleteGeneralProduct.getMessage()), HttpStatus.BAD_REQUEST);

        }
        return ResponseEntity.ok(new SimpleResponse("Product with id [" + id + "] was deleted"));

    }
}
