package com.chaitupenjudcoder.datapojos;

public class IncomeExpense {
    private String title;
    private String amount;
    private String date;
    private String note;
    private String category;
    private String bucksString;

    public IncomeExpense() {
    }

    public IncomeExpense(String title, String amount, String date, String note, String category, String bucksString) {
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.note = note;
        this.category = category;
        this.bucksString = bucksString;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public String getCategory() {
        return category;
    }

    public String getBucksString() {
        return bucksString;
    }
}
