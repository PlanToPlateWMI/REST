package pl.plantoplate.REST.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseOfProductsDto {

    private List<ProductDto> general;
    private List<ProductDto> group;
}
