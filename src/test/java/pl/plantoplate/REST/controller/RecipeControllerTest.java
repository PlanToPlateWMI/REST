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
import pl.plantoplate.REST.controller.dto.response.RecipeOverviewResponse;
import pl.plantoplate.REST.controller.dto.model.RecipeProductQty;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.service.RecipeCategoryService;
import pl.plantoplate.REST.service.RecipeService;
import pl.plantoplate.REST.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
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
    private UserService userService;

    @MockBean
    private RecipeService recipeService;

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
        int numberOfRecipes = 10;
        List<Recipe> allRecipes = returnSpecificNumberOfRecipes(numberOfRecipes);
        when(recipeService.getAllRecipes(null, null)).thenReturn(allRecipes);

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<RecipeOverviewResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeOverviewResponse>>() {
        });
        assertEquals(recipes.size(), numberOfRecipes);
    }

    @Test
    void shouldReturnAllRecipesByCategoryName() throws Exception {

        //given
        long categoryId = 1L;
        String categoryName = "Napoje";
        int numberOfRecipes = 10;

        when(recipeService.getAllRecipes(categoryName, null)).thenReturn(returnSpecificNumberOfRecipes(numberOfRecipes));
        when(recipeCategoryService.findRecipeCategoryByName(categoryName)).thenReturn(new RecipeCategory(categoryId, categoryName));
        when(recipeCategoryService.findAll()).thenReturn(List.of(new RecipeCategory(categoryId, categoryName)));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes?category=" + categoryName))
                .andExpect(status().isOk())
                .andReturn();
        //then
        List<RecipeOverviewResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeOverviewResponse>>() {
        });
        assertEquals(recipes.size(), numberOfRecipes);
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnAllRecipesLikedByGroup() throws Exception {

        //given
        int numberOfRecipes = 10;
        long groupId = 1L;
        Group groupOfUser = new Group();
        groupOfUser.setId(groupId);
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(groupOfUser);
        when(recipeService.getSelectedByGroupRecipes(null,groupOfUser)).thenReturn(returnSpecificNumberOfRecipes(numberOfRecipes));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/selected"))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<RecipeOverviewResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeOverviewResponse>>() {
        });
        assertEquals(recipes.size(), numberOfRecipes);
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnAllRecipesLikedByGroupAndSortedByGroup() throws Exception {

        //given
        long categoryId = 1L;
        String categoryName = "category";
        int numberOfRecipes = 10;
        long groupId = 1L;
        Group groupOfUser = new Group(groupId, "test", null, null, null, null);
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(groupOfUser);
        when(recipeService.getSelectedByGroupRecipes(categoryName,groupOfUser)).thenReturn(returnSpecificNumberOfRecipes(numberOfRecipes));
        when(recipeCategoryService.findRecipeCategoryByName(categoryName)).thenReturn(new RecipeCategory(categoryId, categoryName));

        //when
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/selected?category="+categoryName))
                .andExpect(status().isOk())
                .andReturn();

        //then
        List<RecipeOverviewResponse> recipes = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<RecipeOverviewResponse>>() {
        });
        assertEquals(recipes.size(), numberOfRecipes);
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldAddRecipeToSelected() throws Exception{

        //given
        long recipeId = 1L;
        long groupId = 1L;
        Group groupOfUser = new Group(groupId, "test", null, null, List.of(), null);
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(groupOfUser);

        //when
        mockMvc.perform(MockMvcRequestBuilders.put("/api/recipes/selected/" + recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(recipeService).addRecipeToSelectedByGroup(recipeId, groupOfUser);
    }

    private List<Recipe> returnSpecificNumberOfRecipes(int number) {

        return Collections.nCopies(number, new Recipe().builder().title("test").level(Level.EASY).image_source("test").id(1).category(new RecipeCategory(1L, "category_name")).build());
    }

    @Test
    void shouldReturnRecipeOverview() throws Exception {

        //given
        long recipeId = 1L;
        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).build();
        when(recipeService.findRecipeDetailById(recipeId)).thenReturn(new RecipeProductQty(recipe, new HashMap<>()));

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/recipes/" + recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(recipeService).findRecipeDetailById(recipeId);

    }


    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldDeleteRecipeFromSelected() throws Exception {

        //given
        long recipeId = 1L;
        long groupId = 1L;
        Group groupOfUser = new Group(groupId, "test", null, null, List.of(), null);
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(groupOfUser);

        //when
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/recipes/selected/" + recipeId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(recipeService).deleteRecipeFromSelectedByGroup(recipeId, groupOfUser);

    }
}
