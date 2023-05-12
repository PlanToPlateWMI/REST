package pl.plantoplate.REST.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.plantoplate.REST.dto.Response.ShoppingProductsResponse;
import pl.plantoplate.REST.dto.Response.SimpleResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.shoppinglist.ShopProductGroup;
import pl.plantoplate.REST.exception.UserNotFound;
import pl.plantoplate.REST.service.ShopProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/shopping")
public class ShoppingListProductsController {


    private final ShopProductService shopProductService;
    private final UserService userService;


    @Autowired
    public ShoppingListProductsController(ShopProductService shopProductService, UserService userService) {
        this.shopProductService = shopProductService;
        this.userService = userService;
    }


    @GetMapping("")
    @Operation(summary="Get shopping products - 2 lists - bought and toBuy",description = "User can get list of products he wants to buy and he bought ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products",  content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ShoppingProductsResponse.class)))),
            @ApiResponse(responseCode = "400", description = "Account with this email doesn't exist",  content = @Content(
                    schema = @Schema(implementation = SimpleResponse.class)))})
    public ResponseEntity shoppingListOfGroup(){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Group group = null;

        try{
            group = userService.findGroupOfUser(email);
        }catch (UserNotFound e){
            return new ResponseEntity(
                    new SimpleResponse(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

        List<ShopProductGroup> productShopList = group.getShopProductList();
        Map<Boolean, List<ShopProductGroup>> mapOfBoughtAndToBuyProducts = productShopList.stream().
                collect(Collectors.partitioningBy(x -> x.isBought()));

        ShoppingProductsResponse response = new ShoppingProductsResponse(mapOfBoughtAndToBuyProducts.get(true),
                mapOfBoughtAndToBuyProducts.get(false));

        return new ResponseEntity(response, HttpStatus.OK);

    }
}
