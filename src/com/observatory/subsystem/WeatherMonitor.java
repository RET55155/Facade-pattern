package com.observatory.subsystem;

import java.util.ArrayList;
import java.util.List;

public class WeatherMonitor {
    public static final double MAX_CLOUD_COVER_PERCENT = 30.0;
    public static final double MAX_WIND_SPEED_KMH = 35.0;
    public static final double MAX_HUMIDITY_PERCENT = 85.0;

    private final WeatherReading reading;

    public WeatherMonitor(WeatherReading reading) {
        this.reading = reading;
    }

    public void assertSafeToObserve() throws UnsafeWeatherException {
        List<String> problems = new ArrayList<>();
        if (reading.cloudCoverPercent() > MAX_CLOUD_COVER_PERCENT) {
            problems.add("cloud cover " + reading.cloudCoverPercent() + "%");
        }
        if (reading.windSpeedKmh() > MAX_WIND_SPEED_KMH) {
            problems.add("wind " + reading.windSpeedKmh() + " km/h");
        }
        if (reading.humidityPercent() > MAX_HUMIDITY_PERCENT) {
            problems.add("humidity " + reading.humidityPercent() + "%");
        }
        if (!problems.isEmpty()) {
            throw new UnsafeWeatherException("Unsafe weather (" + String.join(", ", problems) + ")");
        }
    }
}
