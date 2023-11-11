package pl.plantoplate.REST.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.shoppinglist.Unit;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IngredientQtUnit {

    private float qty;
    private Unit unit;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IngredientQtUnit that = (IngredientQtUnit) o;
        return qty == that.qty;
    }

    @Override
    public int hashCode() {
        return Objects.hash(qty, unit);
    }
}
