package model;

import java.util.ArrayList;
import java.util.Collections;
import processor.TaxCalculator;

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
                int total = ol.getKuantitas() + kuantitas;
                if (validasiKuantitas(menu, total)) { ol.setKuantitas(total); return true; }
                return false;
            }
        }
        if (validasiKuantitas(menu, kuantitas)) { listPesanan.add(new OrderLine(menu, kuantitas)); return true; }
        return false;
    }

    private boolean validasiKuantitas(Menu menu, int qty) {
        if (menu instanceof Makanan && qty > 2) {
            System.out.println("Gagal! Total pesanan " + menu.getNama() + " akan menjadi " + qty + " porsi. Maksimal per jenis hanya 2 porsi.");
            return false;
        }
        if (menu instanceof Minuman && qty > 3) {
            System.out.println("Gagal! Total pesanan " + menu.getNama() + " akan menjadi " + qty + " porsi. Maksimal per jenis hanya 3 porsi.");
            return false;
        }
        if (qty < 1) { System.out.println("Gagal! Jumlah kuantitas tidak valid."); return false; }
        return true;
    }

    
    public void tampilkanKeranjang() {
        int lK = 5, lN = 33, lQ = 11;
        ArrayList<OrderLine> s = getSortedByKode();

        if (listPesanan.isEmpty()) {
            System.out.println("\n===========================================================");
            System.out.println("                      PESANAN ANDA                         ");
            System.out.println("===========================================================");
            System.out.printf("| %s | %s | %s |\n", centerText("Kode",lK), centerText("Makanan",lN), centerText("Kuantitas",lQ));
            System.out.println("-----------------------------------------------------------");
            System.out.printf("| %s | %-33s | %s |\n", centerText("-",lK), "Tidak ada pesanan makanan", centerText("-",lQ));
            System.out.println("-----------------------------------------------------------");
            System.out.printf("| %s | %s | %s |\n", centerText("Kode",lK), centerText("Minuman",lN), centerText("Kuantitas",lQ));
            System.out.println("-----------------------------------------------------------");
            System.out.printf("| %s | %-33s | %s |\n", centerText("-",lK), "Tidak ada pesanan minuman", centerText("-",lQ));
            System.out.println("===========================================================");
            return;
        }

        System.out.println("\n==========================================================");
        System.out.println("                      PESANAN ANDA                         ");
        System.out.println("==========================================================");
        System.out.printf("| %s | %s | %s |\n", centerText("Kode",lK), centerText("Makanan",lN), centerText("Kuantitas",lQ));
        System.out.println("----------------------------------------------------------");
        int n = 0;
        for (OrderLine ol : s) if (ol.getMenu() instanceof Makanan) {
            System.out.printf("| %s | %-33s | %s |\n", centerText(ol.getMenu().getKode(),lK), ol.getMenu().getNama(), centerText(String.valueOf(ol.getKuantitas()),lQ));
            n++;
        }
        if (n == 0) System.out.printf("| %s | %-33s | %s |\n", centerText("-",lK), "Tidak ada pesanan makanan", centerText("-",lQ));
        System.out.println("----------------------------------------------------------");
        System.out.printf("| %s | %s | %s |\n", centerText("Kode",lK), centerText("Minuman",lN), centerText("Kuantitas",lQ));
        System.out.println("----------------------------------------------------------");
        n = 0;
        for (OrderLine ol : s) if (ol.getMenu() instanceof Minuman) {
            System.out.printf("| %s | %-33s | %s |\n", centerText(ol.getMenu().getKode(),lK), ol.getMenu().getNama(), centerText(String.valueOf(ol.getKuantitas()),lQ));
            n++;
        }
        if (n == 0) System.out.printf("| %s | %-33s | %s |\n", centerText("-",lK), "Tidak ada pesanan minuman", centerText("-",lQ));
        System.out.println("===========================================================");
    }

    // ─── KUITANSI DENGAN PAJAK (dipanggil setelah pesanan selesai) ────────────
    /**
     * Menampilkan kuitansi lengkap sesuai requirement PDF:
     *  - Kode, nama, harga per porsi, kuantitas, subtotal, pajak(%), pajak(Rp), total per baris
     *  - Pesanan dikelompokkan: Makanan dulu lalu Minuman, tiap kategori urut berdasarkan harga
     *  - Total sebelum pajak, total pajak, total dengan pajak
     *  - Placeholder untuk diskon channel & poin (akan diisi modul teman)
     *  - Judul kuitansi dan ucapan terima kasih
     *
     * @param kodeMember kode member aktif; null/"" jika bukan member
     */
    public void tampilkanKuitansiDenganPajak(String kodeMember) {
        if (listPesanan.isEmpty()) { System.out.println("Tidak ada pesanan."); return; }

        // Lebar kolom: Kode(4) | Nama(32) | Harga(8) | Qty(3) | Subtotal(10) | Pajak%(5) | Pajak Rp(10) | Total(11)
        String garis  = "=".repeat(97);
        String garisT = "-".repeat(97);

        System.out.println("\n" + garis);
        System.out.println(centerText("*** KUITANSI PEMBAYARAN KOHISOP ***", 97));
        System.out.println(garis);

        // Info bebas pajak member
        if (TaxCalculator.adalahmemberBebasPajak(kodeMember)) {
            System.out.println("  Status Member: " + kodeMember + " → BEBAS PAJAK (kode mengandung 'A')");
            System.out.println(garisT);
        } else if (kodeMember != null && !kodeMember.isEmpty()) {
            System.out.println("  Status Member: " + kodeMember);
            System.out.println(garisT);
        }

        // Header kolom
        System.out.printf("| %-4s | %-32s | %8s | %3s | %10s | %5s | %10s | %11s |\n",
            "Kode","Nama","Hrg/pcs","Qty","Subtotal","Pajak","Pajak(Rp)","Total");
        System.out.println(garisT);

        ArrayList<OrderLine> sorted = getSortedByHarga();

        double totalSubtotal = 0, totalPajak = 0, totalKeseluruhan = 0;

       
        boolean adaMakanan = false;
        for (OrderLine ol : sorted) {
            if (!(ol.getMenu() instanceof Makanan)) continue;
            if (!adaMakanan) {
                System.out.printf("| %-93s |\n", "  [MAKANAN]");
                adaMakanan = true;
            }
            cetakBaris(ol, kodeMember);
            Menu m = ol.getMenu(); int qty = ol.getKuantitas();
            double sub  = m.getHarga() * qty;
            double pjk  = TaxCalculator.hitungPajak(m, qty, kodeMember);
            totalSubtotal    += sub;
            totalPajak       += pjk;
            totalKeseluruhan += sub + pjk;
        }
        if (!adaMakanan) System.out.printf("| %-93s |\n", "  [MAKANAN] - tidak ada");

        System.out.println(garisT);

      
        boolean adaMinuman = false;
        for (OrderLine ol : sorted) {
            if (!(ol.getMenu() instanceof Minuman)) continue;
            if (!adaMinuman) {
                System.out.printf("| %-93s |\n", "  [MINUMAN]");
                adaMinuman = true;
            }
            cetakBaris(ol, kodeMember);
            Menu m = ol.getMenu(); int qty = ol.getKuantitas();
            double sub  = m.getHarga() * qty;
            double pjk  = TaxCalculator.hitungPajak(m, qty, kodeMember);
            totalSubtotal    += sub;
            totalPajak       += pjk;
            totalKeseluruhan += sub + pjk;
        }
        if (!adaMinuman) System.out.printf("| %-93s |\n", "  [MINUMAN] - tidak ada");

        // ── RINGKASAN ──
        System.out.println(garis);
        System.out.printf("  %-45s : Rp %,.2f\n", "Total harga seluruh pesanan (sebelum pajak)", totalSubtotal);
        System.out.printf("  %-45s : Rp %,.2f\n", "Total pajak", totalPajak);
        System.out.println(garisT);
        System.out.printf("  %-45s : Rp %,.2f\n", "Total harga seluruh pesanan (dengan pajak)", totalKeseluruhan);
        System.out.println(garisT);
        // Baris di bawah ini akan dilengkapi oleh modul channel pembayaran dan membership teman
        System.out.printf("  %-45s : (dihitung di modul pembayaran)\n", "Diskon channel pembayaran");
        System.out.printf("  %-45s : (dihitung di modul pembayaran)\n", "Total tagihan setelah diskon & admin");
        System.out.printf("  %-45s : (dihitung di modul membership)\n", "Poin sebelum transaksi");
        System.out.printf("  %-45s : (dihitung di modul membership)\n", "Poin setelah transaksi");
        System.out.println(garis);
        System.out.println(centerText("Terima kasih dan silakan datang kembali!", 97));
        System.out.println(garis);
    }

    /** Cetak satu baris item di kuitansi: Kode|Nama|Hrg|Qty|Subtotal|Pajak%|PajakRp|Total */
    private void cetakBaris(OrderLine ol, String kodeMember) {
        Menu   m        = ol.getMenu();
        int    qty      = ol.getKuantitas();
        double subtotal = m.getHarga() * qty;
        double pajakRp  = TaxCalculator.hitungPajak(m, qty, kodeMember);
        double total    = subtotal + pajakRp;
        String labelPjk = TaxCalculator.getLabelTarif(m, kodeMember);

        // Potong nama jika terlalu panjang
        String nama = m.getNama().length() > 32 ? m.getNama().substring(0, 29) + "..." : m.getNama();

        System.out.printf("| %-4s | %-32s | %8.0f | %3d | %10.2f | %5s | %10.2f | %11.2f |\n",
            m.getKode(), nama, m.getHarga(), qty, subtotal, labelPjk, pajakRp, total);
    }

    // ─── HELPER untuk modul payment/membership ────────────────────────────────
    /** Total tagihan dengan pajak (sebelum diskon channel), dalam IDR. */
    public double hitungTotalTagihan(String kodeMember) {
        double t = 0;
        for (OrderLine ol : listPesanan)
            t += TaxCalculator.hitungTotalDenganPajak(ol.getMenu(), ol.getKuantitas(), kodeMember);
        return t;
    }

    /** Total subtotal tanpa pajak, dalam IDR. */
    public double hitungTotalTanpaPajak() {
        double t = 0;
        for (OrderLine ol : listPesanan) t += ol.getMenu().getHarga() * ol.getKuantitas();
        return t;
    }

    // ─── HELPER SORTING ──────────────────────────────────────────────────────
    // Urut: Makanan→Minuman, lalu kode alfabet (untuk keranjang saat input)
    private ArrayList<OrderLine> getSortedByKode() {
        ArrayList<OrderLine> s = new ArrayList<>(listPesanan);
        Collections.sort(s, (a, b) -> {
            int cmp = jenis(a).compareTo(jenis(b));
            return cmp != 0 ? cmp : a.getMenu().getKode().compareToIgnoreCase(b.getMenu().getKode());
        });
        return s;
    }

    // Urut: Makanan→Minuman, lalu harga ascending (untuk kuitansi)
    private ArrayList<OrderLine> getSortedByHarga() {
        ArrayList<OrderLine> s = new ArrayList<>(listPesanan);
        Collections.sort(s, (a, b) -> {
            int cmp = jenis(a).compareTo(jenis(b));
            return cmp != 0 ? cmp : Double.compare(a.getMenu().getHarga(), b.getMenu().getHarga());
        });
        return s;
    }

    private String jenis(OrderLine ol) {
        return (ol.getMenu() instanceof Makanan) ? "Makanan" : "Minuman";
    }
}