package pl.plantoplate.REST.controller.recipe;

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
import pl.plantoplate.REST.dto.Response.RecipeResponse;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.repository.RecipeRepository;
import pl.plantoplate.REST.service.RecipeCategoryService;
import pl.plantoplate.REST.service.UserService;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("RecipeController test")
public class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private RecipeRepository recipeRepository;

    @MockBean
    private UserService userService;

    @MockBean
    private RecipeCategoryService recipeCategoryService;

    private static ObjectMapper mapper = new ObjectMapper();

    private static final String USER_EMAIL = "test@gmail.com";

    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldReturnAllRecipes() throws Exception {

        //given
        int numberOfElements = 10;
        when(recipeRepository.findAll()).thenReturn(returnSpecificNumberOfRecipes(numberOfElements));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<RecipeResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeResponse>>() {
        });
        assertEquals(recipes.size(), numberOfElements);
    }

    private List<Recipe> returnSpecificNumberOfRecipes(int number) {

        return Collections.nCopies(number, new Recipe().builder().title("test").level(Level.EASY).image_source("test").id(1).category(new RecipeCategory(1L, "category_name")).build());
    }

    @Test
    void shouldReturnAllRecipesByCategoryName() throws Exception {

        //given
        long categoryId = 1L;
        String categoryName = "category";
        int numberOfRecipes = 10;

        when(recipeRepository.findAllByCategoryTitle(categoryName)).thenReturn(returnSpecificNumberOfRecipes(numberOfRecipes));
        when(recipeCategoryService.findRecipeCategoryByName(categoryName)).thenReturn(new RecipeCategory(categoryId, categoryName));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes?category=" + categoryName))
                .andExpect(status().isOk())
                .andReturn();
        //then
        List<RecipeResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeResponse>>() {
        });
        assertEquals(recipes.size(), numberOfRecipes);
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnAllRecipesLikedByGroup() throws Exception {

        //given
        int numberOfElements = 10;
        long groupId = 1L;
        Group groupOfUser = new Group();
        groupOfUser.setId(groupId);
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(groupOfUser);
        when(recipeRepository.findAllByGroupId(groupId)).thenReturn(returnSpecificNumberOfRecipes(numberOfElements));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/selected"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<RecipeResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeResponse>>() {
        });
        assertEquals(recipes.size(), numberOfElements);
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnAllRecipesLikedByGroupAndSortedByGroup() throws Exception {

        //given
        long categoryId = 1L;
        String categoryName = "category";
        int numberOfElements = 10;
        long groupId = 1L;
        Group groupOfUser = new Group(groupId, "test", null, null);
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(groupOfUser);
        when(recipeRepository.findAllByGroupSelectedAndCategoryId(groupId, categoryId)).thenReturn(returnSpecificNumberOfRecipes(numberOfElements));
        when(recipeCategoryService.findRecipeCategoryByName(categoryName)).thenReturn(new RecipeCategory(categoryId, categoryName));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/selected?category="+categoryName))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<RecipeResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeResponse>>() {
        });
        assertEquals(recipes.size(), numberOfElements);
    }


}
