package pl.plantoplate.REST.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MealDetailsResponse extends CulinaryDetailsResponse{
    private long recipeId;
}
