public class Coordinate {
    public double lat;
    public double lon;

    public Coordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public String toString() {
        return String.format("(%.6f, %.6f)", lat, lon);
    }
}