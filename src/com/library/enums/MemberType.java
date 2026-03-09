package com.library.enums;

public enum MemberType {
    GUEST(1, 1.0, 0.0),
    STUDENT(3, 0.5, 200.0),
    STANDARD(5, 0.3, 500.0),
    PREMIUM(10, 0.0, 2000.0);

    private final int limit;
    private final double depositRate;
    private final double price;

    MemberType(int limit, double depositRate, double price) {
        this.limit = limit;
        this.depositRate = depositRate;
        this.price = price;
    }

    public int getLimit() {
        return limit;
    }

    public double getDepositRate() {
        return depositRate;
    }
    public double getPrice() {
        return price;
    }
}