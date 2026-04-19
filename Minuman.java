public class Minuman extends Menu {

    public Minuman(String kode, String nama, int harga) {
        super(kode, nama, harga);
    }

    @Override
    public int maksimalPesan() {
        return 3;
    }

    /**
     * Aturan pajak minuman:
     *  - harga < 50        -> 0%
     *  - 50 <= harga <= 55 -> 8%
     *  - harga > 55        -> 11%
     */
    @Override
    public double hitungPajak() {
        if (harga < 50) {
            return 0.0;
        } else if (harga <= 55) {
            return harga * 0.08;
        } else {
            return harga * 0.11;
        }
    }
}
