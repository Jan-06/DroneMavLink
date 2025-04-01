import io.mavsdk.mission.Mission;
import io.reactivex.CompletableSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

public class TakeoffAndLand {
  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    logger.debug("Starting dynamic mission example...");

    io.mavsdk.System drone = new io.mavsdk.System();
    CountDownLatch latch = new CountDownLatch(1);

    List<Mission.MissionItem> missionItems = new ArrayList<>();

    System.out.print("Gib die Flughöhe ein (in Metern): ");
    float altitude = sc.nextFloat();

    for (int i = 1; i <= 4; i++) {
      System.out.println("Wegpunkt " + i + ":");
      System.out.print("Latitude: ");
      double latitude = sc.nextDouble();
      System.out.print("Longitude: ");
      double longitude = sc.nextDouble();

      missionItems.add(generateMissionItem(latitude, longitude, altitude));
    }

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

  public static Mission.MissionItem generateMissionItem(double latitudeDeg, double longitudeDeg, float altitude) {
    return new Mission.MissionItem(
            latitudeDeg,
            longitudeDeg,
            altitude,
            10f, // Fluggeschwindigkeit
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
