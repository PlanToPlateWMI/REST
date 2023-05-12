package pl.plantoplate.REST.dto.Response;

import lombok.*;
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ShoppingProductsResponse {

    private List<ShoppingProduct> bought;
    private List<ShoppingProduct> toBuy;

    public ShoppingProductsResponse(List<ShopProductGroup> bought, List<ShopProductGroup> toBuy){
        this.bought = new ArrayList<>();
        this.toBuy = new ArrayList<>();

        for(ShopProductGroup p: bought){
            this.bought.add(new ShoppingProduct(p));
        }

        for(ShopProductGroup p: toBuy){
            this.toBuy.add(new ShoppingProduct(p));
        }
    }




    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    static class ShoppingProduct {
        private Long id;
        private String name;
        private String category;
        private int amount;
        private String unit;


        public ShoppingProduct(ShopProductGroup productGroup){
            this.name = productGroup.getProduct().getName();
            this.id = productGroup.getId();
            this.amount = productGroup.getAmount();
            this.unit = productGroup.getProduct().getUnit().name();
            this.category = productGroup.getProduct().getCategory().getCategory();
        }
    }

}
