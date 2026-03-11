package com.library.service;

import com.library.entity.abstracts.AbstractBaseBook;
import com.library.enums.BookGenre;

import java.util.*;

public class LibraryInventory {
    // 1. Ana Depo: ID üzerinden hızlı erişim (Map kriteri)
    private final Map<String, AbstractBaseBook> allBooks = new HashMap<>();

    // 2. Kategori Bazlı Raf Düzeni (Set kriteri - Sıralama için TreeSet)
    private final Map<BookGenre, TreeSet<AbstractBaseBook>> shelves = new HashMap<>();

    // 3. Sıralama Kuralı: Önce Yazar, sonra Kitap İsmi, sonra ID (Kopyaları alt alta dizer)
    private final Comparator<AbstractBaseBook> bookComparator = Comparator
            .comparing(AbstractBaseBook::getAuthor, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(AbstractBaseBook::getTitle, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(AbstractBaseBook::getId);

    public LibraryInventory() {
        // Her kategori için boş bir TreeSet (sıralama kuralı tanımlanmış şekilde) oluşturuyoruz
        for (BookGenre genre : BookGenre.values()) {
            shelves.put(genre, new TreeSet<>(bookComparator));
        }
    }

    public void addBook(AbstractBaseBook book) {
        if (book == null)
            throw new IllegalArgumentException("Book cannot be null");

        if (allBooks.containsKey(book.getId())) {
            throw new IllegalStateException("Book with ID " + book.getId() + " already exists!");
        }

        // Hem ana haritaya hem de sıralı kategori setine ekliyoruz
        allBooks.put(book.getId(), book);
        shelves.get(book.getGenre()).add(book);
    }

    public void removeBook(String id) {
        AbstractBaseBook book = allBooks.remove(id);
        if (book != null) {
            shelves.get(book.getGenre()).remove(book);
        } else {
            throw new NoSuchElementException("Book with ID " + id + " not found!");
        }
    }

    public AbstractBaseBook findById(String id) {
        return allBooks.get(id);
    }

    // Belirli bir türdeki tüm kitapları sıralı (Yazar->İsim) getirir
    public List<AbstractBaseBook> getBooksByGenre(BookGenre genre) {
        // TreeSet zaten sıralı olduğu için doğrudan listeye çevirip dönebiliriz
        return new ArrayList<>(shelves.get(genre));
    }

    // Başlığa göre arama sonuçlarını da kullanıcıya sıralı sunmak en iyisidir
    public List<AbstractBaseBook> searchByTitle(String title) {
        List<AbstractBaseBook> results = new ArrayList<>();
        for (AbstractBaseBook b : allBooks.values()) {
            if(b.getTitle().toLowerCase().contains(title.toLowerCase())) {
                results.add(b);
            }
        }
        results.sort(bookComparator); // Görsel düzen için sıralıyoruz
        return results;
    }

    // Yazara göre arama
    public List<AbstractBaseBook> searchByAuthor(String author) {
        List<AbstractBaseBook> results = new ArrayList<>();
        for (AbstractBaseBook b : allBooks.values()) {
            if(b.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                results.add(b);
            }
        }
        results.sort(bookComparator); // Gülün Adı kopyaları burada da alt alta gelecek
        return results;
    }

    @Override
    public String toString() {
        return "Library Inventory [Total Books: " + allBooks.size() + "]";
    }
}