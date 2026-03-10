package com.library.service;

import com.library.entity.BaseBook;
import com.library.enums.BookGenre;

import java.util.*;

public class LibraryInventory {
    private final Map<Long, BaseBook> allBooks = new HashMap<>();
    private final Map<BookGenre, List<BaseBook>> shelves = new HashMap<>();

    public LibraryInventory() {
        for (BookGenre genre : BookGenre.values()) {
            shelves.put(genre, new ArrayList<>());
        }
    }

    public void addBook(BaseBook book) {
        if (book == null)
            throw new IllegalArgumentException("Book cannot be null");

        if (allBooks.containsKey(book.getId())) {
            throw new IllegalStateException("Book with ID " + book.getId() + " already exists!");
        }

        allBooks.put(book.getId(), book);
        shelves.get(book.getGenre()).add(book);
    }

    public void removeBook(long id) {
        BaseBook book = allBooks.remove(id);
        if (book != null) {
            shelves.get(book.getGenre()).remove(book);
        } else {
            throw new NoSuchElementException("Book with ID " + id + " not found!");
        }
    }

    public BaseBook findById(long id) {
        return allBooks.get(id);
    }

    public List<BaseBook> getBooksByGenre(BookGenre genre) {
        return Collections.unmodifiableList(shelves.get(genre));
    }

    public List<BaseBook> searchByTitle(String title) {
        List<BaseBook> results = new ArrayList<>();
        for (BaseBook b : allBooks.values()) {
            if(b.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    public List<BaseBook> searchByAuthor(String author) {
        List<BaseBook> results = new ArrayList<>();
        for (BaseBook b : allBooks.values()) {
            if(b.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                results.add(b);
            }
        }
        return results;
    }

    @Override
    public String toString() {
        return "Library Inventory [Total Books: " + allBooks.size() + "]";
    }
}
