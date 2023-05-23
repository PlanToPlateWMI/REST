package pl.plantoplate.REST.dto.Response;

import lombok.*;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShoppingProductResponse {
        private Long id;
        private String name;
        private String category;
        private int amount;
        private String unit;


        public ShoppingProductResponse(ShopProduct productGroup){
            this.name = productGroup.getProduct().getName();
            this.id = productGroup.getId();
            this.amount = productGroup.getAmount();
            this.unit = productGroup.getProduct().getUnit().name();
            this.category = productGroup.getProduct().getCategory().getCategory();
        }
}
