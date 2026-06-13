package processor;

import model.Member;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class MembershipManager {
    private ArrayList<Member> daftarMember;
    private final String FILE_DATABASE = "database_member.txt";

    public MembershipManager() {
        this.daftarMember = new ArrayList<>();
        muatDataDariFile();
    }

    public Member cariMember(String kode) {
        for (Member m : daftarMember) {
            if (m.getKodeMember().equalsIgnoreCase(kode)) {
                return m;
            }
        }
        return null;
    }

    public Member daftarMemberOtomatis(String nama) {
        String kodeBaru;
        do {
            kodeBaru = generateKodeAcak();
        } while (cariMember(kodeBaru) != null);

        Member memberBaru = new Member(kodeBaru, nama, 0);
        daftarMember.add(memberBaru);
        simpanDataKeFile();
        return memberBaru;
    }

    private String generateKodeAcak() {
        String karakter = "ABCDEF0123456789";
        StringBuilder kode = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 6; i++) {
            kode.append(karakter.charAt(rand.nextInt(karakter.length())));
        }
        return kode.toString();
    }

    public int hitungPoinBaru(double totalBelanja, String kodeMember) {
        int poinDasar = (int) (totalBelanja / 10);
        if (kodeMember != null && kodeMember.toUpperCase().contains("A")) {
            return poinDasar * 2;
        }
        return poinDasar;
    }

    public void tambahPoinMember(String kodeMember, int tambahanPoin) {
        Member m = cariMember(kodeMember);
        if (m != null) {
            m.setPoin(m.getPoin() + tambahanPoin);
            simpanDataKeFile();
        }
    }

    private void muatDataDariFile() {
    File file = new File(FILE_DATABASE);
    if (!file.exists()) {
        try {
            file.createNewFile(); 
        } catch (Exception e) {
            System.out.println("Gagal membuat file database baru.");
        }
        return;
    }
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        String baris;
        while ((baris = br.readLine()) != null) {
            if (baris.trim().isEmpty()) continue;
            String[] data = baris.split(",");
            if (data.length == 3) {
                daftarMember.add(new Member(data[0].trim(), data[1].trim(), Integer.parseInt(data[2].trim())));
            }
        }
    } catch (Exception e) {
        System.out.println("Gagal membaca database member.");
    }
}

    public void simpanDataKeFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_DATABASE))) {
            for (Member m : daftarMember) {
                bw.write(m.toString());
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Gagal menyimpan database member.");
        }
    }
}