package processor;
import Currency.*;
import java.util.Scanner;

public class CurrencyProcessor {

    public PaymentCurrency pilihMataUang(Scanner scan) {
        System.out.println("\n=============================================");
        System.out.println("      PILIH MATA UANG PEMBAYARAN              ");
        System.out.println("=============================================");
        System.out.println("  1. IDR (Rupiah Indonesia) - Default");
        System.out.println("  2. USD (US Dollar)       - 1 USD  = 15 IDR");
        System.out.println("  3. JPY (Japanese Yen)    - 10 JPY = 1 IDR");
        System.out.println("  4. MYR (Malaysian Ringgit) - 1 MYR = 4 IDR");
        System.out.println("  5. EUR (Euro)            - 1 EUR  = 14 IDR");
        System.out.println("=============================================");

        PaymentCurrency currency = null;

        while (currency == null) {
            System.out.print("Pilih (1/2/3/4/5): ");
            String input = scan.nextLine().trim();

            switch (input) {
                case "1":
                    currency = new IDR();
                    break;
                case "2":
                    currency = new USD();
                    break;
                case "3":
                    currency = new JPY();
                    break;
                case "4":
                    currency = new MYR();
                    break;
                case "5":
                    currency = new EUR();
                    break;
                default:
                    System.out.println("Pilihan tidak valid! Masukkan 1, 2, 3, 4, atau 5.");
            }
        }
        return currency;
    }

    public double konversi(double totalIDR, PaymentCurrency currency) {
        return currency.konversiDariIDR(totalIDR);
    }

    public void tampilkanSemuaMataUang() {
        // Optional: bisa dipanggil jika diperlukan
        System.out.println("\n================== MATA UANG YANG TERSEDIA ==================");
        new IDR().tampilkanInformasi();
        System.out.println();
        new USD().tampilkanInformasi();
        System.out.println();
        new JPY().tampilkanInformasi();
        System.out.println();
        new MYR().tampilkanInformasi();
        System.out.println();
        new EUR().tampilkanInformasi();
        System.out.println("==============================================================");
    }
}