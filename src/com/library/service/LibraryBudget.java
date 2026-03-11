package com.library.service;

public class LibraryBudget {
    private final double yearlyGovernmentGrant;
    private final double monthlyFixedExpenses;
    private double accumulatedBalance; // Ana Kasa (Geçen aylardan devreden)
    private double currentMonthPool;   // Bu ayki sıcak para (Bağış, Ceza, Üyelik)

    public LibraryBudget(double yearlyGovernmentGrant, double monthlyFixedExpenses) {
        this.yearlyGovernmentGrant = yearlyGovernmentGrant;
        this.monthlyFixedExpenses = monthlyFixedExpenses;
        this.accumulatedBalance = 0.0;
        this.currentMonthPool = 0.0;
    }

    /**
     * Bağışlar, Gecikme Cezaları ve Üyelik Ücretleri buraya akar.
     */
    public void addToCurrentMonthPool(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Amount must be positive!");

        this.currentMonthPool += amount;
    }

    /**
     * Ay sonu döngüsü: Devlet hibesi eklenir, giderler düşülür
     * ve aydan kalan tüm para ana kasaya (accumulatedBalance) devredilir.
     */
    public void executeMonthEndCycle() {
        double monthlyGrant = yearlyGovernmentGrant / 12;

        // Formül: Mevcut Kasa + (Hibe + Bu ay toplananlar - Sabit Giderler)
        this.accumulatedBalance += (monthlyGrant + currentMonthPool - monthlyFixedExpenses);

        // Ay kapandığı için havuz sıfırlanır
        this.currentMonthPool = 0.0;
    }

    /**
     * Harcanabilir Toplam Para: Kasa + Bu ay toplanan sıcak para
     */
    public double getAvailableBalance() {
        return accumulatedBalance + currentMonthPool;
    }

    /**
     * Kitap satın alırken önce ana kasadan, yetmezse aylık havuzdan düşer.
     */
    public void deductPurchaseAmount(double amount) {
        if (amount <= 0)
            throw new IllegalArgumentException("Expense must be positive!");

        double totalAvailable = getAvailableBalance();

        if (amount > totalAvailable) {
            throw new IllegalStateException("Insufficient funds! Total Available (Cash + Pool): " + totalAvailable);
        }

        // Parayı önce birikmiş bakiyeden düş, yetmezse havuzdan eksilt
        if (accumulatedBalance >= amount) {
            this.accumulatedBalance -= amount;
        } else {
            double remaining = amount - accumulatedBalance;
            this.accumulatedBalance = 0;
            this.currentMonthPool -= remaining;
        }
    }

    @Override
    public String toString() {
        return String.format("Library Budget Status:%n" +
                        " > Total Cash (Accumulated): %.2f TL%n" +
                        " > Current Month Pool (Hot Cash): %.2f TL%n" +
                        " > Total Spendable: %.2f TL%n" +
                        " > Monthly Grant: %.2f TL | Monthly Expenses: %.2f TL",
                accumulatedBalance, currentMonthPool, getAvailableBalance(),
                (yearlyGovernmentGrant / 12), monthlyFixedExpenses);
    }
}