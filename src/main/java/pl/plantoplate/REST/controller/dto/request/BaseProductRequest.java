package pl.plantoplate.REST.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseProductRequest {

    private String name;
    private String unit;
    private String category;

}
