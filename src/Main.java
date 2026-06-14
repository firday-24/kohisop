import processor.*;
import model.*;

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
                break; // atau lanjut sesuai kebutuhan
            }

            System.out.print("Ada pelanggan lagi? (Y/N): ");
            // ...
        }
    }
}