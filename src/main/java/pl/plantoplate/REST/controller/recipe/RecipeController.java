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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.dto.Response.RecipeResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.service.RecipeService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;

    public RecipeController(RecipeService recipeService, UserService userService) {
        this.recipeService = recipeService;
        this.userService = userService;
    }


    @GetMapping()
    @Operation(summary = "Get list of recipes (optional sorting by category by request param)",
            description = "Get list of recipes (optional sorting by category by request param)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of recipes", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecipeResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Category not found", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<RecipeResponse>> getAllRecipes(@RequestParam(name = "category", required = false) @Parameter(schema = @Schema(description = "category of recipe", type = "string", allowableValues = {"napoje", "zupy", "desery", "danie główne", "przystawki", "wege"})) String categoryName) {

        List<RecipeResponse> recipeResponses = recipeService.getAllRecipes(categoryName).stream()
                .map(RecipeResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(recipeResponses, HttpStatus.OK);
    }

    @GetMapping("/selected")
    @Operation(summary = "Get list of selected by group recipes (optional sorting by category by request param)",
            description = "Get list of selected by group recipes (optional sorting by category by request param)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of selected by group recipes", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecipeResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Category not found", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<RecipeResponse>> getAllSelectedByGroupRecipes(
            @RequestParam(name = "category", required = false) @Parameter(schema = @Schema(description = "category of recipe", type = "string", allowableValues = {"napoje", "zupy", "desery", "danie główne", "przystawki", "wege"})) String categoryName) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);
        List<RecipeResponse> recipeResponses = recipeService.getSelectedByGroupRecipes(categoryName, group).stream()
                .map(RecipeResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(recipeResponses, HttpStatus.OK);
    }

}
