import model.*;
import processor.*;

public class Main {
    public static void main(String[] args) {
        KitchenProcessor kitchen = new KitchenProcessor();
        int customerCount = 0;

        while (true) {  // support multiple customers
            OrderProcessor processor = new OrderProcessor();
            processor.run();
            Order order = processor.getOrderBaru();

            if (!order.getListPesanan().isEmpty()) {
                MembershipReceiptProcessor receipt = new MembershipReceiptProcessor();
                receipt.prosesDanCetakKuitansiLengkap(order);
                kitchen.tambahPesanan(order);
                customerCount++;
            }

            if (customerCount >= 3) {
                kitchen.prosesPesananDapur();
                // Reset untuk melayani pelanggan berikutnya
                kitchen = new KitchenProcessor();
                customerCount = 0;
                System.out.println("\n--- Dapur selesai, siap melayani pelanggan berikutnya ---\n");
            }

            System.out.print("Ada pelanggan lagi? (Y/N): ");
            // ...
        }
    }
}