package pl.plantoplate.REST.entity;

import lombok.Data;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;

import javax.persistence.*;

@Entity
@Table(name = "synchronization_product")
@Data
public class Synchronization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Product product;

    @ManyToOne
    private Group group;

    private float qty;
}
