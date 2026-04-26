import java.util.*;

public class Main {

    public static void main(String[] args) {
        LinkedList<Menu> katalog = Menu.generateDaftarMenu();
        Order pesan = new Order();
        Scanner scan = new Scanner(System.in);

        // Welcome Message
        tampilkanWelcome();
        
        Menu.listMenu(katalog);

        boolean lanjutPesan = true;
        while (lanjutPesan) {
            System.out.print("Masukkan Kode Menu (CC untuk Batal): ");
            String kode = scan.nextLine().trim();

            // BATAL
            if (kode.equalsIgnoreCase("CC")) {
                System.out.println("\n✗ Transaksi dibatalkan.");
                System.exit(0);
            }

            Menu terpilih = cariMenu(katalog, kode);
            if (terpilih == null) {
                System.out.println("❌ Kode tidak valid! Cek kembali kode menu.\n");
                continue;
            }

            // Cek kuota penuh
            int jumlahMakanan = pesan.getJumlahJenis(Makanan.class);
            int jumlahMinuman = pesan.getJumlahJenis(Minuman.class);
            if (jumlahMakanan >= 5 && jumlahMinuman >= 5
                    && !pesan.apakahSudahAdaDiKeranjang(terpilih)) {
                System.out.println("\n[GAGAL] Kuota pesanan penuh (5 Makanan & 5 Minuman).");
                continue;
            }

            if (pesan.apakahSudahMaksimal(terpilih)) {
                System.out.println("Anda sudah mencapai batas maksimal untuk menu ini.");
                continue;
            }

            if (pesan.getJumlahJenis(terpilih.getClass()) >= 5
                    && !pesan.apakahSudahAdaDiKeranjang(terpilih)) {
                System.out.println("Sudah mencapai batas pemesanan, yakni 5 jenis.");
                continue;
            }

            System.out.print("Masukkan Kuantitas (default 1; 0 atau S untuk batal): ");
            String qtyStr = scan.nextLine();
            pesan.tambahAtauUpdatePesanan(terpilih, qtyStr);

            // Tampilkan keranjang sementara
            pesan.tampilkanTabelPesanan();
            
            // Tanya apakah ada pesanan lain
            if (!pesan.getKeranjang().isEmpty()) {
                boolean inputValid = false;
                while (!inputValid) {
                    System.out.print("\nApakah ada pesanan lain? (Y/N): ");
                    String jawab = scan.nextLine().trim().toLowerCase();
                    
                    if (jawab.equals("y") || jawab.equals("yes")) {
                        inputValid = true;
                        // Lanjut ke loop berikutnya
                    } else if (jawab.equals("n") || jawab.equals("no")) {
                        lanjutPesan = false;
                        inputValid = true;
                        System.out.println("\n" + "=".repeat(65));
                        System.out.println("     ANDA SELESAI MEMESAN. BERIKUT PESANAN FINAL ANDA:");
                        System.out.println("=".repeat(65));
                        // Tampilkan tagihan lengkap dengan pajak
                        pesan.tampilkanTagihan();
                        // Tambahan: lanjut ke channel pembayaran
                        double totalAkhir = pesan.getTotalAkhir();
                        PaymentProcessor processor = new PaymentProcessor();
                        processor.jalankan(totalAkhir, scan);
                    } else {
                        System.out.println("Input tidak valid! Silakan masukkan Y atau N.");
                    }
                }
            }
        }
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
