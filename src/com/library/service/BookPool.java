package com.library.service;

import com.library.entity.abstracts.AbstractBaseBook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BookPool {
    private List<AbstractBaseBook> newArrivals;
    private List<AbstractBaseBook> returnedBooks;

    public BookPool() {
        this.newArrivals = new ArrayList<>();
        this.returnedBooks = new ArrayList<>();
    }

    public void addNewArrival(AbstractBaseBook book) {
        if (book == null) {
            throw new IllegalArgumentException("Cannot add a null book to arrivals!");
        }
        newArrivals.add(book);
    }

    public void addReturnedBook(AbstractBaseBook book) {
        if (book == null) {
            throw new IllegalArgumentException("Cannot add a null book to returns!");
        }
        returnedBooks.add(book);
    }

    public List<AbstractBaseBook> getNewArrivals() {
        return Collections.unmodifiableList(newArrivals);
    }

    public List<AbstractBaseBook> getReturnedBooks() {
        return Collections.unmodifiableList(returnedBooks);
    }

    public void clearArrivals() {
        newArrivals.clear();
    }

    public void clearReturns() {
        returnedBooks.clear();
    }

    @Override
    public String toString() {
        return "Book Pool Status [New Arrivals: " + newArrivals.size() +
                ", Returns Waiting: " + returnedBooks.size() + "]";
    }

    //HashCode ve equals ezilmeyecek çünkü bu bir veri nesnesi değil, servis yönetim nesnesidir. Tek bir BookPool nesnesi var.
}

