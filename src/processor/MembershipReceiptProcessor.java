package processor;

import model.*;
import java.util.ArrayList;
import java.util.Scanner;

public class MembershipReceiptProcessor {
    private MembershipManager memberManager;
    private Scanner scanner;

    public MembershipReceiptProcessor() {
        this.memberManager = new MembershipManager();
        this.scanner = new Scanner(System.in);
    }

    private String centerText(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        int totalSpaces = width - text.length();
        int leftSpaces = totalSpaces / 2;
        int rightSpaces = totalSpaces - leftSpaces;
        return " ".repeat(leftSpaces) + text + " ".repeat(rightSpaces);
    }

    public void prosesDanCetakKuitansiLengkap(Order order) {
        if (order.getListPesanan().isEmpty()) {
            return;
        }

        System.out.println("\n=========================================================================================");
        System.out.println("                              PROSES PEMBAYARAN & MEMBERSHIP                             ");
        System.out.println("=========================================================================================");
        
        String kodeMemberAktif = null;

        while (true) {
            System.out.print("Apakah pelanggan memiliki kode member? (Y/N): ");
            String punyaMember = scanner.nextLine().trim();

            if (punyaMember.equalsIgnoreCase("Y")) {
                System.out.print("Masukkan Kode Member: ");
                String kodeInput = scanner.nextLine().trim();
                Member m = memberManager.cariMember(kodeInput);
                
                if (m != null) {
                    kodeMemberAktif = m.getKodeMember();
                    System.out.println("=> Member Terverifikasi: " + m.getNamaMember());
                    break;
                } else {
                    System.out.println("kode member tidak ditemukan!");
                }
            } else if (punyaMember.equalsIgnoreCase("N")) {
                System.out.print("Masukkan Nama Pelanggan: ");
                String namaBaru = scanner.nextLine().trim();
                if (!namaBaru.isEmpty()) {
                    Member memberBaru = memberManager.daftarMemberOtomatis(namaBaru);
                    kodeMemberAktif = memberBaru.getKodeMember();
                    System.out.println("\n[SUKSES] Member Berhasil Dibuat Otomatis!");
                    System.out.println(" Nama Pelanggan : " + memberBaru.getNamaMember());
                    System.out.println(" Kode Member    : " + memberBaru.getKodeMember());
                }
                break;
            } else {
                System.out.println("Input tidak valid! Masukkan Y atau N.");
            }
        }

        // KUITANSI 
        int lebarLayout = 93;
        System.out.println("\n==========================================================================================================");
        System.out.println(centerText("ANDA SELESAI MEMESAN. BERIKUT PESANAN FINAL ANDA:", lebarLayout));
        System.out.println("==========================================================================================================");

        Member member = (kodeMemberAktif != null) ? memberManager.cariMember(kodeMemberAktif) : null;
        if (member != null) {
            System.out.println(" Kode Member : " + member.getKodeMember());
            System.out.println(" Nama Member : " + member.getNamaMember());
            System.out.println("----------------------------------------------------------------------------------------------------------");
        }

        double subtotalMakananMurni = 0;
        double subtotalMinumanMurni = 0;
        double pajakMakananTotal = 0;
        double pajakMinumanTotal = 0;
        int totalQtyMakanan = 0;
        int totalQtyMinuman = 0;

        ArrayList<OrderLine> listMakanan = new ArrayList<>();
        ArrayList<OrderLine> listMinuman = new ArrayList<>();

        for (OrderLine ol : order.getListPesanan()) {
            if (ol.getMenu() instanceof Makanan) {
                listMakanan.add(ol);
                subtotalMakananMurni += (ol.getMenu().getHarga() * ol.getKuantitas());
                pajakMakananTotal += TaxCalculator.hitungPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                totalQtyMakanan += ol.getKuantitas();
            } else if (ol.getMenu() instanceof Minuman) {
                listMinuman.add(ol);
                subtotalMinumanMurni += (ol.getMenu().getHarga() * ol.getKuantitas());
                pajakMinumanTotal += TaxCalculator.hitungPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                totalQtyMinuman += ol.getKuantitas();
            }
        }

        // Definisi Lebar Ukuran Kolom Tabel
        int lK = 5;   // Kode
        int lN = 33;  // Nama Menu
        int lQ = 5;   // Qty
        int lH = 11;  // Harga Porsi
        int lP1 = 9;  // Pajak (%)   
        int lP2 = 11; // Pajak (Rp)  
        int lT = 11;  // Total Item

        System.out.printf("| %s | %s | %s | %s | %s | %s | %s |\n", 
            centerText("Kode", lK), centerText("Menu Makanan", lN), centerText("Qty", lQ), 
            centerText("Harga (Rp)", lH), centerText("Pajak (%)", lP1), centerText("Pajak (Rp)", lP2), centerText("Total (Rp)", lT));
        System.out.println("----------------------------------------------------------------------------------------------------------");
        if (listMakanan.isEmpty()) {
            System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                centerText("-", lK), "Tidak ada pesanan makanan", centerText("-", lQ), centerText("-", lH), centerText("-", lP1), centerText("-", lP2), centerText("-", lT));
        } else {
            for (OrderLine ol : listMakanan) {
                double pajakItem = TaxCalculator.hitungPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                double totalItem = TaxCalculator.hitungTotalDenganPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                String ratePajak = TaxCalculator.getLabelTarif(ol.getMenu(), kodeMemberAktif);

                System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                    centerText(ol.getMenu().getKode(), lK), 
                    ol.getMenu().getNama(), 
                    centerText(String.valueOf(ol.getKuantitas()), lQ),
                    centerText(String.format("%.0f", ol.getMenu().getHarga()), lH),
                    centerText(ratePajak, lP1),
                    centerText(String.format("%.1f", pajakItem), lP2),
                    centerText(String.format("%.1f", totalItem), lT));
            }
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %s | %s | %s | %s | %s | %s | %s |\n", 
            centerText("Kode", lK), centerText("Menu Minuman", lN), centerText("Qty", lQ), 
            centerText("Harga (Rp)", lH), centerText("Pajak (%)", lP1), centerText("Pajak (Rp)", lP2), centerText("Total (Rp)", lT));
        System.out.println("----------------------------------------------------------------------------------------------------------");
        if (listMinuman.isEmpty()) {
            System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                centerText("-", lK), "Tidak ada pesanan minuman", centerText("-", lQ), centerText("-", lH), centerText("-", lP1), centerText("-", lP2), centerText("-", lT));
        } else {
            for (OrderLine ol : listMinuman) {
                double pajakItem = TaxCalculator.hitungPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                double totalItem = TaxCalculator.hitungTotalDenganPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                String ratePajak = TaxCalculator.getLabelTarif(ol.getMenu(), kodeMemberAktif);

                System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                    centerText(ol.getMenu().getKode(), lK), 
                    ol.getMenu().getNama(), 
                    centerText(String.valueOf(ol.getKuantitas()), lQ),
                    centerText(String.format("%.0f", ol.getMenu().getHarga()), lH),
                    centerText(ratePajak, lP1),
                    centerText(String.format("%.1f", pajakItem), lP2),
                    centerText(String.format("%.1f", totalItem), lT));
                    System.out.println("----------------------------------------------------------------------------------------------------------");
            }
        }

        // BLOK INFORMASI PEMBAYARAN & MATA UANG
        double totalBelanjaSebelumPajak = order.hitungTotalTanpaPajak();
        System.out.println("INFORMASI PEMBAYARAN & MATA UANG (Mendatang):");
        System.out.printf(" - Total tagihan sebelum penyesuaian    : %.0f\n", totalBelanjaSebelumPajak);
        System.out.println(" - Mata uang pembayaran                 : (Akan dihitung di Modul Mata Uang)");
        System.out.println(" - Diskon channel pembayaran            : (Akan dihitung di Modul Channel Pembayaran)");
        System.out.println(" - Biaya admin channel pembayaran       : (Akan dihitung di Modul Channel Pembayaran)");
        System.out.println("----------------------------------------------------------------------------------------------------------");

        //TOTAL TAGIHAN FINAL AKHIR
        double totalTagihanReal = (subtotalMakananMurni + pajakMakananTotal) + (subtotalMinumanMurni + pajakMinumanTotal);
        System.out.printf("TOTAL TAGIHAN AKHIR                   : %.0f\n", totalTagihanReal);
        System.out.println("----------------------------------------------------------------------------------------------------------");

        // REKAPITULASI POIN MEMBERSHIP
        if (member != null) {
            int poinSebelum = member.getPoin();
            int dapatPoin = memberManager.hitungPoinBaru(totalBelanjaSebelumPajak, kodeMemberAktif);
            memberManager.tambahPoinMember(kodeMemberAktif, dapatPoin);

            System.out.println("REKAPITULASI POIN MEMBERSHIP:");
            System.out.printf(" - Jumlah poin sebelum transaksi        : %d poin\n", poinSebelum);
            System.out.printf(" - Perolehan poin dari transaksi ini    : %d poin %s\n", dapatPoin,
                (kodeMemberAktif.toUpperCase().contains("A") ? "(DIGANDAKAN karena kode mengandung 'A')" : ""));
            System.out.printf(" - Jumlah poin setelah transaksi        : %d poin\n", member.getPoin());
            System.out.println("----------------------------------------------------------------------------------------------------------");
        }

        System.out.println(centerText("terima kasih dan silakan datang kembali", lebarLayout));
        System.out.println("==========================================================================================================");
    }
}