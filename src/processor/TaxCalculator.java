package processor;

import model.*;

/**
 * TaxCalculator - Menghitung pajak berdasarkan jenis dan harga menu.
 *
 * Aturan Pajak:
 * Minuman:
 * - Harga < 50            → bebas pajak (0%)
 * - Harga >= 50 dan <= 55 → pajak 8%
 * - Harga > 55            → pajak 11%
 * Makanan:
 * - Harga > 50            → pajak 8%
 * - Harga <= 50           → pajak 11%
 *
 * Pengecualian:
 * - Member yang kode-nya mengandung karakter 'A' → bebas pajak untuk seluruh pesanan.
 */
public class TaxCalculator {

    // ─────────────────────────────────────────────────────────────
    //  KONSTANTA TARIF PAJAK
    // ─────────────────────────────────────────────────────────────
    public static final double TARIF_RENDAH  = 0.08;  // 8%
    public static final double TARIF_TINGGI  = 0.11;  // 11%
    public static final double BEBAS_PAJAK   = 0.00;  // 0%

    // ─────────────────────────────────────────────────────────────
    //  UTILITAS CEK PEMBEBASAN PAJAK MEMBER
    // ─────────────────────────────────────────────────────────────

    /**
     * Mengembalikan true jika member memiliki karakter 'A' di kode-nya,
     * yang berarti seluruh pesanannya bebas pajak.
     *
     * @param kodeMember kode 6-karakter member (misal "A23FB9"); null atau "" jika bukan member
     */
    public static boolean adalahmemberBebasPajak(String kodeMember) {
        if (kodeMember == null || kodeMember.isEmpty()) return false;
        return kodeMember.toUpperCase().contains("A");
    }

    // ─────────────────────────────────────────────────────────────
    //  KALKULASI TARIF
    // ─────────────────────────────────────────────────────────────

    /**
     * Mengembalikan tarif pajak (dalam desimal) untuk satu item menu.
     * Jika member bebas pajak, langsung kembalikan 0.
     *
     * @param menu        objek Menu (Makanan / Minuman)
     * @param kodeMember  kode member; null / "" jika bukan member
     */
    public static double getTarif(Menu menu, String kodeMember) {
        // Pengecualian: member dengan 'A' di kode → bebas pajak
        if (adalahmemberBebasPajak(kodeMember)) return BEBAS_PAJAK;

        double harga = menu.getHarga();

        if (menu instanceof Minuman) {
            if (harga < 50)  return BEBAS_PAJAK;
            if (harga <= 55) return TARIF_RENDAH;   // >= 50 && <= 55 → 8%
            return TARIF_TINGGI;                     // > 55 → 11%
        }

        if (menu instanceof Makanan) {
            if (harga > 50) return TARIF_RENDAH;    // > 50 → 8%
            return TARIF_TINGGI;                     // <= 50 → 11%
        }

        return BEBAS_PAJAK; // fallback (tidak seharusnya terjadi)
    }

    /**
     * Menghitung pajak (dalam Rupiah) untuk satu item menu dengan kuantitas tertentu.
     *
     * @param menu        objek Menu
     * @param kuantitas   jumlah porsi yang dipesan
     * @param kodeMember  kode member; null / "" jika bukan member
     */
    public static double hitungPajak(Menu menu, int kuantitas, String kodeMember) {
        double tarif = getTarif(menu, kodeMember);
        return menu.getHarga() * kuantitas * tarif;
    }

    /**
     * Menghitung total harga + pajak untuk satu item menu.
     */
    public static double hitungTotalDenganPajak(Menu menu, int kuantitas, String kodeMember) {
        double subtotal = menu.getHarga() * kuantitas;
        return subtotal + hitungPajak(menu, kuantitas, kodeMember);
    }

    // ─────────────────────────────────────────────────────────────
    //  LABEL TARIF (UNTUK TAMPILAN)
    // ─────────────────────────────────────────────────────────────

    /**
     * Mengembalikan label tarif pajak sebagai string yang siap ditampilkan,
     * misal "0%", "8%", atau "11%".
     */
    public static String getLabelTarif(Menu menu, String kodeMember) {
        double tarif = getTarif(menu, kodeMember);
        if (tarif == BEBAS_PAJAK)  return "0%";
        if (tarif == TARIF_RENDAH) return "8%";
        return "11%";
    }

    // ─────────────────────────────────────────────────────────────
    //  METHOD HITUNG TOTAL KESELURUHAN
    // ─────────────────────────────────────────────────────────────
    
    /**
     * Menghitung akumulasi total seluruh tagihan akhir (Harga Item + Pajak) 
     * dari sebuah objek Order agar kelas Order bersih dari unsur perpajakan.
     */
    public static double hitungTotalOrderDenganPajak(Order order, String kodeMember) {
        double totalBelanjaAkhir = 0;
        for (OrderLine ol : order.getListPesanan()) {
            totalBelanjaAkhir += hitungTotalDenganPajak(ol.getMenu(), ol.getKuantitas(), kodeMember);
        }
        return totalBelanjaAkhir;
    }
}
