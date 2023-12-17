package pl.plantoplate.REST.controller.recipe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pl.plantoplate.REST.controller.dto.converter.RecipeMealDetailsConverter;
import pl.plantoplate.REST.controller.dto.request.CreateRecipeRequest;
import pl.plantoplate.REST.controller.dto.response.CulinaryDetailsResponse;
import pl.plantoplate.REST.controller.dto.response.RecipeOverviewResponse;
import pl.plantoplate.REST.controller.dto.response.SimpleResponse;
import pl.plantoplate.REST.controller.dto.model.RecipeProductQty;
import pl.plantoplate.REST.controller.validator.RecipeValidator;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.service.RecipeService;
import pl.plantoplate.REST.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final RecipeValidator validator;

    @GetMapping()
    @Operation(summary = "Get list of recipes (optional sorting by category, level by request param)",
            description = "Get list of recipes (optional sorting by category, level by request param). If user is authorized - returned" +
                    "also recipes of user's group. ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of recipes", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecipeOverviewResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Category or level is wrong", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<RecipeOverviewResponse>> getAllRecipesOverview(@RequestParam(name = "category", required = false) @Parameter(schema = @Schema(description = "category of recipe", type = "string",
            allowableValues = {"Napoje", "Zupy", "Desery", "Dania główne", "Przekąski"})) String categoryName, @RequestParam(name = "level", required = false) @Parameter(schema = @Schema(description = "level of recipe", type = "string",
            allowableValues = {"EASY", "MEDIUM", "HARD"})) String level) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        validator.validateRecipeSortValues(categoryName, level);
        List<RecipeOverviewResponse> recipeOverviewRespons = recipeService.getAllRecipes(categoryName, level, email).stream()
                .map(RecipeOverviewResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(recipeOverviewRespons, HttpStatus.OK);
    }

    @GetMapping("/{recipeId}")
    @Operation(summary = "Get details of selected recipe",
            description = "Get details of selected recipe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of recipes", content = @Content(
                    schema = @Schema(implementation = RecipeOverviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Recipe not found", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<CulinaryDetailsResponse> getRecipeDetails(@PathVariable long recipeId) {

        RecipeProductQty recipe = recipeService.findRecipeDetailById(recipeId);

        return new ResponseEntity<>(RecipeMealDetailsConverter.convertRecipeToCulinaryDetailsResponse(recipe), HttpStatus.OK);
    }

    @GetMapping("/selected")
    @Operation(summary = "Get list of selected by group recipes (optional sorting by category by request param)",
            description = "Get list of selected by group recipes (optional sorting by category by request param)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of selected by group recipes", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecipeOverviewResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Category not found", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<RecipeOverviewResponse>> getAllSelectedByGroupRecipes(
            @RequestParam(name = "category", required = false) @Parameter(schema = @Schema(description = "category of recipe", type = "string", allowableValues = {"napoje", "zupy", "desery", "danie główne", "przystawki", "wege"})) String categoryName) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);
        List<RecipeOverviewResponse> recipeOverviewRespons = recipeService.getSelectedByGroupRecipes(categoryName, group).stream()
                .map(RecipeOverviewResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(recipeOverviewRespons, HttpStatus.OK);
    }

    @GetMapping("/owned")
    @Operation(summary = "Get list of owned by group recipes (optional sorting by category by request param)",
            description = "Get list of owned by group recipes (optional sorting by category by request param)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of owned by group recipes", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecipeOverviewResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Category not found", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<List<RecipeOverviewResponse>> getAllOwnedByGroupRecipes(
            @RequestParam(name = "category", required = false) @Parameter(schema = @Schema(description = "category of recipe", type = "string", allowableValues = {"napoje", "zupy", "desery", "danie główne", "przystawki", "wege"})) String categoryName) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);
        List<RecipeOverviewResponse> recipeOverviewRespons = recipeService.getOwnedByGroupRecipe(categoryName, group).stream()
                .map(RecipeOverviewResponse::new).collect(Collectors.toList());

        return new ResponseEntity<>(recipeOverviewRespons, HttpStatus.OK);
    }

    @PutMapping("/selected/{recipeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add recipe to selected of group (only with ADMIN role)",
            description = "Add recipe to selected of group (only with ADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe was added to selected", content = @Content(
                 schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Recipe was already added to selected of this group or " +
                    "recipe with given id doesn't exist", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> addRecipeToSelectedOfGroup(@PathVariable long recipeId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);

        recipeService.addRecipeToSelectedByGroup(recipeId, group, email);

        return new ResponseEntity<>(new SimpleResponse("Recipe was successfully added to selected"), HttpStatus.OK);
    }

    @DeleteMapping("/selected/{recipeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete recipe from selected of group (only with ADMIN role)",
            description = "Delete recipe from selected of group (only with ADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe was deleted from selected", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Recipe wasn't added to selected of this group or " +
                    "recipe with given id doesn't exist", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> deleteRecipeFromSelectedOfGroup(@PathVariable long recipeId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);

        recipeService.deleteRecipeFromSelectedByGroup(recipeId, group);

        return new ResponseEntity<>(new SimpleResponse("Recipe was successfully deleted from selected"), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create recipe (only with ADMIN role)",
            description = "Create recipe (only with ADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe was created", content = @Content(
                    schema = @Schema(implementation = CulinaryDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Wrong request data", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<CulinaryDetailsResponse> createRecipe(@RequestBody @Valid CreateRecipeRequest request){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);
        validator.validateCreateRecipe(request, group);

        RecipeProductQty recipe = recipeService.createRecipe(request, group);

        return ResponseEntity.ok(RecipeMealDetailsConverter.convertRecipeToCulinaryDetailsResponse(recipe));
    }

    @DeleteMapping("/{recipeId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete recipe from added by user(only with ADMIN role)",
            description = "Delete recipe from added by user(only with ADMIN role)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recipe was deleted", content = @Content(
                    schema = @Schema(implementation = CulinaryDetailsResponse.class))),
            @ApiResponse(responseCode = "400", description = "Recipe not exists or recipe not from user's group", content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity<SimpleResponse> deleteRecipe(@PathVariable long recipeId){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = userService.findGroupOfUser(email);

        recipeService.deleteRecipe(recipeId, group);

        return ResponseEntity.ok(new SimpleResponse("Recipe with id [" + recipeId +"] was deleted."));
    }

}
