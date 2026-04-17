public class Minuman extends Menu {
    
    public Minuman(String kode, String nama, int harga){
        super(kode, nama, harga);
    }

    @Override
    public int maksimalPesan(){
        return 3;
    }
}