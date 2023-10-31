package pl.plantoplate.REST.entity.recipe;

import lombok.*;
import pl.plantoplate.REST.entity.auth.Group;

import javax.persistence.*;
import java.util.List;

/**
 * Entity that represents recipe
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

    @ManyToOne
    private Group group;

    @ManyToOne
    private RecipeCategory category;

}
