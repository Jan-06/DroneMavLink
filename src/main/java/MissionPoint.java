public class MissionPoint {
    private double breite;
    private double hoehe;
    private double laenge;

    public MissionPoint(double breite, double hoehe, double laenge) {
        this.breite = breite;
        this.hoehe = hoehe;
        this.laenge = laenge;
    }

    public double getBreite() {
        return breite;
    }

    public double getHoehe() {
        return hoehe;
    }

    public double getLaenge() {
        return laenge;
    }
}