package pl.plantoplate.REST.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MealOverviewResponse {

    private long mealId;
    private String recipeTitle;
    private int time;
    private String mealType;
}
