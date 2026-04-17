public class Makanan extends Menu {
    
    public Makanan(String kode, String nama, int harga) {
        super(kode, nama, harga);
    }

    @Override
    public int maksimalPesan(){
        return 2;
    }
}