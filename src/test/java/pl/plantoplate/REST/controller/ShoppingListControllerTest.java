package pl.plantoplate.REST.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.dto.Request.AddShopProductRequest;
import pl.plantoplate.REST.dto.Request.AmountRequest;
import pl.plantoplate.REST.dto.Response.ShoppingProductsResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.security.JwtUtils;
import pl.plantoplate.REST.service.ShopProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("BaseProductController test")
@Sql({"/schema-test.sql", "/data-test.sql"})
public class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JwtUtils utils;


    @MockBean
    private UserService userService;
    @MockBean
    private ShopProductService productService;

    private static ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldReturnShoppingProductList() throws Exception {

        Category category = new Category();
        category.setCategory("name");

        Product product = new Product();
        product.setCategory(category);
        product.setUnit(Unit.L);
        product.setName("Name");
        //given
        String email = "email@gmail.com";
        ShopProduct toBuy = new ShopProduct();
        toBuy.setBought(false);
        toBuy.setProduct(product);
        toBuy.setId(1L);
        toBuy.setAmount(10);

        ShopProduct bought = new ShopProduct();
        bought.setBought(true);
        bought.setProduct(product);
        bought.setId(1L);
        bought.setAmount(10);

        List<ShopProduct> toBuyProducts = List.of(toBuy, toBuy);
        List<ShopProduct> boughtProducts = List.of(bought);

        Group group = new Group();
        group.setShopProductList(Stream.concat(toBuyProducts.stream(), boughtProducts.stream()).collect(Collectors.toList()));

        when(userService.findGroupOfUser(email)).thenReturn(group);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/shopping"))
                .andExpect(status().isOk())
                .andReturn();

        ShoppingProductsResponse response = mapper.readValue(mvcResult.getResponse().getContentAsString(), ShoppingProductsResponse.class);
        assertEquals(response.getBought().size(), boughtProducts.size());
        assertEquals(response.getToBuy().size(), toBuyProducts.size());
    }

    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldAddProductToShoppingList() throws Exception {
        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;
        int amount = 10;
        AddShopProductRequest request = new AddShopProductRequest(productId, amount);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/shopping/")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(productService).addProductToList(productId, amount, group);
    }


    @Test
    @WithMockUser(value = "email@gmail.com", roles = {"ADMIN"})
    void shouldDeleteProductFromShoppingList() throws Exception {
        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/shopping/" + productId))
                .andExpect(status().isOk());

        //then
        verify(productService).deleteProduct(productId, group);
    }


    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldModifyProductOnShoppingList() throws Exception {
        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;

        int amount = 10;
        AmountRequest request = new AmountRequest(amount);

        //when
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/shopping/" + productId)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(productService).modifyAmount(productId, group, amount);
    }



    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldModifyIsBoughtProductOnShoppingList() throws Exception {

        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/shopping/" + productId))
                .andExpect(status().isOk());

        //then
        verify(productService).changeIsBought(productId, group);
    }




}
