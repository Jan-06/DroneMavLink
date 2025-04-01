import java.util.ArrayList;
import java.util.List;

public class Mission {
    private ArrayList<MissionPoint> missionPoints = new ArrayList<>();

    public void addMissionPoint(double breite, double hoehe, double laenge) {
        missionPoints.add(new MissionPoint(breite, hoehe, laenge));
    }

    public ArrayList<MissionPoint> getMissionPoints() {
        return missionPoints;
    }
}

