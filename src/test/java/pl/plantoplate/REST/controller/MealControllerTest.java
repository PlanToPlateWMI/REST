package pl.plantoplate.REST.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.plantoplate.REST.controller.dto.model.MealProductQty;
import pl.plantoplate.REST.controller.dto.model.RecipeProductQty;
import pl.plantoplate.REST.controller.dto.request.PlanMealBasedOnRecipeRequest;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.entity.recipe.Recipe;
import pl.plantoplate.REST.service.MealService;
import pl.plantoplate.REST.service.UserService;

import java.time.LocalDate;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("MealController test")
public class MealControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private MealService mealService;

    @MockBean
    private UserService userService;

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
    @WithMockUser(value = USER_EMAIL)
    void shouldPlanRecipe() throws Exception {

        //given
        mapper.registerModule(new JavaTimeModule());
        PlanMealBasedOnRecipeRequest request = createPlanMeaRequest();
        Group group = new Group();
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(group);

        //when
         mockMvc.perform(MockMvcRequestBuilders.post("/api/meals")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        //then
        verify(mealService).planMeal(any(PlanMealBasedOnRecipeRequest.class), any(Group.class));
    }

    @ParameterizedTest
    @MethodSource("createPlanMealInvalidPortionsRequest")
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnBadRequest_NumberOfPortionsLessThanZero(PlanMealBasedOnRecipeRequest planMealBasedOnRecipeRequest) throws Exception {

        //given
        mapper.registerModule(new JavaTimeModule());
        Group group = new Group();
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(group);

        //when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/meals")
                .content(mapper.writeValueAsString(planMealBasedOnRecipeRequest))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnMealOverviewByDate() throws Exception {

        //given
        Group group = new Group();
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(group);
        String date = "2022-11-11";

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/meals?date=" + date)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(mealService).getMealOverviewByDate(LocalDate.of(2022, 11, 11), group);
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnBadRequest_GetMealOverviewByIncorrectDate() throws Exception {

        //given
        Group group = new Group();
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(group);
        String dateIncorrect = "11-11-2022";

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/meals?date=" + dateIncorrect)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(value = USER_EMAIL)
    void shouldReturnMealOverview() throws Exception {

        //given
        Group group = new Group();
        when(userService.findGroupOfUser(USER_EMAIL)).thenReturn(group);

        long mealId = 1L;
        long recipeId = 1L;
        Recipe recipe = Recipe.builder().id(recipeId).title("test").image_source("image").source("source")
                .time(2).level(Level.EASY).portions(2).steps("steps").isVege(true).build();
        Meal meal = new Meal();
        meal.setRecipe(recipe);
        when(mealService.findMealDetailById(mealId, group)).thenReturn(new MealProductQty(meal, new HashMap<>()));

        //when
        mockMvc.perform(MockMvcRequestBuilders.get("/api/meals/" + mealId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        //then
        verify(mealService).findMealDetailById(mealId, group);

    }


    private PlanMealBasedOnRecipeRequest createPlanMeaRequest(){
        return PlanMealBasedOnRecipeRequest.builder().mealType("LUNCH").recipeId(1L).date(LocalDate.now()).portions(2).build();
    }

    private static Object[] createPlanMealInvalidPortionsRequest(){
        return new Object[]{
                PlanMealBasedOnRecipeRequest.builder().mealType("LUNCH").recipeId(1L).date(LocalDate.now()).portions(0).build(),
                PlanMealBasedOnRecipeRequest.builder().mealType("LUNCH").recipeId(1L).date(LocalDate.now()).portions(-2).build()
        };
    }


}
