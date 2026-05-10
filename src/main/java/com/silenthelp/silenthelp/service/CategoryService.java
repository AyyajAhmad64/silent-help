package com.silenthelp.silenthelp.service;

import com.silenthelp.silenthelp.model.Category;
import com.silenthelp.silenthelp.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> activeCategories() {
        return categoryRepository.findByActiveTrueOrderByNameAsc();
    }

    @Transactional(readOnly = true)
    public List<Category> allCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Transactional
    public void toggle(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setActive(!category.isActive());
    }
}
