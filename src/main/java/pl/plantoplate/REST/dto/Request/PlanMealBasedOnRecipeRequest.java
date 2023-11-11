package pl.plantoplate.REST.dto.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
public class PlanMealBasedOnRecipeRequest {

    private String mealType;
    @Min(value = 1, message = "The number of portions must be greater than 0")
    private int portions;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
    private long recipeId;
    private List<Long> ingredientsId;
}
