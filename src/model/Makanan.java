package model;

//Kelas Makanan mewarisi semua sifat dan atribut dari abstract class Menu
public class Makanan extends Menu {
    // Constructor Makanan yang meneruskan nilai parameter ke constructor induk (super)
    public Makanan(String kode, String nama, double harga) {
        super(kode, nama, harga);
    }

    //Override untuk menentukan jenis menu secara spesifik
    @Override
    public String getJenis() {
        return "Makanan";
    }
}