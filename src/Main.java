import processor.OrderProcessor;
import processor.MembershipReceiptProcessor;

public class Main {
    public static void main(String[] args) throws Exception {
        OrderProcessor processor = new OrderProcessor();
        processor.run();
        MembershipReceiptProcessor membershipModule = new MembershipReceiptProcessor();
        membershipModule.prosesDanCetakKuitansiLengkap(processor.getOrderBaru());
    }
}