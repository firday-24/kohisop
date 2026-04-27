import java.util.LinkedList;

public abstract class Menu implements ICheck {
    protected String kode, nama;
    protected int harga;

    public Menu(String kode, String nama, int harga) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
    }

    public String getKode() { 
        return kode; 
    }
    
    public String getNama() { 
        return nama; 
    }
    
    public int getHarga() { 
        return harga; 
    }

    @Override
    public boolean kuantitasValid(int kuantitas) {
        return kuantitas >= 0 && kuantitas <= maksimalPesan();
    }

    @Override
    public abstract int maksimalPesan();

    // Method pajak — tiap subclass punya aturan sendiri
    public abstract double hitungPajak();

    //LinkedList untuk menyimpan menu
    public static LinkedList<Menu> generateDaftarMenu() {
        LinkedList<Menu> list = new LinkedList<>();
        // Minuman
        list.add(new Minuman("A1", "Caffe Latte", 46));
        list.add(new Minuman("A2", "Cappuccino", 46));
        list.add(new Minuman("E1", "Caffe Americano", 37));
        list.add(new Minuman("E2", "Caffe Mocha", 55));
        list.add(new Minuman("E3", "Caramel Macchiato", 59));
        list.add(new Minuman("E4", "Asian Dolce Latte", 55));
        list.add(new Minuman("E5", "Double Shots Iced Shaken Espresso", 50));
        list.add(new Minuman("B1", "Freshly Brewed Coffee", 23));
        list.add(new Minuman("B2", "Vanilla Sweet Cream Cold Brew", 50));
        list.add(new Minuman("B3", "Cold Brew", 44));
        // Makanan
        list.add(new Makanan("M1", "Petemania Pizza", 112));
        list.add(new Makanan("M2", "Mie Rebus Super Mario", 35));
        list.add(new Makanan("M3", "Ayan Bakar Goreng Rebus Spesial", 72));
        list.add(new Makanan("M4", "Soto Kambing Iga Guling", 124));
        list.add(new Makanan("S1", "Singkong Bakar A La Carte", 37));
        list.add(new Makanan("S2", "Ubi Cilembu Bakar Arang", 58));
        return list;
    }

    public static void listMenu (LinkedList<Menu> list) {
        System.out.println("---------------------------------------------------------");
        System.out.printf("| %-3s |           %-23s | %-9s |\n", "Kode", "Menu Minuman", "Harga(Rp.)");
        System.out.println("---------------------------------------------------------");
        for (Menu m : list) {
          if (m instanceof Minuman) {
            System.out.printf("|  %-3s | %-33s |     %-6d |\n", m.kode, m.nama, m.harga);
          }
        }
        System.out.println("---------------------------------------------------------");
        System.out.printf("| %-3s |           %-23s | %-9s |\n", "Kode", "Menu Makanan", "Harga(Rp.)");
        System.out.println("---------------------------------------------------------");
        for (Menu m : list) {
          if (m instanceof Makanan) {
            System.out.printf("|  %-3s | %-33s |     %-6d |\n", m.kode, m.nama, m.harga);
          }
        }
        System.out.println("---------------------------------------------------------");
    }
}
