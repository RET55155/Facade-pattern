package com.observatory.subsystem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StarCatalog {
    private final Map<String, CelestialObject> objects = new LinkedHashMap<>();

    public StarCatalog() {
        register("Vega", 18.6156, 38.7837);
        register("Deneb", 20.6905, 45.2803);
        register("Capella", 5.2782, 45.9980);
        register("Sirius", 6.7525, -16.7161);
        register("Polaris", 2.5303, 89.2641);
        register("M31", 0.7123, 41.2687);
        register("Alpha Centauri", 14.6600, -60.8340);
    }

    public CelestialObject find(String name) throws UnknownTargetException {
        CelestialObject found = objects.get(normalize(name));
        if (found == null) {
            throw new UnknownTargetException("Target '" + name + "' is not in the catalog");
        }
        return found;
    }

    public List<String> knownTargets() {
        List<String> names = new ArrayList<>();
        objects.values().forEach(o -> names.add(o.name()));
        return names;
    }

    private void register(String name, double raHours, double decDegrees) {
        objects.put(normalize(name), new CelestialObject(name, raHours, decDegrees));
    }

    private static String normalize(String name) {
        return name.trim().toLowerCase(Locale.ROOT);
    }
}
