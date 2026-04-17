import java.util.LinkedList;

public class Order {

    private LinkedList<OrderLine> keranjang = new LinkedList<>();

    public LinkedList<OrderLine> getKeranjang() {
    return this.keranjang;
    }

    public void tambahAtauUpdatePesanan(Menu item, String inputQty) {
        // Cek jika input adalah 'CC'
        if (inputQty.equalsIgnoreCase("CC")) {
            System.out.println("Pesanan Dibatalkan.");
            System.exit(0);
        }

        // Skip atau hapus (Input 'S' atau '0')
        if (inputQty.equalsIgnoreCase("S") || inputQty.equals("0")) {
            hapusDariKeranjang(item);
            return;
        }

        // Tentukan kuantitas (Default 1 jika kosong)
        int qtyBaru;
        try {
            qtyBaru = inputQty.isEmpty() ? 1 : Integer.parseInt(inputQty);
        } catch (NumberFormatException e) {
            System.out.println("Input kuantitas tidak valid!");
            return;
        }

        // Cek sudah ada di keranjang?
        int qtyLama = 0;
        for (OrderLine ol : keranjang) {
          if (ol.getMenu().getKode().equals(item.getKode())) {
            qtyLama = ol.getKuantitas();
            break;
          }
        }

       // Validasi total tiap menu
       int totalQty = qtyLama + qtyBaru;
    
        if (!item.kuantitasValid(totalQty)) {
          if (qtyLama == 0) {
            // Pertama kali input tapi langsung kebanyakan
            System.out.println("Gagal! Maksimal per jenis hanya " + item.maksimalPesan() + " porsi.");
          } 
          else {
            // Sebelumnya sudah ada, lalu ditambah dan jadi kebanyakan
            System.out.println("Gagal! Total pesanan " + item.getNama() + " akan menjadi " + totalQty + " porsi.");
            System.out.println("Maksimal per jenis hanya " + item.maksimalPesan() + " porsi.");
          }
        return; 
        }

        updateKeranjang(item, totalQty);
    }

    public boolean apakahSudahMaksimal(Menu item) {
      for (OrderLine ol : keranjang) {
        if (ol.getMenu().getKode().equalsIgnoreCase(item.getKode())) {
            // Cek apakah jumlahnya sudah mencapai batas maksimal kategori tersebut
            return ol.getKuantitas() >= item.maksimalPesan();
        }
      }
      return false; 
    }

    public boolean apakahSudahAdaDiKeranjang(Menu item) {
      for (OrderLine ol : keranjang) {
        if (ol.getMenu().getKode().equalsIgnoreCase(item.getKode())) return true;
      }
      return false;
    }

    private void updateKeranjang(Menu item, int qtyBaru) {
      for (OrderLine ol : keranjang) {
        if (ol.getMenu().getKode().equals(item.getKode())) {
            ol.setKuantitas(qtyBaru); 
            return;
        }
      }
      // Jika benar-benar baru, masukkan ke list
      keranjang.add(new OrderLine(item, qtyBaru));
    }

    private void hapusDariKeranjang(Menu item) {
        keranjang.removeIf(ol -> ol.getMenu().getKode().equals(item.getKode()));
    }

    public void tampilkanTabelPesanan() {
        System.out.println("---------------------------------------------------------");
        System.out.printf("| %-3s |              %-20s | %-6s |\n", "Kode", "Menu", "Kuantitas");
        System.out.println("---------------------------------------------------------");
        for (OrderLine ol : keranjang) {
            System.out.printf("|  %-3s | %-33s |     %-5d |\n", 
                ol.getMenu().getKode(), ol.getMenu().getNama(), ol.getKuantitas());
        }
        System.out.println("---------------------------------------------------------");
    }
    
    public int getJumlahJenis(Class<?> tipe) {
        int count = 0;
        for (OrderLine ol : keranjang) {
            if (tipe.isInstance(ol.getMenu())) count++;
        }
        return count;
    }
}