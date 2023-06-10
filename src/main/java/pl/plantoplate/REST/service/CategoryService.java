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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.plantoplate.REST.entity.product.Category;
import pl.plantoplate.REST.exception.EntityNotFound;
import pl.plantoplate.REST.repository.CategoryRepository;

import java.util.List;

/**
 * Service Layer of Category JPA Repository
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;


    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Returns Category object if it exists otherwise throws RT Exception {@link pl.plantoplate.REST.exception.EntityNotFound}
     * @param name - category name to find
     * @return Category object if category with name parametr exists
     */
    @Transactional(readOnly = true)
    public Category findByName(String name) {
        return categoryRepository.findByCategory(name).orElseThrow(() -> new EntityNotFound("Category [ " + name + "] not found"));
    }

    /**
     * Returns list of all categories
     * @return list of all categories
     */
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}
