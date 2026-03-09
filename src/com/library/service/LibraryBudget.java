package com.library.service;

public class LibraryBudget {
    private final double yearlyGovernmentGrant;
    private final double monthlyFixedExpenses;
    private double accumulatedBalance;
    private double currentMonthPool;

    public LibraryBudget(double yearlyGovernmentGrant, double monthlyFixedExpenses) {
        this.yearlyGovernmentGrant = yearlyGovernmentGrant;
        this.monthlyFixedExpenses = monthlyFixedExpenses;
        this.accumulatedBalance = 0.0;
        this.currentMonthPool = 0.0;
    }

    public void addToCurrentMonthPool(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be positive!");

        this.currentMonthPool += amount;
    }

    public void executeMonthEndCycle() {
        double monthlyGrant = yearlyGovernmentGrant / 12;

        this.accumulatedBalance += (monthlyGrant + currentMonthPool - monthlyFixedExpenses);

        this.currentMonthPool = 0.0;
    }

    public double getAvailableBalance() {
        return accumulatedBalance;
    }

    public void deductPurchaseAmount(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Expense must be positive!");

        if (amount > accumulatedBalance)
            throw new IllegalStateException("Insufficient funds! Available: " + accumulatedBalance);

        this.accumulatedBalance -= amount;
    }

    @Override
    public String toString() {
        return "Library Budget Status [Total Balance: " + accumulatedBalance +
                ", Current Month Income: " + currentMonthPool +
                ", Monthly Grant: " + (yearlyGovernmentGrant / 12) +
                ", Monthly Expenses: " + monthlyFixedExpenses + "]";
    }
}