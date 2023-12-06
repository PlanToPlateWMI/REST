package pl.plantoplate.REST.entity.meal;

import lombok.*;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.recipe.Recipe;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    private int portions;

    private String mealType;

    private LocalDate date;

    @Column(name = "is_prepared")
    private boolean isPrepared;

    @OneToMany
    List<Product> ingredient = new ArrayList<>();

}
