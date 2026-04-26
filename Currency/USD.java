package Currency;

public class USD implements PaymentCurrency {
    private static final String NAMA = "US Dollar";
    private static final String KODE = "USD";
    private static final double NILAI_TUKAR = 15.0; // 1 USD = 15 IDR

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
        return totalIDR / NILAI_TUKAR;
    }

    @Override
    public void tampilkanInformasi() {
        System.out.printf("  Mata Uang: %s (%s)%n", NAMA, KODE);
        System.out.printf("  Nilai Tukar: 1 %s = %.0f IDR%n", KODE, NILAI_TUKAR);
    }
}
