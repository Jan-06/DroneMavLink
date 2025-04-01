import io.mavsdk.System;
import io.mavsdk.camera.Camera;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RunCamera {
  private static final Logger logger = LogManager.getLogger();

  public static void main(String[] args) {
    logger.debug("Starting example: camera...");

    System drone = new System();
    CountDownLatch latch = new CountDownLatch(1);

    drone.getCamera().getCaptureInfo()
        .subscribe(captureInfo -> logger.debug("Picture taken: " + captureInfo.getFileUrl()));

    drone.getCamera().takePhoto()
          .doOnComplete(() -> logger.debug("Taking a photo..."))
          .doOnError(throwable -> logger.error("Failed to take a photo: "
                  + ((Camera.CameraException) throwable).getCode()))
          .delay(2, TimeUnit.SECONDS)
          .andThen(drone.getCamera().takePhoto()
              .doOnComplete(() -> logger.debug("Taking a photo..."))
              .doOnError(throwable -> logger.error("Failed to take a photo: "
                      + ((Camera.CameraException) throwable).getCode())))
          .delay(2, TimeUnit.SECONDS)
          .subscribe(latch::countDown, throwable -> latch.countDown());

    try {
      latch.await();
    } catch (InterruptedException ignored) {
        // This is expected
    }
  }
}