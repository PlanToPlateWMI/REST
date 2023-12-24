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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.service.CategoryService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("ProductCategoryController test")
class ProductCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private CategoryService categoryService;

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
    void shouldReturnAllProducts() throws Exception {

        //given
        String categoryName = "category";
        String categoryName2 = "category2";

        Category category = new Category();
        category.setCategory(categoryName);

        Category category2 = new Category();
        category2.setCategory(categoryName2);
        List<Category> categoryList = List.of(category, category2);
        when(categoryService.findAll()).thenReturn(categoryList);


        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/categories"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<String> resultOfController = mapper.readValue(mvcResult.getResponse().getContentAsString(),  new TypeReference<List<String>>(){});
        assertArrayEquals(resultOfController.toArray(), List.of(categoryName, categoryName2).toArray());


    }
}
