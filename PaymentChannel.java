public interface PaymentChannel {
    String getNamaChannel();
    double hitungTotal(double totalSebelumDiskon);
    boolean proses(double totalSebelumDiskon);
}