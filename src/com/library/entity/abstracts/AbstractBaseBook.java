package com.library.entity.abstracts;

import com.library.entity.interfaces.Categorizable;
import com.library.entity.interfaces.Identifiable;
import com.library.entity.interfaces.Loanable;
import com.library.entity.interfaces.Searchable;
import com.library.enums.BookGenre;
import com.library.enums.BookStatus;
import java.util.UUID;
import java.util.Objects;

public abstract class AbstractBaseBook implements Identifiable, Searchable, Loanable, Categorizable {
    private final String id; // long -> String
    private String title;
    private String author;
    private double price; //Hasar durumunda üyenin totalDebt'ine eklenecek fiyat.
    private BookStatus status;
    private BookGenre genre;

    public AbstractBaseBook(String title, String author, double price, BookStatus status, BookGenre genre) {
        this.id = UUID.randomUUID().toString(); // ID otomatik üretiliyor
        this.title = title;
        this.author = author;
        this.price = price;
        this.status = status;
        this.genre = genre;
    }

    public String getId() { // return tipi String oldu
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public BookStatus getStatus() {
        return status;
    }

    public BookGenre getGenre() {
        return genre;
    }

    public void setTitle(String title) {
        if(title == null || title.isBlank())
            throw new IllegalArgumentException("'title' cannot be empty!");

        this.title = title;
    }

    public void setAuthor(String author) {
        if(author == null || author.isBlank())
            throw new IllegalArgumentException("'author' cannot be empty!");

        this.author = author;
    }

    public void setPrice(double price) {
        if(price <= 0)
            throw new IllegalArgumentException("'price' must be positive!");

        this.price = price;
    }

    public void setStatus(BookStatus status) {
        if(status == null)
            throw new IllegalArgumentException("'status' cannot be empty!");
        this.status = status;
    }

    @Override
    public String toString() {
        return "Book [ID: " + id +
                ", Title: " + title +
                ", Author: " + author +
                ", Price: " + price +
                ", Status: " + status + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractBaseBook baseBook = (AbstractBaseBook) o;
        return Objects.equals(id, baseBook.id); // String karşılaştırması için güncellendi
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id); // String hash kodu için güncellendi
    }

    @Override
    public String getSearchKey() {
        return title;
    }
}