package Currency;

public class IDR implements PaymentCurrency {
    private static final String NAMA = "Rupiah Indonesia";
    private static final String KODE = "IDR";
    private static final double NILAI_TUKAR = 1.0; 

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
