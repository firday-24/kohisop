public class OrderLine {
    private final Menu menu;
    private int kuantitas;

    public OrderLine(Menu menu, int kuantitas) {
        this.menu = menu;
        this.kuantitas = kuantitas;
    }

    public Menu getMenu() { 
        return menu; 
    }
    
    public int getKuantitas() {
        return kuantitas; 
    }
    
    public void setKuantitas(int k) { 
        this.kuantitas = k; 
    }
}
