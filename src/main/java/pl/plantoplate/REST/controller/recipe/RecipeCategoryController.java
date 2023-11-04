package pl.plantoplate.REST.controller.recipe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.dto.Response.RecipeCategoryResponse;
import pl.plantoplate.REST.service.RecipeCategoryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/recipe-categories")
public class RecipeCategoryController {

    private final RecipeCategoryService recipeCategoryService;

    public RecipeCategoryController(RecipeCategoryService recipeCategoryService) {
        this.recipeCategoryService = recipeCategoryService;
    }

    @GetMapping
    @Operation(summary = "Get list of Categories",
            description = "Get list of Categories")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of categories", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = RecipeCategoryResponse.class))))})
    public ResponseEntity<List<RecipeCategoryResponse>> getAllRecipeCategories() {
        return new ResponseEntity<>(recipeCategoryService.findAll().stream()
                .map(RecipeCategoryResponse::new).collect(Collectors.toList()), HttpStatus.OK);
    }
}
