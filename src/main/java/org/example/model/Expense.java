package org.example.model;

import java.time.LocalDate;

public class Expense {
    private int id;
    private String description;
    private String category;
    private double amount;
    private LocalDate date;

    public Expense(String description, String category, double amount, LocalDate date) {
        this.description = description;
        this.category = category;
        this.amount = amount;
        this.date = date;
    }


    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
