package pl.plantoplate.REST.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.shoppinglist.Unit;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    private String name;
    private String unit;
    private String category;

}
