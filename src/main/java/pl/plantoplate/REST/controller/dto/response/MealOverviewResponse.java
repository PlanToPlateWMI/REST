package pl.plantoplate.REST.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class MealOverviewResponse {

    private long mealId;
    private String recipeTitle;
    private int time;
    private String mealType;
    private String image;
    private boolean isVege;
    private boolean isPrepared;
}
