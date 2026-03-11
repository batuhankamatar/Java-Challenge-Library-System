package com.library.service;

import com.library.entity.abstracts.AbstractBaseBook;
import com.library.entity.abstracts.AbstractMember;
import com.library.enums.BookStatus;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LoanService {
    // Key tipleri Long -> String olarak güncellendi
    private final Map<String, LocalDate> loanDates = new HashMap<>();
    private final Map<String, Double> activeDeposits = new HashMap<>();

    private final LibraryBudget budget;
    private final double DAILY_PENALTY = 5.0;
    private final int MAX_LOAN_DAYS = 14;

    public Map<String, LocalDate> getAllLoanedBooks() {
        return Collections.unmodifiableMap(loanDates);
    }

    public Map<String, Double> getAllActiveDeposits() {
        return Collections.unmodifiableMap(activeDeposits);
    }

    public double getTotalValueInDepositWait() {
        double total = 0;
        for (double amount : activeDeposits.values()) {
            total += amount;
        }
        return total;
    }

    public LoanService(LibraryBudget budget) {
        this.budget = budget;
    }

    /**
     * Ödünç verme işlemi.
     * Artık LocalDate.now() yerine simülasyondaki systemDate (currentDate) parametresini kullanıyor.
     */
    public void loanBook(AbstractMember member, AbstractBaseBook book, LocalDate currentDate) {
        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available!");
        }

        double depositAmount = book.getPrice() * member.getType().getDepositRate();

        member.addBorrowedBook(book);
        book.setStatus(BookStatus.LOANED);

        // Tarih artık parametre olarak gelen currentDate (sanal zaman) üzerinden saklanıyor
        loanDates.put(book.getId(), currentDate);
        activeDeposits.put(book.getId(), depositAmount);

        System.out.println(book.getTitle() + " ödünç verildi. Alınan depozito: " + depositAmount);
    }

    /**
     * İade alma işlemi.
     * Gecikme hesaplaması parametre olarak gelen currentDate (sanal zaman) üzerinden yapılıyor.
     */
    public void returnBook(AbstractMember member, AbstractBaseBook book, LocalDate currentDate) {
        // containsKey artık String bekliyor
        if (!loanDates.containsKey(book.getId())) {
            throw new IllegalArgumentException("Bu kitap bu servis üzerinden verilmemiş!");
        }

        if (!member.getBorrowedBooks().contains(book)) {
            throw new IllegalStateException("Üye, üzerinde olmayan bir kitabı iade etmeye çalışıyor!");
        }

        LocalDate loanDate = loanDates.get(book.getId());
        double takenDeposit = activeDeposits.get(book.getId());

        // Gecikme hesaplaması artık LocalDate.now() yerine currentDate (sanal zaman) kullanıyor
        long daysBetween = ChronoUnit.DAYS.between(loanDate, currentDate);

        double totalPenalty = 0.0;

        if (daysBetween > MAX_LOAN_DAYS) {
            long overdueDays = daysBetween - MAX_LOAN_DAYS;
            totalPenalty = overdueDays * DAILY_PENALTY;
            System.out.println("Gecikme tespit edildi! Gün: " + overdueDays + " Toplam Ceza: " + totalPenalty);
        }

        if (totalPenalty > 0) {
            if (totalPenalty <= takenDeposit) {
                double refund = takenDeposit - totalPenalty;
                budget.addToCurrentMonthPool(totalPenalty);
                System.out.println("Ceza depozitodan kesildi. Üyeye iade edilen: " + refund);
            } else {
                double extraDebt = totalPenalty - takenDeposit;
                budget.addToCurrentMonthPool(takenDeposit);
                member.addDebt(extraDebt);
                System.out.println("Depozito cezayı karşılamadı! Üyeye yansıtılan ek borç: " + extraDebt);
            }
        } else {
            System.out.println("Gecikme yok. Depozito (" + takenDeposit + ") iade edildi.");
        }

        member.removeBorrowedBook(book);

        // Önemli: Kitap statüsü Main'de Hasarlı/Kayıp olarak setlenmiş olabilir.
        // Eğer statü Main'de değiştirilmediyse (Hala LOANED ise) AVAILABLE yap.
        if (book.getStatus() == BookStatus.LOANED) {
            book.setStatus(BookStatus.AVAILABLE);
        }

        // Remove işlemleri String ID ile yapılıyor
        loanDates.remove(book.getId());
        activeDeposits.remove(book.getId());
    }
}