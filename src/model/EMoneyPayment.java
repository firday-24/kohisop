package model;

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
        double total = hitungTotal(totalSebelumDiskon);

        if (saldo < total) {
            System.out.println("\n  [GAGAL] Saldo eMoney tidak mencukupi!");
            System.out.printf ("          Kekurangan: Rp %.2f%n", total - saldo);
            return false;
        }

        saldo -= total;
        System.out.println("  [BERHASIL] Pembayaran eMoney diterima.");
        return true;
    }
}