import java.util.*;

public class Main {

    public static void main(String[] args) {
        LinkedList<Menu> katalog = Menu.generateDaftarMenu();
        Order pesan = new Order();
        Scanner scan = new Scanner(System.in);

        // Welcome Message
        tampilkanWelcome();
        System.out.println("Catatan:\nMaksimal pesan adalah 5 jenis untuk makanan dan makanan\n"
                              +"2 kuantitas untuk tiap jenis makanan dan 3 kuantitas untuk tiap jemis minuman.");

        Menu.listMenu(katalog);

        boolean lanjutPesan = true;

        while (lanjutPesan) {
            // 1. CEK AUTO-FINISH (Jika sudah 5 Makanan & 5 Minuman)
            int jumlahMakanan = pesan.getJumlahJenis(Makanan.class);
            int jumlahMinuman = pesan.getJumlahJenis(Minuman.class);
            
            if (jumlahMakanan >= 5 && jumlahMinuman >= 5) {
                System.out.println("\n[SISTEM] Kuota pesanan penuh (5 Makanan & 5 Minuman).");
                break; // Keluar dari loop
            }

            System.out.print("\nMasukkan Kode (Enter untuk Selesai; CC untuk Batal): ");
            String kode = scan.nextLine().trim();

            // 2. LOGIKA SELESAI (User cuma tekan Enter)
            if (kode.isEmpty()) {
                if (pesan.getKeranjang().isEmpty()) {
                    System.out.println("Harap masukkan kode menu terlebih dahulu!");
                    continue; 
                } else {
                    break; // Keluar dari loop untuk cetak nota final
                }
            }

            // 3. LOGIKA BATAL TOTAL
            if (kode.equalsIgnoreCase("CC")) {
                System.out.println("Transaksi dibatalkan.");
                System.exit(0);
            }

            // 4. CARI MENU DI KATALOG
            Menu terpilih = cariMenu(katalog, kode);
            if (terpilih == null) {
                System.out.println("Kode tidak valid! Cek kembali kode menu.");
                continue;
            }

            // 5. VALIDASI SLOT JENIS (Maksimal 5 per kategori)
            if (pesan.getJumlahJenis(terpilih.getClass()) >= 5 && !pesan.apakahSudahAdaDiKeranjang(terpilih)) {
                System.out.println("Gagal! Slot untuk jenis " + terpilih.getClass().getSimpleName() + " sudah penuh (Maks 5).");
                continue;
            }

            // 6. VALIDASI PORSI MAKSIMAL (2 untuk Makanan, 3 untuk Minuman)
            if (pesan.apakahSudahMaksimal(terpilih)) {
                System.out.println("Gagal! Anda sudah mencapai batas maksimal porsi untuk menu ini.");
                continue;
            }

            // 7. INPUT KUANTITAS
            System.out.print("Masukkan Kuantitas (default 1; 0 atau S untuk hapus): ");
            String qtyStr = scan.nextLine().trim();
            
            // 8. PROSES & UPDATE TABEL
            pesan.tambahAtauUpdatePesanan(terpilih, qtyStr);
            pesan.tampilkanTabelPesanan();
            
        }
            System.out.println("\n" + "=".repeat(65));
            System.out.println("     ANDA SELESAI MEMESAN. BERIKUT PESANAN FINAL ANDA:");
            System.out.println("=".repeat(65));
            // Tampilkan tagihan lengkap dengan pajak
            pesan.tampilkanTagihan();
            // Tambahan: lanjut ke channel pembayaran
            double totalAkhir = pesan.getTotalAkhir();
            PaymentProcessor processor = new PaymentProcessor();
            processor.jalankan(totalAkhir, scan);
    }

    private static Menu cariMenu(LinkedList<Menu> list, String kode) {
        for (Menu m : list) {
            if (m.getKode().equalsIgnoreCase(kode)) return m;
        }
        return null;
    }

    private static void tampilkanWelcome() {
        String title = "SELAMAT DATANG DI KOHISOP!";
        String subtitle = "Mau pesan apa hari ini?";
        
        int width = 65;
        String border = "═".repeat(width);
        
        int titlePadding = (width - title.length()) / 2;
        String centeredTitle = " ".repeat(titlePadding) + title + " ".repeat(width - titlePadding - title.length());
        
        int subtitlePadding = (width - subtitle.length()) / 2;
        String centeredSubtitle = " ".repeat(subtitlePadding) + subtitle + " ".repeat(width - subtitlePadding - subtitle.length());
        
        System.out.println("\n" + "=".repeat(width));
        System.out.println("╔" + border + "╗");
        System.out.println("║" + centeredTitle + "║");
        System.out.println("║" + centeredSubtitle + "║");
        System.out.println("╚" + border + "╝");
        System.out.println("=".repeat(width));
    }
}
