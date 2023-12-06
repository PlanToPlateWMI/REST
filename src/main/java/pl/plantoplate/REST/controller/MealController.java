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
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.dto.converter.RecipeMealDetailsConverter;
import pl.plantoplate.REST.controller.dto.model.MealProductQty;
import pl.plantoplate.REST.controller.dto.request.PlanMealBasedOnRecipeRequest;
import pl.plantoplate.REST.controller.dto.response.MealDetailsResponse;
import pl.plantoplate.REST.controller.dto.response.MealOverviewResponse;
import pl.plantoplate.REST.controller.dto.response.SimpleResponse;
import pl.plantoplate.REST.controller.utils.ControllerUtils;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.service.MealService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/meals")
public class MealController {

    private final ControllerUtils utils;
    private final MealService mealService;


    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Plan recipe to provided date, meal type, portions (only with ADMIN role)",
            description = "Plan recipe to provided date, meal type, portions (only with ADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe has been planned", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data : incorrect date, recipe with provided id not found, portion is not positive number," +
                    " incorrect meal type ", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> planMealsBasedOnRecipe(@RequestBody @Valid PlanMealBasedOnRecipeRequest planMeal){

        Group userGroup = utils.authorizeUserByEmail();
        mealService.planMeal(planMeal, userGroup, SecurityContextHolder.getContext().getAuthentication().getName());

        return new ResponseEntity<>(new SimpleResponse("Recipe [" + planMeal.getRecipeId() +"] was planned to " + planMeal.getMealType()), HttpStatus.CREATED);
    }

    @GetMapping()
    @Operation(summary = "Get planned meal overview by provided date",
            description = "Get planned meal overview by provided date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get planned meal overview by provided date", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = MealOverviewResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid date format: yyyy-MM-dd", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<MealOverviewResponse>> getMealOverviewByDate(@RequestParam(name = "date") @DateTimeFormat(pattern = "yyyy-MM-dd")
                                                                                @Schema(type = "string", pattern = "yyyy-MM-dd", example = "2023-02-29") LocalDate localDate){

        Group userGroup = utils.authorizeUserByEmail();
        List<MealOverviewResponse> response = mealService.getMealOverviewByDate(localDate, userGroup);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{mealId}")
    @Operation(summary = "Get planned meal details",
            description = "Get planned meal details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get planned meal details", content = @Content(
                  schema = @Schema(implementation = MealDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Meal with provided id not in group", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<MealDetailsResponse> getMealDetails(@PathVariable("mealId") long id){

        Group group = utils.authorizeUserByEmail();
        MealProductQty mealDetailsIngredientQty = mealService.findMealDetailById(id, group);

        return ResponseEntity.ok(RecipeMealDetailsConverter.convertMealsToMealDetailsResponse(mealDetailsIngredientQty));
    }

    @DeleteMapping("/{mealId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete planned meal (only with ADMIN role)",
            description = "Delete planned meal (only with ADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get planned meal details", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Meal with provided id not in group/not found", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> deleteMeal(@PathVariable("mealId") long mealId){

        Group group = utils.authorizeUserByEmail();
        mealService.deleteMealById(mealId, group);

        return ResponseEntity.ok(new SimpleResponse("Meal " + mealId + " of group [" + group.getId() + "] was successfully deleted"));
    }

    @PutMapping("/prepare/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Prepare planned meal (only with ADMIN role)",
            description = "Prepare planned meal (only with ADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Meal prepared successfully", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Meal with provided id not in group/not found or was already preapred", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> prepareMeal(@PathVariable("id") long mealId){

        Group group = utils.authorizeUserByEmail();
        mealService.prepareMeal(mealId, group);

        return ResponseEntity.ok(new SimpleResponse("Meal with id [" + mealId + "] was prepared"));
    }
}
