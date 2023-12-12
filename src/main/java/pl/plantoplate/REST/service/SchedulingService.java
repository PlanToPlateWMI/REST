package pl.plantoplate.REST.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.auth.Group;
import pl.plantoplate.REST.entity.meal.Meal;
import pl.plantoplate.REST.entity.meal.MealIngredient;
import pl.plantoplate.REST.repository.MealIngredientRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SchedulingService {

    private final SynchronizationService synchronizationService;
    private final MealService mealService;
    private final MealIngredientRepository mealIngredientRepository;

    // https://stackoverflow.com/questions/45124756/spring-scheduling-cron-expression-for-everyday-at-midnight-not-working
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDeletingSynchronizationBase(){

        LocalDate dayThreeDaysAgo = LocalDate.now().minusDays(3);
        List<Meal> mealsPlannedFourDaysAgo = mealService.getMealsByBeforePlannedDate(dayThreeDaysAgo);
        log.info("Meals was deleted : " + mealsPlannedFourDaysAgo.size());
        for(Meal meal:mealsPlannedFourDaysAgo){

            Group group = meal.getGroup();
            for (MealIngredient mealIngredient : mealIngredientRepository.findAllByMeal(meal))
                this.synchronizationService.deleteSynchronizationIngredient(group, mealIngredient);
        }

        mealService.deleteAll(mealsPlannedFourDaysAgo);
    }
}
