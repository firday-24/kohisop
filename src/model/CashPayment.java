package model;

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
    public double getTotalSetelahDiskon(double totalSebelumDiskon) {
        return hitungTotal(totalSebelumDiskon);
    }

    @Override
    public boolean proses(double totalSebelumDiskon) {
        // Tidak perlu menghitung ulang karena tidak ada diskon/biaya
        // Summary pembayaran dipindah ke kuitansi
        return true;
    }
}