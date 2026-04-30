public class EMoneyPayment implements PaymentChannel {
    private static final double DISKON    = 0.07;
    private static final double BIAYA_ADM = 20.0;
    private double saldo;

    public EMoneyPayment(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String getNamaChannel() {
        return "eMoney";
    }

    @Override
    public double hitungTotal(double totalSebelumDiskon) {
        return (totalSebelumDiskon * (1 - DISKON)) + BIAYA_ADM;
    }

    @Override
    public double getTotalSetelahDiskon(double totalSebelumDiskon) {
        return hitungTotal(totalSebelumDiskon);
    }

    @Override
    public boolean proses(double totalSebelumDiskon) {
        double diskon = totalSebelumDiskon * DISKON;
        double total  = hitungTotal(totalSebelumDiskon);

        System.out.println("  Channel      : eMoney");
        System.out.printf ("  Diskon 7%%    : Rp %.2f%n", diskon);
        System.out.printf ("  Biaya Admin  : Rp %.2f%n", BIAYA_ADM);
        System.out.printf ("  TOTAL BAYAR  : Rp %.2f%n", total);
        System.out.printf ("  Saldo eMoney : Rp %.2f%n", saldo);

        if (saldo < total) {
            System.out.println("\n  [GAGAL] Saldo eMoney tidak mencukupi!");
            System.out.printf ("          Kekurangan: Rp %.2f%n", total - saldo);
            return false;
        }

        saldo -= total;
        System.out.printf ("  Sisa Saldo   : Rp %.2f%n", saldo);
        System.out.println("  [BERHASIL] Pembayaran eMoney diterima.");
        return true;
    }
}