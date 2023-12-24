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
import pl.plantoplate.REST.controller.dto.request.BaseProductRequest;
import pl.plantoplate.REST.controller.dto.response.ProductResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.entity.product.Product;
import pl.plantoplate.REST.entity.shoppinglist.Unit;
import pl.plantoplate.REST.service.ProductService;
import pl.plantoplate.REST.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("BaseProductController test")
public class BaseProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;
    @MockBean
    private ProductService productService;

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
    void shouldReturnAllProductsFromBase() throws Exception {

        //given
        String email = "email@gmail.com";
        long groupId = 2L;
        long generalGroupId = 1L;
        Group group = new Group();
        group.setId(groupId);

        Group generalGroup = new Group();
        generalGroup.setId(generalGroupId);

        Product p = new Product();
        p.setUnit(Unit.L);
        p.setName("test");
        Category category = new Category();
        category.setCategory("Inne");
        p.setCategory(category);

        List<Product> generalProducts = List.of(p, p);
        List<Product> groupProducts = List.of(p);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.getProductsOfGroup(generalGroup)).thenReturn(generalProducts);
        when(productService.getProductsOfGroup(group)).thenReturn(groupProducts);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products?type=all"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<ProductResponse> base = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<ProductResponse>>(){});
        assertEquals(generalProducts.size() + groupProducts.size(), base.size());
    }


    @Test
    @WithMockUser(value = "email@gmail.com")
    void shouldReturnGroupProducts() throws Exception {

        //given
        String email = "email@gmail.com";
        long groupId = 2L;
        long generalGroupId = 1L;
        Group group = new Group();
        group.setId(groupId);


        Group generalGroup = new Group();
        generalGroup.setId(generalGroupId);

        Product p = new Product();
        p.setUnit(Unit.L);
        p.setName("test");
        Category category = new Category();
        category.setCategory("Inne");
        p.setCategory(category);

        List<Product> generalProducts = List.of(p, p);
        List<Product> groupProducts = List.of(p);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        when(productService.getProductsOfGroup(generalGroup)).thenReturn(generalProducts);
        when(productService.getProductsOfGroup(group)).thenReturn(groupProducts);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/products?type=group"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<ProductResponse> base = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<ProductResponse>>(){});
        assertEquals(groupProducts.size(), base.size());
    }


    @Test
    @WithMockUser(value = "email@gmail.com", roles = {"USER"})
    void shouldReturnForbiddenThenUserWithoutRoleAdminTryToDeleteProduct() throws Exception {

        long productId = 1L;
        String email = "email@gmail.com";

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + productId))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithMockUser(value = "email@gmail.com", roles = {"ADMIN"})
    void shouldDeleteProductById() throws Exception {

        long productId = 1L;
        String email = "email@gmail.com";

        long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);
        when(userService.findGroupOfUser(email)).thenReturn(group);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/products/" + productId))
                .andExpect(status().isOk());

        verify(productService).deleteById(productId, groupId);
    }

    @Test
    @WithMockUser(value = "email@gmail.com", roles = {"USER"})
    void shouldReturnForbiddenThenUserWithoutRoleAdminTryTUpdateProduct() throws Exception {

        long productId = 1L;
        String email = "email@gmail.com";

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/products/" + productId)
                .content(mapper.writeValueAsString(new BaseProductRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(value = "email@gmail.com", roles = {"ADMIN"})
    void shouldUpdateProduct() throws Exception {

        //given
        long productId = 1L;
        String email = "email@gmail.com";
        String name = "name";
        String category = "category";
        String unit = "unit";
        long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        BaseProductRequest request = new BaseProductRequest(name, unit, category);


        //when
         mockMvc.perform(MockMvcRequestBuilders.patch("/api/products/" + productId)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

         //then
        verify(productService).updateProduct(name, unit, category, group, productId);

    }


    @Test
    @WithMockUser(value = "email@gmail.com", roles = {"USER"})
    void shouldReturnForbiddenThenUserWithoutRoleAdminTryToAddProduct() throws Exception {

        long productId = 1L;
        String email = "email@gmail.com";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/")
                .content(mapper.writeValueAsString(new BaseProductRequest()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithMockUser(value = "email@gmail.com", roles = {"ADMIN"})
    void shouldAddProduct() throws Exception {

        //given
        String email = "email@gmail.com";
        String name = "name";
        String category = "category";
        String unit = "unit";
        long groupId = 1L;
        Group group = new Group();
        group.setId(groupId);

        when(userService.findGroupOfUser(email)).thenReturn(group);
        BaseProductRequest request = new BaseProductRequest(name, unit, category);


        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/products/")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(productService).save(name, category,unit,  group);

    }
}
