public class QRISPayment implements PaymentChannel {
    private static final double DISKON = 0.05;
    private double saldo;

    public QRISPayment(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String getNamaChannel() {
        return "QRIS";
    }

    @Override
    public double hitungTotal(double totalSebelumDiskon) {
        return totalSebelumDiskon * (1 - DISKON);
    }

    @Override
    public boolean proses(double totalSebelumDiskon) {
        double diskon = totalSebelumDiskon * DISKON;
        double total  = hitungTotal(totalSebelumDiskon);

        System.out.println("  Channel      : QRIS");
        System.out.printf ("  Diskon 5%%    : Rp %.2f%n", diskon);
        System.out.printf ("  TOTAL BAYAR  : Rp %.2f%n", total);
        System.out.printf ("  Saldo QRIS   : Rp %.2f%n", saldo);

        if (saldo < total) {
            System.out.println("\n  [GAGAL] Saldo QRIS tidak mencukupi!");
            System.out.printf ("          Kekurangan: Rp %.2f%n", total - saldo);
            return false;
        }

        saldo -= total;
        System.out.printf ("  Sisa Saldo   : Rp %.2f%n", saldo);
        System.out.println("  [BERHASIL] Pembayaran QRIS diterima.");
        return true;
    }
}