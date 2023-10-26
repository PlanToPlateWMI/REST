package pl.plantoplate.REST.controller.recipe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.dto.Response.RecipeResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.service.RecipeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }


    @GetMapping("/all")
    @Operation(summary = "Get list of Recipes sorted by Category",
            description = "Get list of Recipes sorted by Category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of recipes", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecipeResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Category/User not found", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<RecipeResponse>> getAllRecipes(@RequestParam(name = "category", required = false)
                                                              @Parameter(schema = @Schema(description = "category of recipe", type = "string", allowableValues = {"napoje", "zupy",
                                                                      "desery", "danie główne", "przystawki", "wege"})) String categoryName) {

        List<RecipeResponse> recipeResponses = recipeService.getAllRecipesByCategory(categoryName).stream()
                .map(RecipeResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(recipeResponses, HttpStatus.OK);

    }
}
