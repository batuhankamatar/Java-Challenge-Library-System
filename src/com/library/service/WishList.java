package com.library.service;

import com.library.entity.BaseBook;
import java.util.*;

public class WishList {
    private final Map<String, Integer> requestedTitles = new HashMap<>();
    private final List<BaseBook> pendingPurchases = new ArrayList<>();

    public void addRequest(String bookTitle) {
        requestedTitles.put(bookTitle, requestedTitles.getOrDefault(bookTitle, 0) + 1);
        System.out.println("Talep kaydedildi: " + bookTitle);
    }

    public void processPurchases(LibraryBudget budget, LibraryInventory inventory) {
        List<Map.Entry<String, Integer>> sortedRequests = new ArrayList<>(requestedTitles.entrySet());
        sortedRequests.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("--- Satın Alma İşlemi Başladı ---");

        Iterator<Map.Entry<String, Integer>> iterator = sortedRequests.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String title = entry.getKey();

            double estimatedPrice = 150.0;

            if (budget.getAvailableBalance() >= estimatedPrice) {
                budget.deductPurchaseAmount(estimatedPrice);
                System.out.println("Bütçe onaylandı! " + title + " satın alındı ve rafa eklendi.");
                requestedTitles.remove(title);
            } else {
                System.out.println("Yetersiz bütçe: " + title + " için para kalmadı.");
                break;
            }
        }
    }

    @Override
    public String toString() {
        return "WishList [Unique Requests: " + requestedTitles.size() + "]";
    }
}