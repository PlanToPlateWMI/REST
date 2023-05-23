package pl.plantoplate.REST.dto.Response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;


@Getter
@Setter
@NoArgsConstructor
public class ProductDto {

        private long id;
        private String name;
        private String category;
        private String unit;

        public ProductDto (Product product){
            this.name = product.getName();
            this.unit = product.getUnit().name();
            this.id = product.getId();
            this.category = product.getCategory().getCategory();
        }
}
