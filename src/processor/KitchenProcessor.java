package processor;

import model.*;
import java.util.*;

public class KitchenProcessor {
    private Queue<OrderLine> antrianMakanan; // PriorityQueue harga tertinggi dulu
    private Stack<OrderLine> antrianMinuman; // LIFO

    public KitchenProcessor() {
        antrianMakanan = new PriorityQueue<>((o1, o2) -> 
            Double.compare(o2.getMenu().getHarga(), o1.getMenu().getHarga()));
        antrianMinuman = new Stack<>();
    }

    public void tambahPesanan(Order order) {
        for (OrderLine ol : order.getListPesanan()) {
            if (ol.getMenu() instanceof Makanan) {
                antrianMakanan.add(ol);
            } else {
                antrianMinuman.push(ol);
            }
        }
    }

    public void prosesPesananDapur() {
        System.out.println("\n=== PROSES DAPUR KOHISOP ===");
        System.out.println("Makanan (Priority by Harga Desc):");
        while (!antrianMakanan.isEmpty()) {
            OrderLine ol = antrianMakanan.poll();
            System.out.printf(" - %s x%d (Rp %.0f)\n", ol.getMenu().getNama(), ol.getKuantitas(), ol.getMenu().getHarga());
        }

        System.out.println("\nMinuman (Last Ordered First):");
        while (!antrianMinuman.isEmpty()) {
            OrderLine ol = antrianMinuman.pop();
            System.out.printf(" - %s x%d\n", ol.getMenu().getNama(), ol.getKuantitas());
        }
    }
}