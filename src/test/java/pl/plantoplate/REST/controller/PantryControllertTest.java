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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.dto.Request.AddShopProductRequest;
import pl.plantoplate.REST.dto.Request.AmountRequest;
import pl.plantoplate.REST.dto.Response.ShoppingProductResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.ShopProduct;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.service.PantryService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("PantryController test")
@Sql({"/schema-test.sql", "/data-test.sql"})
public class PantryControllertTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @MockBean
    private PantryService pantryService;

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
    void shouldGetAllProductsFromPantry() throws Exception {
        String email = "email@gmail.com";

        Category category = new Category();
        category.setCategory("name");

        Product product = new Product();
        product.setId(1L);
        product.setName("Name");
        product.setUnit(Unit.L);
        product.setCategory(category);

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setAmount(10);
        shopProduct.setProduct(product);

        List<ShopProduct> productList = List.of(shopProduct);
        when(pantryService.findProductsFromPantry(email)).thenReturn(productList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/pantry/"))
                .andExpect(status().isOk())
                .andReturn();

        verify(pantryService).findProductsFromPantry(email);

        List<ShoppingProductResponse> products = mapper.readValue(mvcResult.getResponse().getContentAsString(),  new TypeReference<List<ShoppingProductResponse>>(){});
        assertEquals(productList.size(), products.size());

    }


    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldTransferProductsToPantry() throws Exception {
        String email = "email@gmail.com";

        Category category = new Category();
        category.setCategory("name");

        Product product = new Product();
        product.setId(1L);
        product.setName("Name");
        product.setUnit(Unit.L);
        product.setCategory(category);

        ShopProduct shopProduct = new ShopProduct();
        shopProduct.setAmount(10);
        shopProduct.setProduct(product);

        long[] ids = new long[]{1L,2L};

        List<ShopProduct> productList = List.of(shopProduct);
        when(pantryService.transferProductToPantry(email, ids)).thenReturn(productList);

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/pantry/transfer")
                .content(mapper.writeValueAsString(ids))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        verify(pantryService).transferProductToPantry(email, ids);

        List<ShoppingProductResponse> products = mapper.readValue(mvcResult.getResponse().getContentAsString(),  new TypeReference<List<ShoppingProductResponse>>(){});
        assertEquals(productList.size(), products.size());
    }

    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldAddProductToPantry() throws Exception {
        //given
        String email = "email@gmail.com";
        Group group = new Group();
        when(userService.findGroupOfUser(email)).thenReturn(group);
        long productId = 1L;
        float amount = 10;
        AddShopProductRequest request = new AddShopProductRequest(productId, amount);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/pantry/")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(pantryService).addProductToPantry(productId, amount, email);

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
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/pantry/" + productId))
                .andExpect(status().isOk());

        //then
        verify(pantryService).deleteProduct(productId, email);
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
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/pantry/" + productId)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(pantryService).modifyAmount(productId, email, amount);
    }



}
