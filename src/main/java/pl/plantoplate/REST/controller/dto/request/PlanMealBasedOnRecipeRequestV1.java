package pl.plantoplate.REST.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import javax.validation.constraints.Min;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class PlanMealBasedOnRecipeRequestV1 {

    private long recipeId;
    @Min(value = 1L, message = "The number of portions must be greater than 0")
    private int portions;
    private List<Long> ingredients;
    private String mealType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
}