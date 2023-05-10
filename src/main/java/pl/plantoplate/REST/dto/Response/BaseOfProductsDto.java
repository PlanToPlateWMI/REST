package pl.plantoplate.REST.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.plantoplate.REST.entity.product.Product;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class BaseOfProductsDto {

    private List<ProductDto> general;
    private List<ProductDto> group;

    public BaseOfProductsDto(List<Product> general, List<Product> group){

        this.general = new ArrayList<>();
        this.group = new ArrayList<>();

        for(Product p:general)
            this.general.add(new ProductDto(p));

        for(Product p:group)
            this.group.add(new ProductDto(p));

    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class ProductDto {

        private String name;
        private String unit;

        public ProductDto (Product product){
            this.name = product.getName();
            this.unit = product.getUnit().name();
        }
    }
}
