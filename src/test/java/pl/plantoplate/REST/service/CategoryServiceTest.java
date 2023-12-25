package pl.plantoplate.REST.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("Group Service Test")
class CategoryServiceTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp(){
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void shouldFindCategoryByName(){

        //given
        String categoryName = "test";
        Category category = new Category();
        when(categoryRepository.findByCategory(categoryName)).thenReturn(Optional.of(category));

        //then when
        Category actual = categoryService.findByName(categoryName);
        Assertions.assertEquals(category, actual);
    }

    @Test
    void shouldFindAllCategories(){

        //given
        Category category = new Category();
        when(categoryRepository.findAll()).thenReturn(List.of(category));

        //then when
        List<Category> actual = categoryService.findAll();
        Assertions.assertEquals(actual.size(), 1);
    }

}
