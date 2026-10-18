package com.observatory.subsystem;

import java.time.Instant;

public class SkyCalculator {
    private static final double UNIX_EPOCH_JULIAN_DATE = 2440587.5;
    private static final double J2000_JULIAN_DATE = 2451545.0;

    private final double latitudeDegrees;
    private final double longitudeDegrees;

    public SkyCalculator(double latitudeDegrees, double longitudeDegrees) {
        this.latitudeDegrees = latitudeDegrees;
        this.longitudeDegrees = longitudeDegrees;
    }

    public HorizontalPosition positionOf(CelestialObject object, Instant moment) {
        double hourAngle = Math.toRadians(
                normalizeDegrees(localSiderealTimeDegrees(moment) - object.rightAscensionHours() * 15.0));
        double dec = Math.toRadians(object.declinationDegrees());
        double lat = Math.toRadians(latitudeDegrees);

        double sinAlt = Math.sin(dec) * Math.sin(lat) + Math.cos(dec) * Math.cos(lat) * Math.cos(hourAngle);
        double altitude = Math.asin(clamp(sinAlt));

        double cosAz = (Math.sin(dec) - sinAlt * Math.sin(lat)) / (Math.cos(altitude) * Math.cos(lat));
        double azimuth = Math.toDegrees(Math.acos(clamp(cosAz)));
        if (Math.sin(hourAngle) > 0) {
            azimuth = 360.0 - azimuth;
        }
        return new HorizontalPosition(Math.toDegrees(altitude), azimuth);
    }

    double localSiderealTimeDegrees(Instant moment) {
        double julianDate = moment.getEpochSecond() / 86400.0 + UNIX_EPOCH_JULIAN_DATE;
        double greenwichSidereal = 280.46061837 + 360.98564736629 * (julianDate - J2000_JULIAN_DATE);
        return normalizeDegrees(greenwichSidereal + longitudeDegrees);
    }

    private static double normalizeDegrees(double degrees) {
        double result = degrees % 360.0;
        return result < 0 ? result + 360.0 : result;
    }

    private static double clamp(double value) {
        return Math.max(-1.0, Math.min(1.0, value));
    }
}
