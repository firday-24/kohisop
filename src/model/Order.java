package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Order {
    private ArrayList<OrderLine> listPesanan;

    public Order() {
        this.listPesanan = new ArrayList<>();
    }

    public ArrayList<OrderLine> getListPesanan() {
        return listPesanan;
    }

    private String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        int sp = width - text.length();
        return " ".repeat(sp / 2) + text + " ".repeat(sp - sp / 2);
    }

    public boolean apakahMenuSudahAda(Menu menu) {
        for (OrderLine ol : listPesanan)
            if (ol.getMenu().getKode().equalsIgnoreCase(menu.getKode())) return true;
        return false;
    }

    public boolean apakahSlotTersedia(Menu menu) {
        int cm = 0, cmi = 0;
        for (OrderLine ol : listPesanan) {
            if (ol.getMenu() instanceof Makanan) cm++;
            if (ol.getMenu() instanceof Minuman) cmi++;
        }
        if (menu instanceof Makanan && cm  >= 5) return false;
        if (menu instanceof Minuman && cmi >= 5) return false;
        return true;
    }

    public boolean tambahPesanan(Menu menu, int kuantitas) {
        for (OrderLine ol : listPesanan) {
            if (ol.getMenu().getKode().equalsIgnoreCase(menu.getKode())) {
                ol.setKuantitas(ol.getKuantitas() + kuantitas);
                return true;
            }
        }
        if (!apakahSlotTersedia(menu)) return false;
        listPesanan.add(new OrderLine(menu, kuantitas));
        return true;
    }

    public double hitungTotalTanpaPajak() {
        double t = 0;
        for (OrderLine ol : listPesanan) {
            t += ol.getMenu().getHarga() * ol.getKuantitas();
        }
        return t;
    }

    public void tampilkanKeranjang() {
        int lebarKode = 5;
        int lebarNama = 33;
        int lebarHarga = 11; // Kolom baru untuk Harga Satuan
        int lebarQty = 11;

        // HANDLING KONDISI KOSONG
        if (listPesanan.isEmpty()) {
            System.out.println("\n=====================================================================");
            System.out.println("                         PESANAN ANDA                                ");
            System.out.println("=====================================================================");
            System.out.printf("| %s | %s | %s | %s |\n", centerText("Kode", lebarKode), centerText("Makanan", lebarNama), centerText("Harga(Rp.)", lebarHarga), centerText("Kuantitas", lebarQty));
            System.out.println("---------------------------------------------------------------------");
            System.out.printf("| %s | %-33s | %s | %s |\n", centerText("-", lebarKode), "Tidak ada pesanan makanan", centerText("-", lebarHarga), centerText("-", lebarQty));
            System.out.println("---------------------------------------------------------------------");
            System.out.printf("| %s | %s | %s | %s |\n", centerText("Kode", lebarKode), centerText("Minuman", lebarNama), centerText("Harga(Rp.)", lebarHarga), centerText("Kuantitas", lebarQty));
            System.out.println("---------------------------------------------------------------------");
            System.out.printf("| %s | %-33s | %s | %s |\n", centerText("-", lebarKode), "Tidak ada pesanan minuman", centerText("-", lebarHarga), centerText("-", lebarQty));
            System.out.println("=====================================================================");
            return;
        }

        // Duplikasi list pesanan asli ke dalam list baru untuk kebutuhan sorting display
        ArrayList<OrderLine> sortedList = new ArrayList<>(listPesanan);

        // SORTING MULTI-LEVEL KRITERIA: Kategori -> Inisial Kode -> Harga Descending
        Collections.sort(sortedList, new Comparator<OrderLine>() {
            @Override
            public int compare(OrderLine o1, OrderLine o2) {
                String jenis1 = (o1.getMenu() instanceof Makanan) ? "Makanan" : "Minuman";
                String jenis2 = (o2.getMenu() instanceof Makanan) ? "Makanan" : "Minuman";

                // Kriteria 1: Kelompokkan Kategori (Makanan dulu baru Minuman)
                int jenisCompare = jenis1.compareTo(jenis2);
                if (jenisCompare != 0) return jenisCompare;

                // Kriteria 2: Kelompokkan berdasarkan Huruf Inisial Kode Alfabetis (M, S, A, E, B, dll)
                char inisial1 = o1.getMenu().getKode().toUpperCase().charAt(0);
                char inisial2 = o2.getMenu().getKode().toUpperCase().charAt(0);
                int inisialCompare = Character.compare(inisial1, inisial2);
                if (inisialCompare != 0) return inisialCompare;

                // Kriteria 3: Jika inisial sama, urutkan berdasarkan Harga Descending (Termahal -> Termurah)
                return Double.compare(o2.getMenu().getHarga(), o1.getMenu().getHarga());
            }
        });

        // CETAK TABEL KERANJANG BERISI DATA TERURUT 
        System.out.println("\n=========================================================================");
        System.out.println("                         PESANAN ANDA                                ");
        System.out.println("=========================================================================");
        System.out.printf("| %s | %s | %s | %s |\n", centerText("Kode", lebarKode), centerText("Makanan", lebarNama), centerText("Harga(Rp.)", lebarHarga), centerText("Kuantitas", lebarQty));
        System.out.println("-------------------------------------------------------------------------");
        
        // Loop 1: Khusus Menyaring dan Mencetak Sub-tabel Makanan
        int adaMakanan = 0;
        for (OrderLine ol : sortedList) {
            if (ol.getMenu() instanceof Makanan) {
                System.out.printf("| %s | %-33s | %s | %s |\n", 
                    centerText(ol.getMenu().getKode(), lebarKode), 
                    ol.getMenu().getNama(), // Nama rata kiri
                    centerText(String.format("%.0f", ol.getMenu().getHarga()), lebarHarga),
                    centerText(String.valueOf(ol.getKuantitas()), lebarQty)
                );
                adaMakanan++;
            }
        }
        if (adaMakanan == 0) {
            System.out.printf("| %s | %-33s | %s | %s |\n", centerText("-", lebarKode), "Tidak ada pesanan makanan", centerText("-", lebarHarga), centerText("-", lebarQty));
        }
        System.out.println("-------------------------------------------------------------------------");

        System.out.printf("| %s | %s | %s | %s |\n", centerText("Kode", lebarKode), centerText("Minuman", lebarNama), centerText("Harga(Rp.)", lebarHarga), centerText("Kuantitas", lebarQty));
        System.out.println("-------------------------------------------------------------------------");
        
        // Loop 2: Khusus Menyaring dan Mencetak Sub-tabel Minuman
        int adaMinuman = 0;
        for (OrderLine ol : sortedList) {
            if (ol.getMenu() instanceof Minuman) {
                System.out.printf("| %s | %-33s | %s | %s |\n", 
                    centerText(ol.getMenu().getKode(), lebarKode), 
                    ol.getMenu().getNama(), // Nama rata kiri
                    centerText(String.format("%.0f", ol.getMenu().getHarga()), lebarHarga),
                    centerText(String.valueOf(ol.getKuantitas()), lebarQty)
                );
                adaMinuman++;
            }
        }
        if (adaMinuman == 0) {
            System.out.printf("| %s | %-33s | %s | %s |\n", centerText("-", lebarKode), "Tidak ada pesanan minuman", centerText("-", lebarHarga), centerText("-", lebarQty));
        }
        System.out.println("=========================================================================");
    }
}
