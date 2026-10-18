package com.observatory.facade;

import com.observatory.subsystem.CameraController;
import com.observatory.subsystem.CelestialObject;
import com.observatory.subsystem.DomeController;
import com.observatory.subsystem.Frame;
import com.observatory.subsystem.HorizontalPosition;
import com.observatory.subsystem.ImageStacker;
import com.observatory.subsystem.ObservationLogger;
import com.observatory.subsystem.SkyCalculator;
import com.observatory.subsystem.StackedImage;
import com.observatory.subsystem.StarCatalog;
import com.observatory.subsystem.SubsystemException;
import com.observatory.subsystem.TelescopeMount;
import com.observatory.subsystem.WeatherMonitor;
import com.observatory.subsystem.WeatherReading;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;

public class ObservatoryFacade {
    private static final double SITE_LATITUDE = 51.1694;
    private static final double SITE_LONGITUDE = 71.4491;
    private static final double SENSOR_TARGET_CELSIUS = -10.0;

    private final WeatherMonitor weather;
    private final DomeController dome = new DomeController();
    private final TelescopeMount mount = new TelescopeMount();
    private final CameraController camera = new CameraController();
    private final ImageStacker stacker = new ImageStacker();
    private final StarCatalog catalog = new StarCatalog();
    private final SkyCalculator sky = new SkyCalculator(SITE_LATITUDE, SITE_LONGITUDE);
    private final ObservationLogger logger = new ObservationLogger(System.out);
    private final Clock clock;

    public ObservatoryFacade() {
        this(SkyConditions.CLEAR, Clock.systemUTC());
    }

    public ObservatoryFacade(SkyConditions conditions, Clock clock) {
        this.weather = new WeatherMonitor(readingFor(conditions));
        this.clock = clock;
    }

    public ObservationReport observe(String targetName, int exposureCount, double exposureSeconds)
            throws ObservationException {
        requireValidRequest(targetName, exposureCount, exposureSeconds);
        try {
            logger.log("Observation requested: " + targetName);
            weather.assertSafeToObserve();

            CelestialObject target = catalog.find(targetName);
            HorizontalPosition position = sky.positionOf(target, clock.instant());
            mount.assertReachable(position);
            logger.log(String.format("%s is at alt %.1f deg, az %.1f deg",
                    target.name(), position.altitudeDegrees(), position.azimuthDegrees()));

            prepareObservatory(position);
            List<Frame> frames = captureFrames(exposureCount, exposureSeconds);
            StackedImage image = stacker.stack(frames);
            logger.log("Stacked " + image.frameCount() + " frames");

            return new ObservationReport(
                    target.name(), position.altitudeDegrees(), position.azimuthDegrees(),
                    image.frameCount(), image.totalExposureSeconds(), image.signalToNoiseGain());
        } catch (SubsystemException e) {
            logger.log("FAILED: " + e.getMessage());
            throw new ObservationException("Observation of '" + targetName + "' failed: " + e.getMessage(), e);
        } finally {
            secureObservatory();
        }
    }

    public List<String> availableTargets() {
        return catalog.knownTargets();
    }

    public List<String> observingLog() {
        return logger.entries();
    }

    private void prepareObservatory(HorizontalPosition position) throws SubsystemException {
        dome.openSlit();
        double travelled = dome.rotateTo(position.azimuthDegrees());
        logger.log(String.format("Dome open and rotated %.0f deg", travelled));
        mount.unpark();
        mount.slewTo(position);
        mount.startTracking();
        logger.log("Mount tracking target");
        camera.coolTo(SENSOR_TARGET_CELSIUS);
        logger.log("Sensor cooled to " + SENSOR_TARGET_CELSIUS + " C");
    }

    private List<Frame> captureFrames(int count, double seconds) throws SubsystemException {
        List<Frame> frames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            frames.add(camera.expose(seconds));
        }
        logger.log("Captured " + frames.size() + " x " + seconds + " s frames");
        return frames;
    }

        private void secureObservatory() {
        mount.park();
        dome.closeSlit();
        camera.warmUp();
        logger.log("Observatory secured (mount parked, dome closed, sensor warmed)");
    }

    private static void requireValidRequest(String targetName, int exposureCount, double exposureSeconds) {
        if (targetName == null || targetName.isBlank()) {
            throw new IllegalArgumentException("Target name must not be blank");
        }
        if (exposureCount < 1) {
            throw new IllegalArgumentException("Need at least one exposure");
        }
        if (exposureSeconds <= 0) {
            throw new IllegalArgumentException("Exposure time must be positive");
        }
    }

    private static WeatherReading readingFor(SkyConditions conditions) {
        return switch (conditions) {
            case CLEAR -> new WeatherReading(10, 12, 55);
            case CLOUDY -> new WeatherReading(85, 15, 70);
            case STORMY -> new WeatherReading(60, 70, 95);
        };
    }
}
