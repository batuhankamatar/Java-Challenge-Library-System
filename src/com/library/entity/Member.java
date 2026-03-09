package com.library.entity;

import com.library.enums.MemberType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Member extends Person {
    private MemberType type;
    private double totalDebt;
    private int bookLimit;
    private double totalDonation;
    private List<BaseBook> borrowedBooks;

    public Member(long id, String name, String surname, MemberType type) {
        super(id, name, surname);
        this.type = type;
        this.totalDebt = 0.0;
        this.totalDonation = type.getPrice();
        this.bookLimit = type.getLimit();
        this.borrowedBooks = new ArrayList<>();
    }

    public void donate(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Donation must be positive!");
        }

        this.totalDonation += amount;
        checkStatusUpgrade();
    }

    private void checkStatusUpgrade() {
        if (this.totalDonation >= 2000.0 && this.type != MemberType.PREMIUM) {
            updateMemberType(MemberType.PREMIUM);
        }

        else if (this.totalDonation >= 500.0 &&
                (this.type == MemberType.GUEST || this.type == MemberType.STUDENT)) {
            updateMemberType(MemberType.STANDARD);
        }
    }

    private void updateMemberType(MemberType newType) {
        this.type = newType;
        this.bookLimit = newType.getLimit();
        System.out.println(getName() + " has been upgraded to " + newType + " status!");
    }

    public void addDebt(double amount) {
        if (amount < 0)
            throw new IllegalArgumentException("Debt amount cannot be negative!");

        this.totalDebt += amount;
    }

    public void payDebt(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Payment must be positive!");

        this.totalDebt -= amount;
    }

    public void addBorrowedBook(BaseBook book) {
        if (this.borrowedBooks.size() >= this.bookLimit) {
            throw new IllegalStateException(getName() + " has reached the book limit (" + bookLimit + ")!");
        }

        this.borrowedBooks.add(book);
    }

    public void removeBorrowedBook(BaseBook book) {
        this.borrowedBooks.remove(book);
    }

    public List<BaseBook> getBorrowedBooks() {
        return Collections.unmodifiableList(borrowedBooks);
    }

    public MemberType getType() {
        return type;
    }

    public double getTotalDebt() {
        return totalDebt;
    }

    public int getBookLimit() {
        return bookLimit;
    }

    public double getTotalDonation() {
        return totalDonation;
    }

    @Override
    public String toString() {
        return "Member [ID: " + getId() +
                ", Name: " + getName() + " " + getSurname() +
                ", Type: " + type +
                ", Debt: " + totalDebt +
                ", Limit: " + bookLimit + "]";
    }
}
