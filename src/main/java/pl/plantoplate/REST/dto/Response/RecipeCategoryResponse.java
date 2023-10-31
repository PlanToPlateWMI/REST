package pl.plantoplate.REST.dto.Response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;

@Getter
@Setter
@NoArgsConstructor
public class RecipeCategoryResponse {

    private long id;
    private String name;

    public RecipeCategoryResponse(RecipeCategory recipeCategory){
        this.id = recipeCategory.getId();
        this.name = recipeCategory.getTitle();
    }
}
