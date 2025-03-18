public class Drone {
    private String id;
    private String modell;
    private String status;
    private int akku;

    public Drone(String id, String modell, String status, int akku) {
        this.id = id;
        this.modell = modell;
        this.status = status;
        this.akku = akku;
    }

    public String getId() {
        return id;
    }

    public String getModell() {
        return modell;
    }

    public String getStatus() {
        return status;
    }

    public int getAkku() {
        return akku;
    }
}