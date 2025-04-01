import io.mavsdk.mission.Mission;
import io.reactivex.CompletableSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.Scanner;


public class TakeoffAndLand {
  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    System.out.println("Test");
    Scanner sc = new Scanner(System.in);

    logger.debug("Starting example: takeoff and land...");

    io.mavsdk.System drone = new io.mavsdk.System();
    CountDownLatch latch = new CountDownLatch(1);

    List<Mission.MissionItem> cycle = new ArrayList<>();
    cycle.add(generateMissionItem(47.398039859999997, 8.5455725400000002));
    cycle.add(generateMissionItem(47.398036222362471, 8.5450146439425509));
    cycle.add(generateMissionItem(47.397825620791885, 8.5450092830163271));
    cycle.add(generateMissionItem(47.397832880000003, 8.5455939999999995));

    List<Mission.MissionItem> missionItems = new ArrayList<>();
    for (int i = 0; i < 300; i++) {
      missionItems.addAll(cycle);
    }

    Mission.MissionPlan missionPlan = new Mission.MissionPlan(missionItems);
    logger.debug("About to upload " + missionItems.size() + " mission items");

    drone.getMission()
            .setReturnToLaunchAfterMission(true)
            .andThen(drone.getMission().uploadMission(missionPlan)
                    .doOnComplete(() -> logger.debug("Upload succeeded")))
            .andThen(drone.getMission().downloadMission()
                    .doOnSubscribe(disposable -> logger.debug("Downloading mission"))
                    .doAfterSuccess(disposable -> logger.debug("Mission downloaded")))
            .toCompletable()
            .andThen((CompletableSource) cs -> latch.countDown())
            .subscribe();
    try {
      latch.await();
    } catch (InterruptedException ignored) {
        // This is expected
    }
  }

  public static Mission.MissionItem generateMissionItem(double latitudeDeg, double longitudeDeg) {
    return new Mission.MissionItem(
            latitudeDeg,
            longitudeDeg,
            10f,
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