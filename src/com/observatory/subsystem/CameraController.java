package com.observatory.subsystem;

public class CameraController {
    public static final double AMBIENT_TEMPERATURE_CELSIUS = 20.0;
    public static final double READY_TEMPERATURE_CELSIUS = -5.0;
    public static final double COOLEST_SUPPORTED_CELSIUS = -30.0;
    public static final double MAX_EXPOSURE_SECONDS = 600.0;

    private double sensorTemperature = AMBIENT_TEMPERATURE_CELSIUS;
    private int framesTaken;

    public void coolTo(double targetCelsius) throws CameraException {
        if (targetCelsius < COOLEST_SUPPORTED_CELSIUS) {
            throw new CameraException("Sensor cannot be cooled below " + COOLEST_SUPPORTED_CELSIUS + " C");
        }
        sensorTemperature = targetCelsius;
    }

    public Frame expose(double seconds) throws CameraException {
        if (sensorTemperature > READY_TEMPERATURE_CELSIUS) {
            throw new CameraException("Sensor is too warm to expose (" + sensorTemperature + " C)");
        }
        if (seconds <= 0 || seconds > MAX_EXPOSURE_SECONDS) {
            throw new CameraException("Exposure must be between 0 and " + MAX_EXPOSURE_SECONDS + " seconds");
        }
        framesTaken++;
        return new Frame(framesTaken, seconds, sensorTemperature);
    }

        public void warmUp() {
        sensorTemperature = AMBIENT_TEMPERATURE_CELSIUS;
    }
}
