package pl.plantoplate.REST.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.dto.Response.BaseOfProductsDto;
import pl.plantoplate.REST.dto.Response.ProductDto;
import pl.plantoplate.REST.dto.Response.ShoppingProductDto;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.service.ProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private final ProductService productService;
    private final UserService userService;


    public ProductController(ProductService productService, UserService userService) {
        this.productService = productService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary="Get all products from base",description = "User can get list of all product in base - general and group custom products ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "2 lists of products - general and group products",  content = @Content(
                    schema = @Schema(implementation = BaseOfProductsDto.class))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity getAllProduct(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = null;

        try{
            group = userService.findGroupOfUser(email);
        }catch (UserNotFound e){
            return new ResponseEntity(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        long groupId = group.getId();

        List<Product> productsOfGroup = productService.getProductsOfGroup(groupId);
        List<Product> generalProducts = productService.getProductsOfGroup(1L);

        List<ProductDto> productsOfGroupDto = new ArrayList<>();
        List<ProductDto> generalProductsDto = new ArrayList<>();

        for(Product p:productsOfGroup){
            productsOfGroupDto.add(new ProductDto(p));
        }

        for(Product p:generalProducts){
            generalProductsDto.add(new ProductDto(p));
        }

        BaseOfProductsDto baseOfProductsDto = new BaseOfProductsDto(generalProductsDto, productsOfGroupDto);

        return new ResponseEntity(baseOfProductsDto, HttpStatus.OK);

    }
}
