public class Makanan extends Menu {

    public Makanan(String kode, String nama, int harga) {
        super(kode, nama, harga);
    }

    @Override
    public int maksimalPesan() {
        return 2;
    }

    /**
     * Aturan pajak makanan:
     *  - harga > 50  -> 8%
     *  - harga <= 50 -> 11%
     */
    @Override
    public double hitungPajak() {
        if (harga > 50) {
            return harga * 0.08;
        } else {
            return harga * 0.11;
        }
    }
}
