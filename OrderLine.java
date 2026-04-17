public class OrderLine {
    private Menu menu;       // Objek menu yang dibeli
    private int kuantitas;   // Jumlah yang dibeli

    public OrderLine(Menu menu, int kuantitas) {
        this.menu = menu;
        this.kuantitas = kuantitas;
    }

    // Getter dan Setter
    public Menu getMenu() {
        return menu; 
    }
    public int getKuantitas() { 
        return kuantitas; 
    }
    public void setKuantitas(int kuantitas) { 
        this.kuantitas = kuantitas; 
    }

}
