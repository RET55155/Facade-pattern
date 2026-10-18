package com.observatory.subsystem;

public class TelescopeMount {
    public static final double MIN_ALTITUDE_DEGREES = 20.0;

    private boolean parked = true;
    private boolean tracking;
    private HorizontalPosition pointedAt;

    public void assertReachable(HorizontalPosition target) throws TargetBelowHorizonException {
        if (target.altitudeDegrees() < MIN_ALTITUDE_DEGREES) {
            throw new TargetBelowHorizonException(String.format(
                    "Target is at %.1f deg altitude, mount limit is %.1f deg",
                    target.altitudeDegrees(), MIN_ALTITUDE_DEGREES));
        }
    }

    public void unpark() {
        parked = false;
    }

    public void slewTo(HorizontalPosition target) throws MountException {
        if (parked) {
            throw new MountException("Cannot slew while the mount is parked");
        }
        assertReachable(target);
        pointedAt = target;
        tracking = false;
    }

    public void startTracking() throws MountException {
        if (pointedAt == null) {
            throw new MountException("Slew to a target before tracking");
        }
        tracking = true;
    }

        public void park() {
        parked = true;
        tracking = false;
        pointedAt = null;
    }

    public boolean isTracking() {
        return tracking;
    }
}
