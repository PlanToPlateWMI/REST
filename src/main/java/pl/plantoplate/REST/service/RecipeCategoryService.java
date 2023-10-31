package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.recipe.RecipeCategory;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.RecipeCategoryRepository;

import java.util.List;

@Service
public class RecipeCategoryService {

    private final RecipeCategoryRepository recipeCategoryRepository;

    public RecipeCategoryService(RecipeCategoryRepository recipeCategoryRepository) {
        this.recipeCategoryRepository = recipeCategoryRepository;
    }

    @Transactional(readOnly = true)
    public RecipeCategory findRecipeCategoryByName(String categoryName){
        return recipeCategoryRepository.findByTitle(categoryName).orElseThrow(() -> new EntityNotFound("Category [ " + categoryName
                + " ] not found."));
    }

    @Transactional(readOnly = true)
    public List<RecipeCategory> findAll(){
        return recipeCategoryRepository.findAll();
    }


}
