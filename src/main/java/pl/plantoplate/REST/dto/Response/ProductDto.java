package pl.plantoplate.REST.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {

    private String name;
    private String unit;

    public ProductDto (Product product){
        this.name = product.getName();
        this.unit = product.getUnit().name();
    }
}
