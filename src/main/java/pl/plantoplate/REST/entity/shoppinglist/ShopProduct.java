/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */
package pl.plantoplate.REST.entity.shoppinglist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shop_product_group")
public class ShopProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "group_owner_id")
    private Group group;

    @Column
    private int amount;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private ProductState productState;

    public ShopProduct(Product product, Group group, int amount, ProductState productState) {
        this.product = product;
        this.group = group;
        this.amount = amount;
        this.productState = productState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShopProduct that = (ShopProduct) o;
        return id == that.id && amount == that.amount && productState == that.productState;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, amount, productState);
    }
}
