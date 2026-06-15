package model;

// Menggunakan abstract class karena Menu merupakan blueprint umum yang tidak boleh diinstansiasi secara langsung
public abstract class Menu {
    // Hak akses private untuk melindungi data item menu
    private String kode;
    private String nama;
    private double harga;

    // Constructor untuk menginisialisasi properti dasar setiap menu
    public Menu(String kode, String nama, double harga) {
        this.kode = kode;
        this.nama = nama;
        this.harga = harga;
    }

    // Method Getter untuk mengambil nilai dari properti private
    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public double getHarga() { return harga; }
    
    // Abstract method yang wajib diimplementasikan secara spesifik oleh kelas turunannya (Polymorphism)
    public abstract String getJenis();
}