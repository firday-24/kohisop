import java.util.LinkedList;

public class Order {
    private LinkedList<OrderLine> keranjang = new LinkedList<>();

    public LinkedList<OrderLine> getKeranjang() { return keranjang; }

    public void tambahAtauUpdatePesanan(Menu item, String inputQty) {
        if (inputQty.equalsIgnoreCase("CC")) {
            System.out.println("Pesanan Dibatalkan.");
            System.exit(0);
        }

        if (inputQty.equalsIgnoreCase("S") || inputQty.equals("0")) {
            hapusDariKeranjang(item);
            return;
        }

        int qtyBaru;
        try {
            qtyBaru = inputQty.isEmpty() ? 1 : Integer.parseInt(inputQty);
        } catch (NumberFormatException e) {
            System.out.println("Input kuantitas tidak valid!");
            return;
        }

        int qtyLama = 0;
        for (OrderLine ol : keranjang) {
            if (ol.getMenu().getKode().equals(item.getKode())) {
                qtyLama = ol.getKuantitas();
                break;
            }
        }

        int totalQty = qtyLama + qtyBaru;
        if (!item.kuantitasValid(totalQty)) {
            if (qtyLama == 0) {
                System.out.println("Gagal! Maksimal per jenis hanya " + item.maksimalPesan() + " porsi.");
            } else {
                System.out.println("Gagal! Total pesanan " + item.getNama() +
                        " akan menjadi " + totalQty + " porsi.");
                System.out.println("Maksimal per jenis hanya " + item.maksimalPesan() + " porsi.");
            }
            return;
        }

        updateKeranjang(item, totalQty);
    }

    public boolean apakahSudahMaksimal(Menu item) {
        for (OrderLine ol : keranjang) {
            if (ol.getMenu().getKode().equalsIgnoreCase(item.getKode())) {
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
        keranjang.add(new OrderLine(item, qtyBaru));
    }

    private void hapusDariKeranjang(Menu item) {
        keranjang.removeIf(ol -> ol.getMenu().getKode().equals(item.getKode()));
    }

    public int getJumlahJenis(Class<?> tipe) {
        int count = 0;
        for (OrderLine ol : keranjang) {
            if (tipe.isInstance(ol.getMenu())) count++;
        }
        return count;
    }

    // Tabel pesanan sementara (tanpa pajak final) — ditampilkan saat masih input
    public void tampilkanTabelPesanan() {
        System.out.println("-------------------------------------------------------");
        System.out.printf("| %-3s | %-27s | %-8s |\n", "Kode", "Menu", "Kuantitas");
        System.out.println("-------------------------------------------------------");
        for (OrderLine ol : keranjang) {
            System.out.printf("| %-3s | %-27s | %-8d |\n",
                    ol.getMenu().getKode(), ol.getMenu().getNama(), ol.getKuantitas());
        }
        System.out.println("-------------------------------------------------------");
    }

    // Tabel final lengkap dengan kolom pajak dan total — dipanggil saat selesai memesan
    public void tampilkanTagihan() {
        System.out.println("=============================================================================");
        System.out.printf("| %-3s | %-27s | %3s | %7s | %5s | %10s |\n",
                "Kode", "Menu", "Qty", "Harga", "Pajak", "Subtotal");
        System.out.println("=============================================================================");

        double totalHarga = 0;
        double totalPajak  = 0;

        for (OrderLine ol : keranjang) {
            Menu m        = ol.getMenu();
            int  qty      = ol.getKuantitas();
            double harga  = m.getHarga();
            double pajak  = m.hitungPajak();   // pajak per satuan
            double sub    = (harga + pajak) * qty;

            // Label persentase pajak
            String pctLabel;
            if (pajak == 0)              pctLabel = "0%";
            else if (pajak == harga * 0.08) pctLabel = "8%";
            else                         pctLabel = "11%";

            System.out.printf("| %-3s | %-27s | %3d | %7.0f | %5s | %10.2f |\n",
                    m.getKode(), m.getNama(), qty, harga, pctLabel, sub);

            totalHarga += harga * qty;
            totalPajak += pajak * qty;
        }

        double totalAkhir = totalHarga + totalPajak;

        System.out.println("=============================================================================");
        System.out.printf("  %52s Subtotal : Rp %10.2f\n", "", totalHarga);
        System.out.printf("  %52s Pajak    : Rp %10.2f\n", "", totalPajak);
        System.out.println("-----------------------------------------------------------------------------");
        System.out.printf("  %52s TOTAL    : Rp %10.2f\n", "", totalAkhir);
        System.out.println("=============================================================================");
    }

    // Kembalikan nilai total akhir (harga + pajak) untuk diproses pembayaran
    public double getTotalAkhir() {
        double total = 0;
        for (OrderLine ol : keranjang) {
            Menu m = ol.getMenu();
            total += (m.getHarga() + m.hitungPajak()) * ol.getKuantitas();
        }
        return total;
    }
}
