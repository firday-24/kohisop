public class CashPayment implements PaymentChannel {

    @Override
    public String getNamaChannel() {
        return "Tunai";
    }

    @Override
    public double hitungTotal(double totalSebelumDiskon) {
        return totalSebelumDiskon;
    }

    @Override
    public boolean proses(double totalSebelumDiskon) {
        double total = hitungTotal(totalSebelumDiskon);
        System.out.println("  Channel      : Tunai");
        System.out.println("  Diskon       : Rp 0.00");
        System.out.printf ("  TOTAL BAYAR  : Rp %.2f%n", total);
        return true;
    }
}