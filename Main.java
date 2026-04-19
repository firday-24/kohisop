import java.util.*;

public class Main {

    public static void main(String[] args) {
        LinkedList<Menu> katalog = Menu.generateDaftarMenu();
        Order pesan = new Order();
        Scanner scan = new Scanner(System.in);

        Menu.listMenu(katalog);

        boolean lanjutPesan = true;
        while (lanjutPesan) {
            System.out.print("\nMasukkan Kode (Enter untuk Selesai; CC untuk Batal): ");
            String kode = scan.nextLine().trim();

            // SELESAI
            if (kode.isEmpty()) {
                if (pesan.getKeranjang().isEmpty()) {
                    System.out.println("Harap masukkan kode menu terlebih dahulu!");
                    continue;
                }
                lanjutPesan = false;
                System.out.println("\n=========================================================");
                System.out.println("   ANDA SELESAI MEMESAN. BERIKUT PESANAN FINAL ANDA:   ");
                System.out.println("=========================================================");
                // Tampilkan tagihan lengkap dengan pajak
                pesan.tampilkanTagihan();
                break;
            }

            // BATAL
            if (kode.equalsIgnoreCase("CC")) {
                System.out.println("Transaksi dibatalkan.");
                System.exit(0);
            }

            Menu terpilih = cariMenu(katalog, kode);
            if (terpilih == null) {
                System.out.println("Kode tidak valid! Cek kembali kode menu.");
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

            // Tampilkan keranjang sementara (tanpa pajak)
            pesan.tampilkanTabelPesanan();
        }
    }

    private static Menu cariMenu(LinkedList<Menu> list, String kode) {
        for (Menu m : list) {
            if (m.getKode().equalsIgnoreCase(kode)) return m;
        }
        return null;
    }
}
