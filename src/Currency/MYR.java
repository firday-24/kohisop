package Currency;

public class MYR implements PaymentCurrency {
    private static final String NAMA = "Malaysian Ringgit";
    private static final String KODE = "MYR";
    private static final double NILAI_TUKAR = 4.0; // 1 MYR = 4 IDR

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
