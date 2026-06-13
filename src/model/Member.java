package model;

public class Member {
    private String kodeMember;
    private String namaMember;
    private int poin;

    public Member(String kodeMember, String namaMember, int poin) {
        this.kodeMember = kodeMember;
        this.namaMember = namaMember;
        this.poin = poin;
    }

    public String getKodeMember() { return kodeMember; }
    public String getNamaMember() { return namaMember; }
    public int getPoin() { return poin; }
    public void setPoin(int poin) { this.poin = poin; }

    @Override
    public String toString() {
        return kodeMember + "," + namaMember + "," + poin;
    }
}