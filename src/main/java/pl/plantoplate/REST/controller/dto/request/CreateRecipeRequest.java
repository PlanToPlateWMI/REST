package pl.plantoplate.REST.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateRecipeRequest {

    @NotBlank(message = "Title of recipe cannot be blank")
    private String title;
    private String level;
    @Min(value = 1, message = "The time of recipe should be greater than 0")
    private int time;
    @NotBlank(message = "Steps of recipe cannot be blank")
    private String steps;
    @Min(value = 1, message = "The number of portions must be greater than 0")
    private int portions;
    private boolean isVege;
    private long category;
    @NotNull(message = "Should pass ingredients of recipe")
    private List<IngredientQtyRequest> ingredients;

    @Override
    public String toString() {
        return "CreateRecipeRequest{" +
                "title='" + title + '\'' +
                ", level='" + level + '\'' +
                ", time=" + time +
                ", steps='" + steps + '\'' +
                ", portions=" + portions +
                ", isVege=" + isVege +
                ", category=" + category +
                ", ingredients=" + ingredients +
                '}';
    }
}
