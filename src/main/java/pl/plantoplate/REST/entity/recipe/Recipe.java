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

package pl.plantoplate.REST.entity.recipe;

import lombok.*;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity that represents Recipe
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "recipe")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @Column
    private String description;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Column
    private int portions;

    @Column
    private int time;

    @Column
    private String source;

    @Column
    private String image_source;

    @Column
    private String steps;

    @Column(name = "is_vege")
    private boolean isVege;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "group_recipe",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    private List<Group> groupsSelectedRecipe =  new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group ownerGroup;

    @ManyToOne
    private RecipeCategory category;

    @OneToMany
    List<Product> ingredient = new ArrayList<>();

    public void addGroupSelected(Group group){
        groupsSelectedRecipe.add(group);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recipe recipe = (Recipe) o;
        return id == recipe.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
