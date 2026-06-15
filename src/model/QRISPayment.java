package model;

public class QRISPayment implements PaymentChannel {
    private static final double DISKON = 0.05;
    private double saldo;

    public QRISPayment(double saldo) {
        this.saldo = saldo;
    }

    @Override
    public String getNamaChannel() { return "QRIS"; }

    @Override
    public double hitungTotal(double totalSebelumDiskon) {
        return totalSebelumDiskon * (1 - DISKON);
    }

    @Override
    public double getTotalSetelahDiskon(double totalSebelumDiskon) {
        return hitungTotal(totalSebelumDiskon);
    }

    @Override
    public boolean proses(double totalSebelumDiskon) {
        double total = hitungTotal(totalSebelumDiskon);

        if (saldo < total) {
            System.out.println("\n  [GAGAL] Saldo QRIS tidak mencukupi!");
            System.out.printf ("          Kekurangan: Rp %.2f%n", total - saldo);
            return false;
        }

        saldo -= total;
        System.out.println("  [BERHASIL] Pembayaran QRIS diterima.");
        return true;
    }
}