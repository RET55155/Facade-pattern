package com.observatory.subsystem;

public class DomeController {
    private boolean slitOpen;
    private double azimuthDegrees;

    public void openSlit() throws DomeException {
        if (slitOpen) {
            throw new DomeException("Dome slit is already open");
        }
        slitOpen = true;
    }

        public double rotateTo(double targetAzimuthDegrees) throws DomeException {
        if (!slitOpen) {
            throw new DomeException("Open the slit before rotating the dome");
        }
        double target = ((targetAzimuthDegrees % 360.0) + 360.0) % 360.0;
        double shortestTravel = Math.abs(((target - azimuthDegrees + 540.0) % 360.0) - 180.0);
        azimuthDegrees = target;
        return shortestTravel;
    }

        public void closeSlit() {
        slitOpen = false;
    }

    public boolean isSlitOpen() {
        return slitOpen;
    }
}
