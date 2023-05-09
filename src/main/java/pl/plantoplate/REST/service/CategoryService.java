package pl.plantoplate.REST.service;

import org.springframework.stereotype.Service;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.repository.CategoryRepository;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category findByName(String name){
        return categoryRepository.findByCategory(name).orElseThrow(() -> new RuntimeException());
    }
}
