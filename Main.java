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
    
            // SELESAI (User cuma tekan enter)
            if (kode.isEmpty()) {
               // Cek apakah keranjang masih kosong
               if (pesan.getKeranjang().isEmpty()) {
                 System.out.println("Harap masukkan kode menu terlebih dahulu!");
                 continue; // Balik lagi ke atas untuk minta input kode
               } 
               else {
                 lanjutPesan = false; 
                 System.out.println("\n=========================================================");
                 System.out.println("    ANDA SELESAI MEMESAN. BERIKUT PESANAN FINAL ANDA:    ");
                 pesan.tampilkanTabelPesanan();
                 break; 
               }
            }

            // CANCEL
            if (kode.equalsIgnoreCase("CC")) {
            System.out.println("Transaksi dibatalkan.");
            System.exit(0);
            }

            // Cari item di katalog
            Menu terpilih = cariMenu(katalog, kode);
            if (terpilih == null) {
                System.out.println("Kode tidak valid! Cek kembali kode menu.");
                continue;
            }

            // Cek sudah pernah dipesan? Sudah mentok?
            if (pesan.apakahSudahMaksimal(terpilih)) {
              System.out.println("Anda sudah pernah memesan ini sebelumnya dan sudah mencapai batas maksimal.");
              continue; // Balik ke input kode lagi, tidak nanya kuantitas
            }

            // Cek batas 5 jenis 
            if (pesan.getJumlahJenis(terpilih.getClass()) >= 5 && !pesan.apakahSudahAdaDiKeranjang(terpilih)) {
              System.out.println("Sudah mencapai batas pemesanan, yakni 5 jenis.");
              continue;
            }

            System.out.print("Masukkan Kuantitas (default 1; 0 atau S untuk batal memesan kode tersebut): ");
            String qtyStr = scan.nextLine();
            
            pesan.tambahAtauUpdatePesanan(terpilih, qtyStr);
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
