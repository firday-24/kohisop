package processor;

import model.*;
import java.util.ArrayList;
import java.util.Scanner;

public class OrderProcessor {
    private ArrayList<Menu> daftarMenu; // Penyimpanan internal katalog menu toko
    private Order orderBaru;            // Objek transaksi pesanan aktif
    private Scanner scanner;            // Alat membaca input keyboard kasir

    // Inisialisasi awal sistem kasir processor
    public OrderProcessor() {
        this.daftarMenu = new ArrayList<>();
        this.orderBaru = new Order();
        this.scanner = new Scanner(System.in);
        inisialisasiMenuToko(); // Memuat data produk jualan ke memori
    }

    // Fungsi Helper untuk membuat perataan teks otomatis berada di tengah layout judul
    private String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        
        int totalSpaces = width - text.length();
        int leftSpaces = totalSpaces / 2;
        int rightSpaces = totalSpaces - leftSpaces;
        
        return " ".repeat(leftSpaces) + text + " ".repeat(rightSpaces);
    }

    // Mesin utama / alur berjalannya siklus transaksi aplikasi kasir KohiSop
    public void run() {
        int lebarLayout = 57; // Batas lebar simetris judul sambutan atas

        System.out.println("==========================================================");
        System.out.println(centerText("SELAMAT DATANG DI KOHISOP!", lebarLayout));
        System.out.println(centerText("Mau pesan apa hari ini?", lebarLayout));
        System.out.println("==========================================================");
        tampilkanDaftarMenuToko();

        // Loop meminta input transaksi dari pengguna terus menerus
        while (true) {
            System.out.print("\nMasukkan Kode (Enter untuk Selesai; CC untuk Batal): ");
            String inputKode = scanner.nextLine().trim();

            // Skenario 1: Jika user menekan Enter langsung tanpa input teks -> Selesai belanja
            if (inputKode.isEmpty()) {
                break; 
            }

            // PROSES UTAMA 1: Validasi input kode dan pencarian menu (Diekstrak ke fungsi)
            Menu menuTerpilih = validasiDanAmbilMenu(inputKode);
            if (menuTerpilih == null) {
                continue; // Ulangi meminta input kode baru jika tidak valid
            }

            // PROSES UTAMA 2: Validasi batasan slot unik kategori maks 5 (Diekstrak ke fungsi)
            if (!validasiSlotKategori(menuTerpilih)) {
                continue; // Kembali minta kode menu lain jika slot penuh
            }

            // PROSES UTAMA 3: Tahap pengisian kuantitas porsi
            prosesInputKuantitas(menuTerpilih);
        }

        // Pesanan selesai — kuitansi ditampilkan oleh MembershipReceiptProcessor
    }

    /**
     * PROSES UTAMA: Validasi input kode dari user dan mencarinya di katalog menu.
     * Juga menangani fitur pembatalan 'CC'.
     */
    private Menu validasiDanAmbilMenu(String inputKode) {
        // Skenario 2: Jika user mengetik kode pembatalan "CC" -> Hentikan paksa aplikasi
        if (inputKode.equalsIgnoreCase("CC")) {
            System.out.println("Pesanan dibatalkan. Program berhenti.");
            System.exit(0);
        }

        // Validasi keberadaan kode produk di database katalog toko
        Menu menuTerpilih = cariMenu(inputKode);
        if (menuTerpilih == null) {
            System.out.println("Kode tidak valid! Cek kembali kode menu.");
            return null;
        }

        return menuTerpilih;
    }

    /**
     * PROSES UTAMA: Validasi apakah menu baru masih bisa dimasukkan 
     * berdasarkan kuota maksimal 5 jenis unik per kategori.
     */
    private boolean validasiSlotKategori(Menu menuTerpilih) {
        // Jika menu belum pernah dipesan, periksa ketersediaan slot uniknya (maksimal 5)
        if (!orderBaru.apakahMenuSudahAda(menuTerpilih) && !orderBaru.apakahSlotTersedia(menuTerpilih)) {
            String kategori = (menuTerpilih instanceof Makanan) ? "Makanan" : "Minuman";
            System.out.println("Gagal! Slot untuk jenis " + kategori + " sudah penuh (Maks 5).");
            return false; // Validasi gagal
        }
        return true; // Validasi sukses
    }

    // Menangani pembacaan data kuantitas serta opsi skip/hapus menu sementara
    private void prosesInputKuantitas(Menu menu) {
        while (true) {
            try {
                System.out.print("Masukkan Kuantitas (default 1; 0 atau S untuk hapus): ");
                String inputQty = scanner.nextLine().trim();

                // Mendukung pembatalan transaksi global di tengah-tengah input kuantitas
                if (inputQty.equalsIgnoreCase("CC")) {
                    System.out.println("Pesanan dibatalkan. Program berhenti.");
                    System.exit(0);
                }

                int kuantitas = 1; // Nilai porsi bawaan otomatis jika menekan Enter langsung

                // Opsi Skip/Batal Sementara: Jika mengetik 'S' atau '0', gagalkan entri item ini tanpa merusak sistem
                if (inputQty.equalsIgnoreCase("S") || inputQty.equals("0")) {
                    System.out.println("Pesanan untuk " + menu.getNama() + " dilewati.");
                    break; // Keluar dari loop kuantitas kembali ke menu input kode utama
                } else if (!inputQty.isEmpty()) {
                    kuantitas = Integer.parseInt(inputQty); // Parsing String ke Integer jika memasukkan angka porsi khusus
                }

                // Mengirimkan data menu dan kuantitas porsi ke objek keranjang belanja
                boolean sukses = orderBaru.tambahPesanan(menu, kuantitas);
                if (sukses) {
                    orderBaru.tampilkanKeranjang(); // Tampilkan preview struk realtime belanjaan terurut jika sukses
                    break; // Selesai memproses item ini
                }
            } catch (NumberFormatException e) {
                // Mencegah crash jika kasir tidak sengaja mengetik huruf acak bukan angka angka di kolom kuantitas
                System.out.println("Gagal! Input kuantitas tidak valid. Harap masukkan angka yang benar.");
            }
        }
    }

    // Helper pencarian objek menu berdasarkan string input kode produk
    private Menu cariMenu(String kode) {
        for (Menu m : daftarMenu) {
            if (m.getKode().equalsIgnoreCase(kode)) return m;
        }
        return null;
    }

    // Mencetak list menu papan tulis toko ke terminal di awal program dijalankan
    private void tampilkanDaftarMenuToko() {
        int lebarKode = 5;
        int lebarNama = 33;
        int lebarHarga = 11;

        System.out.println("Catatan:\nMaksimal pesan adalah 5 jenis untuk makanan dan minuman");
        System.out.println("Maksimal kuantitas untuk tiap jenis makanan adalah 2\nMaksimal kuantitas untuk tiap jenis minuman adalah 3");
         System.out.println("----------------------------------------------------------");
        System.out.printf("| %s | %s | %s |\n", centerText("Kode", lebarKode), centerText("Menu Makanan", lebarNama), centerText("Harga(Rp.)", lebarHarga));
        System.out.println("----------------------------------------------------------");
        for (Menu m : daftarMenu) {
            if (m instanceof Makanan) {
                System.out.printf("| %s | %-33s | %s |\n", 
                    centerText(m.getKode(), lebarKode), 
                    m.getNama(), // Rata kiri
                    centerText(String.format("%.0f", m.getHarga()), lebarHarga)
                );
            }
        }
        System.out.println("----------------------------------------------------------");
    
        System.out.printf("| %s | %s | %s |\n", centerText("Kode", lebarKode), centerText("Menu Minuman", lebarNama), centerText("Harga(Rp.)", lebarHarga));
        System.out.println("----------------------------------------------------------");
        for (Menu m : daftarMenu) {
            if (m instanceof Minuman) {
                System.out.printf("| %s | %-33s | %s |\n", 
                    centerText(m.getKode(), lebarKode), 
                    m.getNama(), // Rata kiri
                    centerText(String.format("%.0f", m.getHarga()), lebarHarga)
                );
            }
        }
        System.out.println("----------------------------------------------------------");
    }

    // Mengisi database list daftar menu
    private void inisialisasiMenuToko() {
        // Daftar Katalog Minuman
        daftarMenu.add(new Minuman("A1", "Caffe Latte", 46));
        daftarMenu.add(new Minuman("A2", "Cappuccino", 46));
        daftarMenu.add(new Minuman("E1", "Caffe Americano", 37));
        daftarMenu.add(new Minuman("E2", "Caffe Mocha", 55));
        daftarMenu.add(new Minuman("E3", "Caramel Macchiato", 59));
        daftarMenu.add(new Minuman("E4", "Asian Dolce Latte", 55));
        daftarMenu.add(new Minuman("E5", "Double Shots Iced Shaken Espresso", 50));
        daftarMenu.add(new Minuman("B1", "Freshly Brewed Coffee", 23));
        daftarMenu.add(new Minuman("B2", "Vanilla Sweet Cream Cold Brew", 50));
        daftarMenu.add(new Minuman("B3", "Cold Brew", 44));
        
        // Daftar Katalog Makanan
        daftarMenu.add(new Makanan("M1", "Petemania Pizza", 112));
        daftarMenu.add(new Makanan("M2", "Mie Rebus Super Mario", 35));
        daftarMenu.add(new Makanan("M3", "Ayam Bakar Goreng Rebus Spesial", 72));
        daftarMenu.add(new Makanan("M4", "Soto Kambing Iga Guling", 124));
        daftarMenu.add(new Makanan("S1", "Singkong Bakar A La Carte", 37));
        daftarMenu.add(new Makanan("S2", "Ubi Cilembu Bakar Arang", 58));
        daftarMenu.add(new Makanan("S3", "Tempe Mendoan", 18));
        daftarMenu.add(new Makanan("S4", "Tahu Bakso Extra Telur", 28));
    }

    public Order getOrderBaru() {
        return this.orderBaru;
    }
}
