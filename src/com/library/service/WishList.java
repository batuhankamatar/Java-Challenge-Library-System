package com.library.service;

import com.library.entity.abstracts.AbstractBaseBook;
import java.util.*;

public class WishList {
    private final Map<String, Integer> requestedTitles = new HashMap<>();

    public void addRequest(String bookTitle, LibraryInventory inventory) {
        // 1. Önce envanterde bu isimde bir kitap var mı diye bakıyoruz
        List<AbstractBaseBook> existingBooks = inventory.searchByTitle(bookTitle);

        if (!existingBooks.isEmpty()) {
            System.out.println("UYARI: '" + bookTitle + "' zaten kütüphanemizde mevcut. Wishlist'e eklenemez.");
            return; // Kitap varsa ekleme yapmadan metottan çıkıyoruz
        }

        // 2. Kitap envanterde yoksa talebi kaydediyoruz
        String titleKey = bookTitle.toLowerCase();
        requestedTitles.put(titleKey, requestedTitles.getOrDefault(titleKey, 0) + 1);
        System.out.println("TALEP ONAYLANDI: '" + bookTitle + "' wishlist'e eklendi.");
    }

    public Map<String, Integer> getRequestedTitles() {
        return requestedTitles;
    }

    @Override
    public String toString() {
        return "WishList [Unique Requests: " + requestedTitles.size() + "]";
    }
}