package net.nighthawkempires.economy.data;

import net.nighthawkempires.economy.category.CategoryModel;

public class CategoryData {

    private CategoryModel categoryModel;
    private int currentPage;

    public CategoryData(CategoryModel model, int currentPage) {
        this.categoryModel = model;
        this.currentPage = currentPage;
    }

    public CategoryModel getCategoryModel() {
        return categoryModel;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
