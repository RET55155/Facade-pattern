package com.observatory.facade;

public record ObservationReport(
        String targetName,
        double altitudeDegrees,
        double azimuthDegrees,
        int framesCaptured,
        double totalExposureSeconds,
        double signalToNoiseGain) {
    @Override
    public String toString() {
        return String.format(
                "%s: alt %.1f deg, az %.1f deg, %d frames, %.0f s total, SNR x%.2f",
                targetName, altitudeDegrees, azimuthDegrees,
                framesCaptured, totalExposureSeconds, signalToNoiseGain);
    }
}
