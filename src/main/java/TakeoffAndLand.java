import io.mavsdk.System;
import io.mavsdk.action.Action;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


public class TakeoffAndLand {
  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    logger.debug("Starting example: takeoff and land...");

    System drone = new System();
    CountDownLatch latch = new CountDownLatch(1);

    drone.getAction().arm()
          .doOnComplete(() -> logger.debug("Arming..."))
          .doOnError(throwable -> logger.error("Failed to arm: "
                  + ((Action.ActionException) throwable).getMessage()))
          .andThen(drone.getAction().takeoff()
            .doOnComplete(() -> logger.debug("Taking off..."))
            .doOnError(throwable -> logger.error("Failed to take off: "
                    + ((Action.ActionException) throwable).getMessage())))
          .delay(5, TimeUnit.SECONDS)
          .andThen(drone.getAction().land()
            .doOnComplete(() -> logger.debug("Landing..."))
            .doOnError(throwable -> logger.error("Failed to land: "
                    + ((Action.ActionException) throwable).getMessage())))
          .subscribe(latch::countDown, throwable -> latch.countDown());

    try {
      latch.await();
    } catch (InterruptedException ignored) {
        // This is expected
    }
  }
}