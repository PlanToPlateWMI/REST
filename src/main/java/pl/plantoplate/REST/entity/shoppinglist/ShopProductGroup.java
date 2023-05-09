package pl.plantoplate.REST.entity.shoppinglist;

import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;

public class ShopProductGroup {

    private long id;
    private Product product;
    private int amount;
    private Unit unit;
    private Group group;
}
