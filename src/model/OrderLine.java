package model;

// Representasi satu baris item data di dalam struktur tabel belanja (Kombinasi Objek Menu + Jumlah Belinya)
public class OrderLine {
    private Menu menu;
    private int kuantitas;

    // Constructor pembentuk baris item pesanan
    public OrderLine(Menu menu, int kuantitas) {
        this.menu = menu;
        this.kuantitas = kuantitas;
    }

    // Method Getter dan Setter penunjang manipulasi data pesanan
    public Menu getMenu() { return menu; }
    public int getKuantitas() { return kuantitas; }
    public void setKuantitas(int kuantitas) { this.kuantitas = kuantitas; }
}