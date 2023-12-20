package pl.plantoplate.REST.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AddRecipeToShoppingList {

    private long recipeId;
    @Min(value = 1, message = "The number of portions must be greater than 0")
    private int portions;
    private List<Long> ingredientsId;

}
