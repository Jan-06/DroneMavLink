import io.mavsdk.System;
import io.mavsdk.camera.Camera;
import io.reactivex.Completable;
import io.reactivex.Single;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ListPhotos {
  private static final Logger logger = LogManager.getLogger();
  public static void main(String[] args) {
    logger.debug("Starting example: taking and listing photos...");

    System drone = new System();
    CountDownLatch latch = new CountDownLatch(1);

    // Completable trying to take 50 pictures (stopping in case of error)
    Completable takePhotoCompletable
            = drone.getCamera().takePhoto()
                .doOnComplete(() -> logger.debug("Picture taken"))
                .doOnError(throwable -> logger.error("Error: " + throwable.getMessage()))
                .onErrorComplete()
                .delay(1, TimeUnit.SECONDS)
                .repeat(50);

    // Single getting a list of all the pictures available on the camera
    Single<List<Camera.CaptureInfo>> photosList
            = drone.getCamera().listPhotos(Camera.PhotosRange.ALL)
            .doOnSuccess(captureInfos -> logger.debug("Got " + captureInfos.size() + " photos"))
            .doOnError(throwable -> logger.error("Error: " + throwable.getMessage()));

    try {
      latch.await();
    } catch (InterruptedException ignored) {
      // This is expected
    }
  }
}