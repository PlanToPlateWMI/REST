package pl.plantoplate.REST.controller.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CulinaryDetailsResponse {

    private long id;
    private String title;
    private String image;
    private String source;
    private int time;
    private String level;
    private long portions;
    private List<String> steps;
    private boolean isVege;
    private List<IngredientResponse> ingredients;
}
