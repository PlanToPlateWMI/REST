/*
Copyright 2023 the original author or authors

Licensed under the Apache License, Version 2.0 (the "License"); you
may not use this file except in compliance with the License. You
may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied. See the License for the specific language
governing permissions and limitations under the License.
 */


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

    /**
     * Every day at midnight delete Synchronization {@link pl.plantoplate.REST.entity.Synchronization}
     * of meal planned to day before 4 days and Meal {@link pl.plantoplate.REST.entity.meal.Meal}
     */
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
