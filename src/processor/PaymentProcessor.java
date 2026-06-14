package processor;

import model.*;
import java.util.Scanner;

public class PaymentProcessor {

    public PaymentChannel pilihChannel(Scanner scan) {
        System.out.println("\n=============================================");
        System.out.println("         PILIH CHANNEL PEMBAYARAN           ");
        System.out.println("=============================================");
        System.out.println("  1. Tunai   (tanpa diskon)");
        System.out.println("  2. QRIS    (diskon 5%)");
        System.out.println("  3. eMoney  (diskon 7%, biaya admin Rp 20)");
        System.out.println("=============================================");

        PaymentChannel channel = null;

        while (channel == null) {
            System.out.print("Pilih (1/2/3): ");
            String input = scan.nextLine().trim();

            switch (input) {
                case "1":
                    channel = new CashPayment();
                    break;
                case "2":
                    System.out.print("Masukkan saldo QRIS Anda   : Rp ");
                    double saldoQris = bacaDouble(scan);
                    channel = new QRISPayment(saldoQris);
                    break;
                case "3":
                    System.out.print("Masukkan saldo eMoney Anda : Rp ");
                    double saldoEmoney = bacaDouble(scan);
                    channel = new EMoneyPayment(saldoEmoney);
                    break;
                default:
                    System.out.println("Pilihan tidak valid! Masukkan 1, 2, atau 3.");
            }
        }
        return channel;
    }

    public boolean prosesPembayaran(PaymentChannel channel, double totalAkhir) {
        return channel.proses(totalAkhir);
    }

    public double getTotalSetelahDiskon(PaymentChannel channel, double totalAkhir) {
        return channel.getTotalSetelahDiskon(totalAkhir);
    }

    private double bacaDouble(Scanner scan) {
        while (true) {
            try {
                return Double.parseDouble(scan.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Input tidak valid, masukkan angka: Rp ");
            }
        }
    }
}