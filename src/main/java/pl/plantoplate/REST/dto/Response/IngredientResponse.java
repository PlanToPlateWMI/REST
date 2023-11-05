package pl.plantoplate.REST.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientResponse {

    private long id;
    private float quantity;
    private String ingredientName;
    private String unit;

    public IngredientResponse(Product product, float qty){
        this.ingredientName = product.getName();
        this.id = product.getId();
        this.quantity = qty;
        this.unit = product.getUnit().name();
    }

}
