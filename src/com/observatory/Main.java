package com.observatory;

import com.observatory.facade.ObservationException;
import com.observatory.facade.ObservationReport;
import com.observatory.facade.ObservatoryFacade;
import com.observatory.facade.ObservatoryMaintenanceFacade;
import com.observatory.facade.SkyConditions;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class Main {
    public static void main(String[] args) throws Exception {
        Clock autumnEvening = Clock.fixed(Instant.parse("2026-10-18T17:00:00Z"), ZoneOffset.UTC);

        System.out.println("=== 1. Clear sky, target above the horizon ===");
        ObservatoryFacade observatory = new ObservatoryFacade(SkyConditions.CLEAR, autumnEvening);
        System.out.println("Catalog: " + observatory.availableTargets());
        ObservationReport report = observatory.observe("Vega", 12, 30);
        System.out.println("REPORT -> " + report);

        System.out.println("\n=== 2. Target below the horizon ===");
        tryObserve(new ObservatoryFacade(SkyConditions.CLEAR, autumnEvening), "Alpha Centauri");

        System.out.println("\n=== 3. Cloudy sky ===");
        tryObserve(new ObservatoryFacade(SkyConditions.CLOUDY, autumnEvening), "Vega");

        System.out.println("\n=== 4. Unknown target ===");
        tryObserve(new ObservatoryFacade(SkyConditions.CLEAR, autumnEvening), "Planet Nine");

        System.out.println("\n=== 5. Second facade: maintenance ===");
        System.out.println(new ObservatoryMaintenanceFacade().buildDarkFrameLibrary(20, 60));
    }

    private static void tryObserve(ObservatoryFacade observatory, String target) {
        try {
            observatory.observe(target, 5, 30);
        } catch (ObservationException e) {
            System.out.println("HANDLED -> " + e.getMessage());
        }
    }
}
