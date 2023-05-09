package pl.plantoplate.REST.dto.Response;

import lombok.*;
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShoppingListDto {

    private String product;
    private Long id;
    private int amount;
    private String unit;


    public ShoppingListDto(ShopProductGroup productGroup){
        this.product = productGroup.getProduct().getName();
        this.id = productGroup.getId();
        this.amount = productGroup.getAmount();
        this.unit = productGroup.getProduct().getUnit().name();
    }
}
