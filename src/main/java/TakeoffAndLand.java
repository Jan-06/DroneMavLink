import io.mavsdk.mission.Mission;
import io.reactivex.CompletableSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class TakeoffAndLand {
  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    logger.debug("Starting dynamic grid mission...");

    io.mavsdk.System drone = new io.mavsdk.System();
    CountDownLatch latch = new CountDownLatch(1);

    System.out.print("Gib Flughoehe ein (Meter): ");
    float height = sc.nextFloat();

    System.out.println("Gib 4 Koordinaten ein (links unten → links oben → rechts oben → rechts unten):");
    Coordinate lb = new Coordinate(sc.nextDouble(), sc.nextDouble());
    Coordinate lt = new Coordinate(sc.nextDouble(), sc.nextDouble());
    Coordinate rt = new Coordinate(sc.nextDouble(), sc.nextDouble());
    Coordinate rb = new Coordinate(sc.nextDouble(), sc.nextDouble());

    List<Mission.MissionItem> missionItems = generateMissionGrid(lb, lt, rt, rb, height);

    Mission.MissionPlan missionPlan = new Mission.MissionPlan(missionItems);
    logger.debug("About to upload " + missionItems.size() + " mission items");

    drone.getMission()
            .setReturnToLaunchAfterMission(true)
            .andThen(drone.getMission().uploadMission(missionPlan)
                    .doOnComplete(() -> logger.debug("Upload succeeded")))
            .andThen(drone.getMission().startMission()
                    .doOnComplete(() -> logger.debug("Mission started")))
            .subscribe(() -> latch.countDown(),
                    throwable -> logger.error("Fehler: " + throwable.getMessage()));

    try {
      latch.await();
    } catch (InterruptedException ignored) {
    }
  }

  public static List<Mission.MissionItem> generateMissionGrid(Coordinate lb, Coordinate lt, Coordinate rt, Coordinate rb, float height) {
    double latStep = height * 2.0 / 111000.0; // Sichtfeld-Höhe
    double lonStep = height * 1.0 / (111000.0 * Math.cos(Math.toRadians(lb.lat))); // Sichtfeld-Breite

    double totalWidth = distanceInMeters(lb.lon, rt.lon, lb.lat);
    int numStrips = (int) Math.ceil(totalWidth / (height * 1.0));

    List<Mission.MissionItem> items = new ArrayList<>();

    for (int i = 0; i <= numStrips; i++) {
      double lonOffset = i * lonStep;

      Coordinate start = interpolate(lb, rb, lonOffset);
      Coordinate end = interpolate(lt, rt, lonOffset);

      if (i % 2 == 0) {
        items.add(generateItem(start.lat, start.lon, height));
        items.add(generateItem(end.lat, end.lon, height));
      } else {
        items.add(generateItem(end.lat, end.lon, height));
        items.add(generateItem(start.lat, start.lon, height));
      }
    }

    return items;
  }

  public static Coordinate interpolate(Coordinate left, Coordinate right, double lonOffset) {
    return new Coordinate(left.lat, left.lon + lonOffset);
  }

  public static double distanceInMeters(double lon1, double lon2, double lat) {
    double degreeToMeters = 111000.0 * Math.cos(Math.toRadians(lat));
    return Math.abs(lon2 - lon1) * degreeToMeters;
  }

  public static Mission.MissionItem generateItem(double lat, double lon, float alt) {
    return new Mission.MissionItem(
            lat,
            lon,
            alt,
            10f,
            true,
            0f,
            0f,
            Mission.MissionItem.CameraAction.TAKE_PHOTO,
            0f,
            0d,
            0f,
            0f,
            0f);
  }
}
