package Currency;

public interface PaymentCurrency {
    String getNamaMataUang();
    String getKodeMataUang();
    double konversiDariIDR(double totalIDR);
    void tampilkanInformasi();
}
