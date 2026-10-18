package com.observatory.facade;

import com.observatory.subsystem.CameraController;
import com.observatory.subsystem.Frame;
import com.observatory.subsystem.ImageStacker;
import com.observatory.subsystem.ObservationLogger;
import com.observatory.subsystem.StackedImage;
import com.observatory.subsystem.SubsystemException;

import java.util.ArrayList;
import java.util.List;

public class ObservatoryMaintenanceFacade {
    private static final double DARK_FRAME_CELSIUS = -15.0;

    private final CameraController camera = new CameraController();
    private final ImageStacker stacker = new ImageStacker();
    private final ObservationLogger logger = new ObservationLogger(System.out);

    public String buildDarkFrameLibrary(int frameCount, double exposureSeconds) throws ObservationException {
        if (frameCount < 1 || exposureSeconds <= 0) {
            throw new IllegalArgumentException("Need a positive frame count and exposure time");
        }
        try {
            camera.coolTo(DARK_FRAME_CELSIUS);
            List<Frame> darks = new ArrayList<>();
            for (int i = 0; i < frameCount; i++) {
                darks.add(camera.expose(exposureSeconds));
            }
            StackedImage master = stacker.stack(darks);
            String summary = String.format("Master dark: %d frames, %.0f s each, noise reduced x%.2f",
                    master.frameCount(), exposureSeconds, master.signalToNoiseGain());
            logger.log(summary);
            return summary;
        } catch (SubsystemException e) {
            logger.log("FAILED: " + e.getMessage());
            throw new ObservationException("Dark frame library failed: " + e.getMessage(), e);
        } finally {
            camera.warmUp();
        }
    }
}
