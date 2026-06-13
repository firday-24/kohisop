package model;

//Kelas Minuman mewarisi semua sifat dan atribut dari abstract class Menu
public class Minuman extends Menu {
    // Constructor Minuman yang meneruskan nilai parameter ke constructor induk (super)
    public Minuman(String kode, String nama, double harga) {
        super(kode, nama, harga);
    }

    //Override untuk menentukan jenis menu secara spesifik
    @Override
    public String getJenis() {
        return "Minuman";
    }
}