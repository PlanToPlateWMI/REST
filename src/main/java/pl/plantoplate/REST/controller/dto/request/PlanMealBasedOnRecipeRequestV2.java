package pl.plantoplate.REST.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class PlanMealBasedOnRecipeRequestV2 {

    private long recipeId;
    @Min(value = 1, message = "The number of portions must be greater than 0")
    private int portions;
    private String mealType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private List<Long> ingredients;
    private boolean isProductsAdd;
    private boolean isSynchronize;
}
