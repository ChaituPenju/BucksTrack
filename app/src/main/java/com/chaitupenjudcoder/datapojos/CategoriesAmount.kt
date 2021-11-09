package com.chaitupenjudcoder.datapojos;

public class CategoriesAmount {

    private String categoryName;
    private String totalAmount;
    private float percentage;

    public CategoriesAmount() {
    }

    public CategoriesAmount(String categoryName, String totalAmount, float percentage) {
        this.categoryName = categoryName;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public float getPercentage() {
        return percentage;
    }
}
