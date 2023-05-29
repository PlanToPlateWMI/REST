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

package pl.plantoplate.REST.entity.product;

import lombok.*;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.shoppinglist.Unit;

import javax.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "group_created_id")
    private Group createdBy;

    @Column
    @Enumerated(EnumType.STRING)
    private Unit unit;


    public Product(String name, Category category, Group createdBy, Unit unit){
        this.category = category;
        this.createdBy = createdBy;
        this.unit = unit;
        this.name = name;
    }

}
