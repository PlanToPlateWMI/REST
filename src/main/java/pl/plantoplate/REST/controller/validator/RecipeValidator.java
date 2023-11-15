package pl.plantoplate.REST.controller.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import pl.plantoplate.REST.entity.recipe.Level;
import pl.plantoplate.REST.exception.WrongRequestData;
import pl.plantoplate.REST.service.RecipeCategoryService;

@Component
@RequiredArgsConstructor
public class RecipeValidator {

    private final RecipeCategoryService recipeCategoryService;

    public void validateRecipeSortValues(String categoryName, String level){

        if(StringUtils.hasLength(categoryName))
            recipeCategoryService.findAll().stream().
                    filter(rc -> rc.getTitle().equals(categoryName)).findFirst().orElseThrow( () -> new WrongRequestData("Wrong category name"));

        if(StringUtils.hasLength(level)){
            try{
                Level.valueOf(level);
            }catch (IllegalArgumentException e){
                throw new WrongRequestData("Wrong level");
            }
        }

    }
}
