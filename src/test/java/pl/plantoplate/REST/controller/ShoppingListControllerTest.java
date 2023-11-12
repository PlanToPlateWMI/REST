package pl.plantoplate.REST.controller;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.controller.dto.request.AddShopProductRequest;
import pl.plantoplate.REST.controller.dto.request.AmountRequest;
import pl.plantoplate.REST.controller.dto.response.ShoppingProductResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ProductState;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.service.ShoppingListService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("ShoppingListController test")
public class ShoppingListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;
    @MockBean
    private ShoppingListService productService;

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
    void shouldReturnShoppingProductListToBuy() throws Exception {

        //given
        String email = "email@gmail.com";

        Category category = new Category();
        category.setCategory("name");

        Product product = new Product();
        product.setId(1L);
        product.setName("Name");
        product.setUnit(Unit.L);
        product.setCategory(category);

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setAmount(10.3f);
        shopProduct.setProduct(product);

        List<ShopProduct> products = List.of(shopProduct);
        when(productService.getProducts(email, ProductState.BUY)).thenReturn(products);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/shopping?bought=false"))
                .andExpect(status().isOk())
                .andReturn();

        List<ShoppingProductResponse> productResponses = mapper.readValue(mvcResult.getResponse().getContentAsString(),  new TypeReference<List<ShoppingProductResponse>>(){});
        assertEquals(products.size(), productResponses.size());
    }


    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldReturnShoppingProductListBought() throws Exception {

        String email = "email@gmail.com";

        Category category = new Category();
        category.setCategory("name");

        Product product = new Product();
        product.setId(1L);
        product.setName("Name");
        product.setUnit(Unit.L);
        product.setCategory(category);

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setAmount(10.4f);
        shopProduct.setProduct(product);

        List<ShopProduct> products = List.of(shopProduct);
        when(productService.getProducts(email, ProductState.BOUGHT)).thenReturn(products);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/shopping?bought=true"))
                .andExpect(status().isOk())
                .andReturn();

        List<ShoppingProductResponse> productResponses = mapper.readValue(mvcResult.getResponse().getContentAsString(),  new TypeReference<List<ShoppingProductResponse>>(){});
        assertEquals(products.size(), productResponses.size());
    }

    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldAddProductToShoppingList() throws Exception {
        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;
        float amount = 10;
        AddShopProductRequest request = new AddShopProductRequest(productId, amount);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/shopping/")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(productService).addProductToShoppingList(productId, amount, email);
    }

    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldAddProductsToShoppingList() throws Exception {

        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;
        float amount = 10;
        long productId2 = 2L;
        float amount2 = 20;
        AddShopProductRequest product1 = new AddShopProductRequest(productId, amount);
        AddShopProductRequest product2 = new AddShopProductRequest(productId2, amount2);
        AddShopProductRequest[] request = new AddShopProductRequest[]{product1, product2};

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/shopping/list")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(productService).addProductsToShoppingList(any(), anyString());
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
        verify(productService).deleteProduct(productId, email);
    }


    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldModifyProductOnShoppingList() throws Exception {
        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;

        float amount = 10;
        AmountRequest request = new AmountRequest(amount);

        //when
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/shopping/" + productId)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(productService).modifyAmount(productId, email, amount);
    }



    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldModifyStateOfProductOnShoppingList() throws Exception {

        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/shopping/" + productId))
                .andExpect(status().isOk());

        //then
        verify(productService).changeProductStateOnShoppingList(productId, email);
    }




}
