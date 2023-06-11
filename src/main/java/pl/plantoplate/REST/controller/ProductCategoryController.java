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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller with Endpoints connected with Categories of products
 */
@RestController
@RequestMapping("api/categories")
public class ProductCategoryController {

    private final CategoryService categoryService;


    public ProductCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Returns all existing product categories
     * @return ResponseEntity parametrized with List of Strings with names of categories
     */
    @GetMapping
    @Operation(summary="Get all categories",description = "Get list of all existing categories.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list of categories",  content = @Content(
                    array = @ArraySchema ( schema = @Schema(implementation = String.class))))})
    public ResponseEntity getAllCategories(){

        List<Category> categoryList = categoryService.findAll();
        List<String> categoriesName = categoryList.stream().map(e -> e.getCategory()).collect(Collectors.toList());

        return ResponseEntity.ok(categoriesName);
    }


}
