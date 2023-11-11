package pl.plantoplate.REST.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.controller.utils.ControllerUtils;
import pl.plantoplate.REST.dto.Request.PlanMealBasedOnRecipeRequest;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.service.MealService;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("api/meals")
public class MealController {

    private final ControllerUtils utils;
    private final MealService mealService;


    @PostMapping
    @Operation(summary = "Plan recipe to provided date, meal type, portions",
            description = "Plan recipe to provided date, meal type, portions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recipe has been planned", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = PlanMealBasedOnRecipeRequest.class)))),
            @ApiResponse(responseCode = "400", description = "Invalid request data : incorrect date, recipe with provided id not found, portion is not positive number," +
                    " incorrect meal type ", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> planMealsBasedOnRecipe(@RequestBody @Valid PlanMealBasedOnRecipeRequest planMeal){

        Group userGroup = utils.authorizeUserByEmail();
        mealService.planMeal(planMeal, userGroup);

        return new ResponseEntity<>(new SimpleResponse("Recipe [" + planMeal.getRecipeId() +"] was planned to " + planMeal.getMealType()), HttpStatus.CREATED);
    }
}
