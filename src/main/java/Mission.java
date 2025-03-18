import java.util.ArrayList;
import java.util.List;

public class Mission {
    private ArrayList<MissionPoint> MissionPoints;

    public void addMissionPoint(double Breite, double Höhe, double Länge) {
        MissionPoint point = new MissionPoint(Breite, Höhe, Länge);
        MissionPoints.add(point);
    }

    public ArrayList<MissionPoint> getMissionPoints() {
        return MissionPoints;
    }
}
