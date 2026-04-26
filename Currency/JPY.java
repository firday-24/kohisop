package Currency;

public class JPY implements PaymentCurrency {
    private static final String NAMA = "Japanese Yen";
    private static final String KODE = "JPY";
    private static final double NILAI_TUKAR_PEMBAGI = 10.0; // 10 JPY = 1 IDR

    @Override
    public String getNamaMataUang() {
        return NAMA;
    }

    @Override
    public String getKodeMataUang() {
        return KODE;
    }

    @Override
    public double konversiDariIDR(double totalIDR) {
        return totalIDR * NILAI_TUKAR_PEMBAGI;
    }

    @Override
    public void tampilkanInformasi() {
        System.out.printf("  Mata Uang: %s (%s)%n", NAMA, KODE);
        System.out.printf("  Nilai Tukar: 10 %s = 1 IDR%n", KODE);
    }
}
