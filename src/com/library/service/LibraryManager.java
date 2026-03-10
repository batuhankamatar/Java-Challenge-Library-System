package com.library.service;

import com.library.entity.BaseBook;
import com.library.entity.Member;
import com.library.enums.BookGenre;
import java.util.List;

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

    public void loanBookToMember(Member member, BaseBook book) {
        loanService.loanBook(member, book);
    }

    public void returnBookFromMember(Member member, BaseBook book) {
        loanService.returnBook(member, book);
    }

    public List<BaseBook> searchBookByTitle(String title) {
        return inventory.searchByTitle(title);
    }

    public List<BaseBook> getBooksInGenre(BookGenre genre) {
        return inventory.getBooksByGenre(genre);
    }

    public void removeBrokenBook(long id) {
        inventory.removeBook(id);
        System.out.println("Hasarlı kitap envanterden düşüldü. ID: " + id);
    }

    public void processBookPool() {
        for (BaseBook book : bookPool.getNewArrivals()) {
            inventory.addBook(book);
        }
        bookPool.clearArrivals();
        System.out.println("Tüm yeni kitaplar raflara dizildi.");
    }

    public void addNewRequest(String title) {
        wishList.addRequest(title);
    }

    public void finalizeMonthEnd() {
        budget.executeMonthEndCycle();
        wishList.processPurchases(budget, inventory);
        System.out.println("Ay sonu işlemleri tamamlandı. Bütçe güncellendi ve alımlar yapıldı.");
    }

    public void showLibraryStatus() {
        System.out.println("=== KÜTÜPHANE GENEL DURUMU ===");
        System.out.println(inventory.toString());
        System.out.println(budget.toString());
        System.out.println("Bekleyen Emanet Toplamı: " + loanService.getTotalValueInDepositWait());
    }
}