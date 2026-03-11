package com.library.service;

import com.library.entity.abstracts.AbstractBaseBook;
import com.library.entity.abstracts.AbstractMember;
import com.library.enums.BookGenre;
import com.library.enums.BookStatus;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LibraryManager {
    private final LibraryInventory inventory;
    private final LoanService loanService;
    private final LibraryBudget budget;
    private final WishList wishList;
    private final BookPool bookPool;

    public LibraryManager(LibraryInventory inv, LoanService loan, LibraryBudget bud, WishList wish, BookPool pool) {
        this.inventory = inv;
        this.loanService = loan;
        this.budget = bud;
        this.wishList = wish;
        this.bookPool = pool;
    }

    /**
     * Ödünç verme işlemi sanal tarihi (systemDate) parametre olarak alır.
     */
    public void loanBookToMember(AbstractMember member, AbstractBaseBook book, LocalDate systemDate) {
        loanService.loanBook(member, book, systemDate);
    }

    /**
     * İade işlemi sanal tarihi (systemDate) parametre olarak alır.
     */
    public void returnBookFromMember(AbstractMember member, AbstractBaseBook book, LocalDate systemDate) {
        // 1. Finansal işlemleri, gecikme cezasını ve üye kayıtlarını hallet
        loanService.returnBook(member, book, systemDate);

        // 2. KİTAP HAVUZUNA EKLE
        // Kitap rafa çıkmadan önce "iade havuzuna" girmeli.
        bookPool.addReturnedBook(book);

        System.out.println(book.getTitle() + " iade havuzuna alındı.");
    }

    public List<AbstractBaseBook> searchBookByTitle(String title) {
        return inventory.searchByTitle(title);
    }

    public List<AbstractBaseBook> getBooksInGenre(BookGenre genre) {
        return inventory.getBooksByGenre(genre);
    }

    public void removeBrokenBook(String id) {
        inventory.removeBook(id);
        System.out.println("Kitap envanterden düşüldü. ID: " + id);
    }

    /**
     * İade havuzunu ve yeni gelenleri işleme.
     * Düzenleme: Hasarlı veya Kayıp olan kitapları AVAILABLE yapmaz, statülerini korur.
     */
    public void processBookPool() {
        // Yeni gelenler (Satın alma/Bağış) ilk kez eklenir
        for (AbstractBaseBook book : bookPool.getNewArrivals()) {
            inventory.addBook(book);
        }

        // İade edilenler işlenir
        for (AbstractBaseBook book : bookPool.getReturnedBooks()) {
            // SADECE statüsü uygun olan kitaplar AVAILABLE (Müsait) yapılır.
            // Main'de DAMAGED veya LOST olarak işaretlenen kitaplar bu statülerini korur.
            if (book.getStatus() != BookStatus.DAMAGED && book.getStatus() != BookStatus.LOST) {
                book.setStatus(BookStatus.AVAILABLE);
            }
        }

        bookPool.clearArrivals();
        bookPool.clearReturns();
        System.out.println("Kitap havuzu işlendi: Yeni gelenler rafa dizildi, sağlam iadeler kullanıma açıldı.");
    }

    /**
     * WISHLIST KONTROLÜ: Kitap eklemeden önce envanter kontrolü yapar
     */
    public void addNewRequest(String title) {
        wishList.addRequest(title, this.inventory);
    }

    /**
     * Kütüphanecinin kararlarına göre satın alma işlemi
     */
    public void processPurchases(Map<String, AbstractBaseBook> librarianDecision) {
        System.out.println("--- Satın Alma İşlemi Başladı ---");

        Map<String, Integer> requests = wishList.getRequestedTitles();
        Iterator<Map.Entry<String, Integer>> iterator = requests.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            String requestedTitle = entry.getKey();

            AbstractBaseBook bookToBuy = librarianDecision.get(requestedTitle.toLowerCase());

            if (bookToBuy != null) {
                if (budget.getAvailableBalance() >= bookToBuy.getPrice()) {
                    budget.deductPurchaseAmount(bookToBuy.getPrice());
                    inventory.addBook(bookToBuy);

                    System.out.println("SATIN ALINDI: " + bookToBuy.getTitle() + " [Tür: " + bookToBuy.getGenre() + "]");
                    iterator.remove();
                } else {
                    System.out.println("BÜTÇE YETERSİZ: " + requestedTitle);
                }
            } else {
                System.out.println("BİLGİ: " + requestedTitle + " için henüz seçim yapılmadı.");
            }
        }
    }

    /**
     * Üyenin borç ödemesi ve bütçeye gelir kaydedilmesi
     */
    public void memberPaysDebt(AbstractMember member, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Ödenen miktar pozitif olmalıdır!");
        }

        // 1. Üyenin borç bakiyesini güncelle
        member.payDebt(amount);

        // 2. Ödenen parayı kütüphane bütçesine (aylık havuza) ekle
        budget.addToCurrentMonthPool(amount);

        System.out.println("İŞLEM BAŞARILI: " + member.getName() + " " + member.getSurname() +
                " tarafından " + amount + " TL borç ödemesi yapıldı. Bütçeye aktarıldı.");
    }

    public void finalizeMonthEnd(Map<String, AbstractBaseBook> librarianDecision) {
        budget.executeMonthEndCycle();
        processPurchases(librarianDecision);
        System.out.println("Ay sonu işlemleri tamamlandı.");
    }

    public void showLibraryStatus() {
        System.out.println("=== KÜTÜPHANE GENEL DURUMU ===");
        System.out.println(inventory.toString());
        System.out.println(budget.toString());
        System.out.println("Bekleyen Emanet Toplamı: " + loanService.getTotalValueInDepositWait());
    }
}