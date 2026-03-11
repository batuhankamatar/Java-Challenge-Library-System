import com.library.entity.abstracts.*;
import com.library.entity.concrete.*;
import com.library.enums.*;
import com.library.service.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Main {
    private static LibraryManager manager;
    private static Map<String, AbstractMember> memberRegistry = new HashMap<>();
    private static LocalDate systemDate = LocalDate.now();
    private static final Scanner scanner = new Scanner(System.in);

    private static BookPool bookPool;
    private static LoanService loanService;
    private static LibraryBudget budget;

    public static void main(String[] args) {
        initSystem();
        runConsole();
    }

    private static void initSystem() {
        budget = new LibraryBudget(12000.0, 500.0);
        loanService = new LoanService(budget);
        LibraryInventory inventory = new LibraryInventory();
        bookPool = new BookPool();
        manager = new LibraryManager(inventory, loanService, budget, new WishList(), bookPool);

        // --- BAŞLANGIÇ VERİLERİ ---
        inventory.addBook(new CrimeBook("Gülün Adı", "Umberto Eco", 200.0));
        inventory.addBook(new CrimeBook("Gülün Adı", "Umberto Eco", 200.0));
        inventory.addBook(new FictionBook("Puslu Kıtalar Atlası", "İhsan Oktay Anar", 150.0));

        String[][] bookData = {
                {"Suç ve Ceza", "Dostoyevski", "Crime"},
                {"Sefiller", "Victor Hugo", "Classic"},
                {"1984", "George Orwell", "Fiction"},
                {"Vakıf", "Isaac Asimov", "SciFi"},
                {"Devlet", "Platon", "Philosophy"},
                {"Sapiens", "Yuval Noah Harari", "History"},
                {"Hobbit", "J.R.R. Tolkien", "Fantastic"},
                {"Nutuk", "Mustafa Kemal Atatürk", "History"},
                {"Cesur Yeni Dünya", "Aldous Huxley", "SciFi"},
                {"Kürk Mantolu Madonna", "Sabahattin Ali", "Fiction"}
        };

        for (int i = 0; i < 100; i++) {
            String[] data = bookData[i % bookData.length];
            double price = 100.0 + (i * 2);

            AbstractBaseBook book = switch (data[2]) {
                case "Crime" -> new CrimeBook(data[0], data[1], price);
                case "Fiction" -> new FictionBook(data[0], data[1], price);
                case "SciFi" -> new SciFiBook(data[0], data[1], price);
                case "Philosophy" -> new PhilosophyBook(data[0], data[1], price);
                case "Fantastic" -> new FantasticBook(data[0], data[1], price);
                case "Classic" -> new ClassicBook(data[0], data[1], price);
                default -> new HistoryBook(data[0], data[1], price);
            };
            inventory.addBook(book);
        }

        AbstractMember m1 = new StandardMember("Ali", "Kaya");
        memberRegistry.put(m1.getId(), m1);
        budget.addToCurrentMonthPool(m1.getType().getPrice());

        System.out.println(">>> Kütüphane Otomasyonu Başlatıldı.");
        System.out.println(">>> Toplam 103 kitap raflara dizildi.");
        System.out.println(">>> Mevcut Tarih: " + systemDate);
    }

    private static void runConsole() {
        while (true) {
            printHeader();
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1" -> registerNewMember();
                    case "2" -> listMembers();
                    case "3" -> handleLoanProcess();
                    case "4" -> handleReturnProcess();
                    case "5" -> listInventory();
                    case "6" -> listByGenre();
                    case "7" -> searchByAuthor();
                    case "8" -> updateOrDeleteBook();
                    case "9" -> buyNewBook();
                    case "10" -> manageBookPool();
                    case "11" -> payMemberDebt();
                    case "12" -> handleDonation();
                    case "13" -> systemTimeFastForward();
                    case "14" -> deleteMember();
                    case "0" -> System.exit(0);
                    default -> System.out.println("![HATA] Geçersiz seçenek.");
                }
            } catch (Exception e) {
                System.out.println("![HATA] " + e.getMessage());
            }
        }
    }

    private static void registerNewMember() {
        System.out.println("\n--- YENİ ÜYE KAYDI (İptal için '0' yazın) ---");
        System.out.print("İsim: "); String n = scanner.nextLine();
        if (n.equals("0")) return;
        System.out.print("Soyisim: "); String s = scanner.nextLine();
        if (s.equals("0")) return;
        System.out.println("Üyelik Tipi: 1.Standard 2.Student 3.Premium 4.Guest (Vazgeç: 0)");
        String type = scanner.nextLine();
        if (type.equals("0")) return;

        AbstractMember member = switch (type) {
            case "2" -> new StudentMember(n, s);
            case "3" -> new PremiumMember(n, s);
            case "4" -> new GuestMember(n, s);
            default -> new StandardMember(n, s);
        };
        memberRegistry.put(member.getId(), member);
        budget.addToCurrentMonthPool(member.getType().getPrice());
        System.out.println("Üye kaydedildi. Ücret tahsil edildi.");
    }

    private static void deleteMember() {
        System.out.println("\n--- ÜYE SİLDİRME ---");
        AbstractMember member = selectMember();
        if (member == null) return;
        if (!member.getBorrowedBooks().isEmpty()) {
            System.out.println("![HATA] Üyenin üzerinde kitap varken silinemez!");
            return;
        }
        System.out.print(member.getName() + " isimli üyeyi silmek istediğinize emin misiniz? (E/H): ");
        if (scanner.nextLine().equalsIgnoreCase("E")) {
            memberRegistry.remove(member.getId());
            System.out.println("Üye sistemden silindi.");
        }
    }

    private static void handleLoanProcess() {
        System.out.println("\n--- ÖDÜNÇ VERME İŞLEMİ ---");
        AbstractMember member = selectMember();
        if (member == null) return;
        System.out.print("Aranacak Kitap Adı (Geri: 0): ");
        String title = scanner.nextLine();
        if (title.equals("0")) return;

        List<AbstractBaseBook> results = manager.searchBookByTitle(title);
        if (results.isEmpty()) {
            System.out.println("Kitap yok. Wishlist'e ekleniyor.");
            manager.addNewRequest(title);
            return;
        }
        System.out.println("\nBulunan Kopyalar:");
        results.forEach(b -> System.out.printf("[%s] %s - %s | Durum: %s%n", b.getId().substring(0,8), b.getTitle(), b.getAuthor(), b.getStatus()));
        System.out.print("\nÖdünç verilecek ID (ilk 8 hane) veya İptal (0): ");
        String sid = scanner.nextLine();
        if (sid.equals("0")) return;

        AbstractBaseBook selectedBook = results.stream().filter(b -> b.getId().startsWith(sid)).findFirst().orElse(null);
        if (selectedBook != null && selectedBook.getStatus() == BookStatus.AVAILABLE) {
            manager.loanBookToMember(member, selectedBook, systemDate);
            System.out.println("İŞLEM TAMAM.");
        } else {
            System.out.println("![HATA] Kitap seçilemedi veya müsait değil.");
        }
    }

    private static void handleReturnProcess() {
        System.out.println("\n--- İADE ALMA İŞLEMİ ---");
        AbstractMember member = selectMember();
        if (member == null || member.getBorrowedBooks().isEmpty()) {
            System.out.println("Üyenin üzerinde kitap bulunmuyor.");
            return;
        }
        System.out.println("Üyenin üzerindeki kitaplar:");
        member.getBorrowedBooks().forEach(b -> System.out.println("- " + b.getTitle() + " (ID: " + b.getId().substring(0,8) + ")"));
        System.out.print("İade edilecek ID (İptal: 0): ");
        String sid = scanner.nextLine();
        if (sid.equals("0")) return;

        AbstractBaseBook book = member.getBorrowedBooks().stream().filter(b -> b.getId().startsWith(sid)).findFirst().orElse(null);
        if (book != null) {
            System.out.println("Kitap Durumu: 1.Sağlam 2.Hasarlı 3.Kayıp (Vazgeç: 0)");
            String cond = scanner.nextLine();
            if (cond.equals("0")) return;

            // Eğer kitap hasarlı veya kayıpsa LoanService içindeki ceza mantığını tetikler
            if (cond.equals("2")) book.setStatus(BookStatus.DAMAGED);
            else if (cond.equals("3")) book.setStatus(BookStatus.LOST);
            else book.setStatus(BookStatus.AVAILABLE);

            manager.returnBookFromMember(member, book, systemDate);
            System.out.println("İade işlemi tamamlandı.");
        }
    }

    private static void listByGenre() {
        System.out.print("Kategori adı (Vazgeç: 0): ");
        String input = scanner.nextLine().toUpperCase();
        if (input.equals("0")) return;
        try {
            manager.getBooksInGenre(BookGenre.valueOf(input)).forEach(System.out::println);
        } catch (Exception e) { System.out.println("Hata: " + e.getMessage()); }
    }

    private static void searchByAuthor() {
        System.out.print("Yazar (Geri: 0): ");
        String a = scanner.nextLine();
        if (a.equals("0")) return;
        manager.searchBookByTitle("").stream().filter(b -> b.getAuthor().toLowerCase().contains(a.toLowerCase())).forEach(System.out::println);
    }

    private static void updateOrDeleteBook() {
        System.out.println("\n1. Kitap Sil | 2. Fiyat Güncelle | 0. Geri");
        String choice = scanner.nextLine();
        if (choice.equals("0")) return;
        System.out.print("Kitap ID (8 hane): ");
        String sid = scanner.nextLine();
        if (sid.equals("0")) return;

        // 8 haneli ID ile gerçek kitabı bulma
        AbstractBaseBook target = manager.searchBookByTitle("").stream()
                .filter(b -> b.getId().startsWith(sid))
                .findFirst().orElse(null);

        if (target == null) {
            System.out.println("![HATA] Kitap bulunamadı.");
            return;
        }

        if (choice.equals("1")) {
            manager.removeBrokenBook(target.getId());
        } else if (choice.equals("2")) {
            System.out.print("Yeni Fiyat: ");
            double np = Double.parseDouble(scanner.nextLine());
            target.setPrice(np);
            System.out.println("Fiyat güncellendi.");
        }
    }

    private static void buyNewBook() {
        System.out.println("\n--- YENİ KİTAP SATIN AL (Geri: 0) ---");
        System.out.print("Ad: "); String t = scanner.nextLine();
        if (t.equals("0")) return;
        System.out.print("Yazar: "); String a = scanner.nextLine();
        System.out.print("Fiyat: "); double p = Double.parseDouble(scanner.nextLine());

        System.out.println("Tür Seçiniz:");
        System.out.println("1.Fiction   2.Academic   3.History   4.Science   5.Philosophy");
        System.out.println("6.Biography 7.Fantastic  8.Sci-Fi    9.Classic   10.Crime    (Vazgeç: 0)");
        String gc = scanner.nextLine();

        AbstractBaseBook newBook = switch (gc) {
            case "2" -> new AcademicBook(t, a, p);
            case "3" -> new HistoryBook(t, a, p);
            case "4" -> new ScienceBook(t, a, p);
            case "5" -> new PhilosophyBook(t, a, p);
            case "6" -> new BiographyBook(t, a, p);
            case "7" -> new FantasticBook(t, a, p);
            case "8" -> new SciFiBook(t, a, p);
            case "9" -> new ClassicBook(t, a, p);
            case "10" -> new CrimeBook(t, a, p);
            case "0" -> null;
            default -> new FictionBook(t, a, p);
        };

        if (newBook == null) return;

        if (budget.getAvailableBalance() >= p) {
            budget.deductPurchaseAmount(p);
            bookPool.addNewArrival(newBook);
            System.out.println("Satın alındı, havuzda bekliyor.");
        } else {
            System.out.println("Bakiye yetersiz.");
        }
    }

    private static void manageBookPool() {
        System.out.println("\n--- KİTAP HAVUZU (BOOK POOL) DURUMU ---");
        System.out.println("Yeni Gelen Kitaplar: " + bookPool.getNewArrivals().size());
        System.out.println("İade Bekleyen Kitaplar: " + bookPool.getReturnedBooks().size());
        if ((bookPool.getNewArrivals().size() + bookPool.getReturnedBooks().size()) > 0) {
            System.out.print("İşlensin mi? (E/H): ");
            if (scanner.nextLine().equalsIgnoreCase("E")) manager.processBookPool();
        }
    }

    private static void payMemberDebt() {
        AbstractMember m = selectMember();
        if (m != null) {
            System.out.print("Ödeme Miktarı: ");
            manager.memberPaysDebt(m, Double.parseDouble(scanner.nextLine()));
        }
    }

    private static void handleDonation() {
        AbstractMember m = selectMember();
        if (m == null) return;
        System.out.println("1. Para | 2. Kitap | 0. Geri");
        String c = scanner.nextLine();
        if (c.equals("1")) {
            System.out.print("Bağış Miktarı: ");
            double amt = Double.parseDouble(scanner.nextLine());
            m.donate(amt);
            budget.addToCurrentMonthPool(amt);
            System.out.println("Bağış kabul edildi.");
        } else if (c.equals("2")) {
            System.out.print("Kitap Adı: "); String t = scanner.nextLine();
            System.out.print("Yazar: "); String a = scanner.nextLine();
            System.out.println("Kitap Türü (Örn: Fiction, History): ");
            String genre = scanner.nextLine();
            // Basit bir bağış kitabı oluşturma (Geliştirilebilir)
            bookPool.addNewArrival(new FictionBook(t, a, 100.0));
            m.donate(50.0);
            System.out.println("Bağış kabul edildi.");
        }
    }

    private static void systemTimeFastForward() {
        System.out.print("Kaç gün ilerle: ");
        int days = Integer.parseInt(scanner.nextLine());
        systemDate = systemDate.plusDays(days);
        System.out.println("Yeni Tarih: " + systemDate);
    }

    private static AbstractMember selectMember() {
        System.out.print("Üye Ad Soyad veya ID (İptal için '0'): ");
        String query = scanner.nextLine().toLowerCase().trim();
        if (query.equals("0")) return null;
        for (AbstractMember m : memberRegistry.values()) {
            String fullName = (m.getName() + " " + m.getSurname()).toLowerCase();
            if (fullName.contains(query) || m.getId().toLowerCase().startsWith(query)) return m;
        }
        System.out.println("![UYARI] Üye bulunamadı.");
        return null;
    }

    private static void listMembers() {
        System.out.println("\n--- DETAYLI ÜYE LİSTESİ ---");
        if (memberRegistry.isEmpty()) { System.out.println("Kayıtlı üye bulunmamaktadır."); return; }
        for (AbstractMember m : memberRegistry.values()) {
            System.out.println("-------------------------------------------");
            System.out.printf("ÜYE: %s %s [%s] ID: %s%n", m.getName(), m.getSurname(), m.getType(), m.getId());
            System.out.printf("Borç: %.2f TL | Bağış: %.2f TL | Kitap Limiti: %d/%d%n", m.getTotalDebt(), m.getTotalDonation(), m.getBorrowedBooks().size(), m.getBookLimit());
            if (!m.getBorrowedBooks().isEmpty()) {
                System.out.println("Kitaplar:");
                for (AbstractBaseBook b : m.getBorrowedBooks()) {
                    LocalDate loanDate = loanService.getAllLoanedBooks().get(b.getId());
                    LocalDate effectiveDate = (loanDate != null) ? loanDate : systemDate;
                    long daysUsed = ChronoUnit.DAYS.between(effectiveDate, systemDate);
                    long daysLeft = 14 - daysUsed;
                    double dep = loanService.getAllActiveDeposits().getOrDefault(b.getId(), 0.0);
                    System.out.printf("  > %-20s | Alındığı Tarih: %s | Kalan Gün: %d | Depozito: %.2f%n", b.getTitle(), effectiveDate, daysLeft, dep);
                }
            }
        }
    }

    private static void listInventory() {
        System.out.println("\n--- RAF DURUMU (TreeSet Sıralı) ---");
        for (BookGenre g : BookGenre.values()) {
            List<AbstractBaseBook> books = manager.getBooksInGenre(g);
            if(!books.isEmpty()) {
                System.out.println("\n[" + g + "]");
                books.forEach(b -> System.out.printf("%-10s | %-20s | %-15s | %s%n", b.getId().substring(0,8), b.getTitle(), b.getAuthor(), b.getStatus()));
            }
        }
    }

    private static void printHeader() {
        int poolSize = bookPool.getNewArrivals().size() + bookPool.getReturnedBooks().size();
        System.out.println("\n" + "=".repeat(60));
        System.out.printf(" TARİH: %s | ENVANTER: %d | HAVUZ: %d Kitap%n", systemDate, manager.searchBookByTitle("").size(), poolSize);
        System.out.printf(" KASA (Harcanabilir): %.2f TL%n", budget.getAvailableBalance());
        System.out.println("=".repeat(60));
        System.out.println(" 1. Üye Kaydı");
        System.out.println(" 2. Üye Listesi (Detaylı)");
        System.out.println(" 3. Kitap Ödünç Ver");
        System.out.println(" 4. Kitap İade Al");
        System.out.println(" 5. Tüm Envanteri Listele");
        System.out.println(" 6. Kategoriye Göre Listele");
        System.out.println(" 7. Yazara Göre Kitap Ara");
        System.out.println(" 8. Kitap Sil / Güncelle");
        System.out.println(" 9. Yeni Kitap Satın Al");
        System.out.println("10. Kitap Havuzunu İşle");
        System.out.println("11. Üye Borç Ödeme");
        System.out.println("12. Bağış Yap");
        System.out.println("13. Zamanı İlerlet (+Gün)");
        System.out.println("14. ÜYE SİL");
        System.out.println(" 0. Çıkış");
        System.out.print("Seçiminiz: ");
    }
}