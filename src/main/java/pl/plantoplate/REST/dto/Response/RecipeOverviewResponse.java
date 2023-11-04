package pl.plantoplate.REST.dto.Response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.recipe.Recipe;

@Getter
@Setter
@NoArgsConstructor
public class RecipeOverviewResponse {

    private long id;
    private String title;
    private int time;
    private String level;
    private String image;
    private String categoryName;
    private boolean isVege;

    public RecipeOverviewResponse(Recipe recipe) {
        this.id = recipe.getId();
        this.title = recipe.getTitle();
        this.time = recipe.getTime();
        this.level = recipe.getLevel().name();
        this.image = recipe.getImage_source();
        this.categoryName = recipe.getCategory().getTitle();
        this.isVege = recipe.isVege();
    }

}
