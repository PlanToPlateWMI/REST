package pl.plantoplate.REST.controller.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;


@Getter
@Setter
@NoArgsConstructor
public class ProductResponse {

        private long id;
        private String name;
        private String category;
        private String unit;

        public ProductResponse(Product product){
            this.name = product.getName();
            this.unit = product.getUnit().name();
            this.id = product.getId();
            this.category = product.getCategory().getCategory();
        }
}
