public interface PaymentChannel {
    String getNamaChannel();
    double hitungTotal(double totalSebelumDiskon);
    double getTotalSetelahDiskon(double totalSebelumDiskon);
    boolean proses(double totalSebelumDiskon);
}