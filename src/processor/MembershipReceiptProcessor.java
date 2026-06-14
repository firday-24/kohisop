package processor;

import model.*;
import Currency.PaymentCurrency;
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

        // === Membership ===
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

        double totalBelanjaSebelumPajak = order.hitungTotalTanpaPajak();
        double totalDenganPajak = TaxCalculator.hitungTotalOrderDenganPajak(order, kodeMemberAktif);

        double potonganPoin = 0;
        int poinDigunakan = 0;
        Member member = (kodeMemberAktif != null) ? memberManager.cariMember(kodeMemberAktif) : null;
        boolean lunasDenganPoin = false;

        // === LOGIKA PEMOTONGAN POIN MEMBERSHIP (Partial) ===
        if (member != null && member.getPoin() > 0) {
            int poinTersedia = member.getPoin();
            double maksPotongan = poinTersedia * 2.0; // 1 poin = Rp 2

            System.out.printf("\nPoin membership tersedia: %d poin (maksimal potongan Rp %.0f)\n", poinTersedia, maksPotongan);
            System.out.print("Gunakan poin untuk potongan? (Y/N): ");
            String gunakanPoin = scanner.nextLine().trim();

            if (gunakanPoin.equalsIgnoreCase("Y")) {
                potonganPoin = Math.min(maksPotongan, totalDenganPajak);
                poinDigunakan = (int) Math.ceil(potonganPoin / 2.0);

                if (potonganPoin > 0) {
                    memberManager.kurangiPoinMember(kodeMemberAktif, poinDigunakan);
                    System.out.printf("✓ Potongan poin berhasil: Rp %.0f (menggunakan %d poin)\n", potonganPoin, poinDigunakan);
                    
                    if (potonganPoin >= totalDenganPajak) {
                        lunasDenganPoin = true;
                    }
                }
            }
        }

        double totalSetelahPoin = totalDenganPajak - potonganPoin;

        // Variabel untuk channel dan currency
        PaymentChannel channel = null;
        PaymentCurrency currency = null;
        double totalSetelahDiskon = totalSetelahPoin;
        double totalFinal = totalSetelahPoin;

        // === Jika belum lunas dengan poin, baru pilih channel dan mata uang ===
        if (!lunasDenganPoin && totalSetelahPoin > 0) {
            // === Pilih Channel & Proses Pembayaran ===
            PaymentProcessor paymentProc = new PaymentProcessor();
            channel = paymentProc.pilihChannel(scanner);
            boolean berhasil = paymentProc.prosesPembayaran(channel, totalSetelahPoin);

            if (!berhasil) {
                System.out.print("\nIngin mencoba channel lain? (Y/N): ");
                if (scanner.nextLine().trim().equalsIgnoreCase("Y")) {
                    prosesDanCetakKuitansiLengkap(order); // retry
                    return;
                } else {
                    System.out.println("Transaksi dibatalkan.");
                    return;
                }
            }

            totalSetelahDiskon = paymentProc.getTotalSetelahDiskon(channel, totalSetelahPoin);

            // === Pilih Mata Uang ===
            CurrencyProcessor currencyProc = new CurrencyProcessor();
            currency = currencyProc.pilihMataUang(scanner);
            totalFinal = currencyProc.konversi(totalSetelahDiskon, currency);
        }

        // === CETAK KUITANSI LENGKAP ===
        int lebarLayout = 100;
        System.out.println("\n===========================================================================================================");
        System.out.println(centerText("KUITANSI BELANJA KOHISOP", lebarLayout));
        System.out.println("===========================================================================================================");

        if (member != null) {
            System.out.println(" Kode Member : " + member.getKodeMember());
            System.out.println(" Nama Member : " + member.getNamaMember());
        }

        // Tabel Makanan & Minuman
        ArrayList<OrderLine> listMakanan = new ArrayList<>();
        ArrayList<OrderLine> listMinuman = new ArrayList<>();

        for (OrderLine ol : order.getListPesanan()) {
            if (ol.getMenu() instanceof Makanan) listMakanan.add(ol);
            else if (ol.getMenu() instanceof Minuman) listMinuman.add(ol);
        }

        int lK = 5, lN = 33, lQ = 5, lH = 11, lP1 = 9, lP2 = 11, lT = 11;

        printTabelMakanan(order, kodeMemberAktif, listMakanan, lK, lN, lQ, lH, lP1, lP2, lT);
        printTabelMinuman(order, kodeMemberAktif, listMinuman, lK, lN, lQ, lH, lP1, lP2, lT);

        System.out.printf("TOTAL TAGIHAN DENGAN PAJAK         : Rp %.0f%n", totalDenganPajak);
        
        if (potonganPoin > 0) {
            System.out.printf("POTONGAN POIN MEMBERSHIP           : Rp %.0f%n", potonganPoin);
        }
        System.out.printf("TOTAL SETELAH POTONGAN POIN        : Rp %.0f%n", totalSetelahPoin);

        if (lunasDenganPoin) {
            System.out.println("\nTRANSAKSI LUNAS MENGGUNAKAN POIN MEMBERSHIP");
        } else {
            // RINGKASAN PEMBAYARAN dari channel pembayaran
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.println("\nRINGKASAN PEMBAYARAN:");
            System.out.println("  Channel      : " + channel.getNamaChannel());
            if (channel instanceof QRISPayment) {
                System.out.printf("  Diskon 5%%    : Rp %.2f%n", totalSetelahPoin * 0.05);
            } else if (channel instanceof EMoneyPayment) {
                System.out.printf("  Diskon 7%%    : Rp %.2f%n", totalSetelahPoin * 0.07);
                System.out.println("  Biaya Admin  : Rp 20.00");
            }
            System.out.printf("  TOTAL SETELAH DISKON & ADMIN : Rp %.2f%n", totalSetelahDiskon);

            // RINGKASAN KONVERSI dari mata uang
            System.out.println("-----------------------------------------------------------------------------------------------------------");
            System.out.println("\nRINGKASAN KONVERSI MATA UANG:");
            currency.tampilkanInformasi();
            System.out.printf("  Total sebelum konversi : Rp %.2f%n", totalSetelahDiskon);
            System.out.printf("  Total akhir            : %.2f %s%n", totalFinal, currency.getKodeMataUang());
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        // REKAPITULASI POIN
        if (member != null) {
            int poinSekarang = member.getPoin();
            int dapatPoinBaru = memberManager.hitungPoinBaru(totalBelanjaSebelumPajak, kodeMemberAktif);
            memberManager.tambahPoinMember(kodeMemberAktif, dapatPoinBaru);

            System.out.println("\nREKAPITULASI POIN MEMBERSHIP:");
            System.out.printf(" - Poin sebelum transaksi          : %d poin\n", (member.getPoin() + poinDigunakan) - dapatPoinBaru); // JANGAN DIGANTII KARENA MEMBERSHIP MANAGER SUDAH DIUPDATE
            if (poinDigunakan > 0) {
                System.out.printf(" - Poin digunakan                  : %d poin (Rp %.0f)\n", poinDigunakan, potonganPoin);
            }
            System.out.printf(" - Poin diperoleh transaksi ini    : %d poin %s\n", dapatPoinBaru,
                (kodeMemberAktif.toUpperCase().contains("A") ? "(DIGANDAKAN karena kode member A)" : ""));
            System.out.printf(" - Poin akhir                      : %d poin\n", poinSekarang + dapatPoinBaru);
            System.out.println("-----------------------------------------------------------------------------------------------------------");
        }

        System.out.println(centerText("terima kasih dan silakan datang kembali", lebarLayout));
        System.out.println("===========================================================================================================");
    }

    private void printTabelMakanan(Order order, String kodeMemberAktif, ArrayList<OrderLine> listMakanan, 
                                   int lK, int lN, int lQ, int lH, int lP1, int lP2, int lT) {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %s | %s | %s | %s | %s | %s | %s |\n", 
            centerText("Kode", lK), centerText("Menu Makanan", lN), centerText("Qty", lQ), 
            centerText("Harga (Rp)", lH), centerText("Pajak (%)", lP1), centerText("Pajak (Rp)", lP2), centerText("Total (Rp)", lT));
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        if (listMakanan.isEmpty()) {
            System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                centerText("-", lK), "Tidak ada pesanan makanan", centerText("-", lQ), centerText("-", lH), 
                centerText("-", lP1), centerText("-", lP2), centerText("-", lT));
        } else {
            for (OrderLine ol : listMakanan) {
                double pajakItem = TaxCalculator.hitungPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                double totalItem = TaxCalculator.hitungTotalDenganPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                String ratePajak = TaxCalculator.getLabelTarif(ol.getMenu(), kodeMemberAktif);

                System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                    centerText(ol.getMenu().getKode(), lK), ol.getMenu().getNama(), 
                    centerText(String.valueOf(ol.getKuantitas()), lQ),
                    centerText(String.format("%.0f", ol.getMenu().getHarga()), lH), 
                    centerText(ratePajak, lP1),
                    centerText(String.format("%.1f", pajakItem), lP2), 
                    centerText(String.format("%.1f", totalItem), lT));
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }

    private void printTabelMinuman(Order order, String kodeMemberAktif, ArrayList<OrderLine> listMinuman, 
                                   int lK, int lN, int lQ, int lH, int lP1, int lP2, int lT) {
        System.out.println("-----------------------------------------------------------------------------------------------------------");
        System.out.printf("| %s | %s | %s | %s | %s | %s | %s |\n", 
            centerText("Kode", lK), centerText("Menu Minuman", lN), centerText("Qty", lQ), 
            centerText("Harga (Rp)", lH), centerText("Pajak (%)", lP1), centerText("Pajak (Rp)", lP2), centerText("Total (Rp)", lT));
        System.out.println("-----------------------------------------------------------------------------------------------------------");

        if (listMinuman.isEmpty()) {
            System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                centerText("-", lK), "Tidak ada pesanan minuman", centerText("-", lQ), centerText("-", lH), 
                centerText("-", lP1), centerText("-", lP2), centerText("-", lT));
        } else {
            for (OrderLine ol : listMinuman) {
                double pajakItem = TaxCalculator.hitungPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                double totalItem = TaxCalculator.hitungTotalDenganPajak(ol.getMenu(), ol.getKuantitas(), kodeMemberAktif);
                String ratePajak = TaxCalculator.getLabelTarif(ol.getMenu(), kodeMemberAktif);

                System.out.printf("| %s | %-33s | %s | %s | %s | %s | %s |\n", 
                    centerText(ol.getMenu().getKode(), lK), ol.getMenu().getNama(), 
                    centerText(String.valueOf(ol.getKuantitas()), lQ),
                    centerText(String.format("%.0f", ol.getMenu().getHarga()), lH), 
                    centerText(ratePajak, lP1),
                    centerText(String.format("%.1f", pajakItem), lP2), 
                    centerText(String.format("%.1f", totalItem), lT));
            }
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------");
    }
}