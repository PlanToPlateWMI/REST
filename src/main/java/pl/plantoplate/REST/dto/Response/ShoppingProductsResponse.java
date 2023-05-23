package pl.plantoplate.REST.dto.Response;

import lombok.*;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ShoppingProductsResponse {

    private List<ShoppingProductResponse> bought;
    private List<ShoppingProductResponse> toBuy;

    public ShoppingProductsResponse(List<ShopProduct> bought, List<ShopProduct> toBuy){
        this.bought = new ArrayList<>();
        this.toBuy = new ArrayList<>();

        for(ShopProduct p: bought){
            this.bought.add(new ShoppingProductResponse(p));
        }

        for(ShopProduct p: toBuy){
            this.toBuy.add(new ShoppingProductResponse(p));
        }
    }

}
