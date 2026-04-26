import Currency.EUR;
import Currency.JPY;
import Currency.MYR;
import Currency.PaymentCurrency;
import Currency.USD;
import java.util.Scanner;

public class CurrencyProcessor {
    public double jalankan(double totalIDR, Scanner scan) {
        System.out.println("\n=============================================");
        System.out.println("      PILIH MATA UANG PEMBAYARAN              ");
        System.out.println("=============================================");
        System.out.println("  1. USD (US Dollar)       - 1 USD  = 15 IDR");
        System.out.println("  2. JPY (Japanese Yen)    - 10 JPY = 1 IDR");
        System.out.println("  3. MYR (Malaysian Ringgit) - 1 MYR = 4 IDR");
        System.out.println("  4. EUR (Euro)            - 1 EUR  = 14 IDR");
        System.out.println("=============================================");

        PaymentCurrency currency = null;

        while (currency == null) {
            System.out.print("Pilih (1/2/3/4): ");
            String input = scan.nextLine().trim();

            switch (input) {
                case "1":
                    currency = new USD();
                    break;
                case "2":
                    currency = new JPY();
                    break;
                case "3":
                    currency = new MYR();
                    break;
                case "4":
                    currency = new EUR();
                    break;
                default:
                    System.out.println("Pilihan tidak valid! Masukkan 1, 2, 3, atau 4.");
            }
        }

        System.out.println("\n---------------------------------------------");
        System.out.println("           RINGKASAN KONVERSI                ");
        System.out.println("---------------------------------------------");
        currency.tampilkanInformasi();
        System.out.printf("  Total dalam IDR: Rp %.2f%n", totalIDR);

        double totalTerkonversi = currency.konversiDariIDR(totalIDR);
        System.out.printf("  Total dalam %s: %.2f %s%n",
                currency.getNamaMataUang(),
                totalTerkonversi,
                currency.getKodeMataUang());
        System.out.println("---------------------------------------------");

        return totalTerkonversi;
    }

    public void tampilkanSemuaMataUang() {
        System.out.println("\n================== MATA UANG YANG TERSEDIA ==================");
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
