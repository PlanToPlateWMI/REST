package pl.plantoplate.REST.controller.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IngredientQtyRequest {

    private long id;
    private float qty;

    @Override
    public String toString() {
        return "IngredientQtyRequest{" +
                "id=" + id +
                ", qty=" + qty +
                '}';
    }
}
