package com.jihyun.mobilesoftwareproject;

public class MealCostData {
    private String type;
    private int totalCost;

    public MealCostData(String type, int totalCost) {
        this.type = type;
        this.totalCost = totalCost;
    }

    public String getType() {
        return type;
    }

    public int getTotalCost() {
        return totalCost;
    }
}
