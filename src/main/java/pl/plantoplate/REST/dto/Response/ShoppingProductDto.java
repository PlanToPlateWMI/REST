package pl.plantoplate.REST.dto.Response;

import lombok.*;
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShoppingProductDto {

    private Long id;
    private String product;
    private int amount;
    private String unit;


    public ShoppingProductDto(ShopProductGroup productGroup){
        this.product = productGroup.getProduct().getName();
        this.id = productGroup.getId();
        this.amount = productGroup.getAmount();
        this.unit = productGroup.getProduct().getUnit().name();
    }

}
