package com.chaitupenjudcoder.datapojos;

public class CategoriesAmount {

    private String categoryName;
    private String totalAmount;
    private int percentage;

    public CategoriesAmount() {
    }

    public CategoriesAmount(String categoryName, String totalAmount, int percentage) {
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

    public int getPercentage() {
        return percentage;
    }
}
