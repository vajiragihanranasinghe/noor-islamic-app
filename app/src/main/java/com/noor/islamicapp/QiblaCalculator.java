package com.noor.islamicapp;

public class QiblaCalculator {

    // Kaaba coordinates in Mecca
    public static final double KAABA_LAT = 21.4225;
    public static final double KAABA_LON = 39.8262;

    /**
     * Calculate the Qibla bearing (direction to Kaaba) from a location.
     * Returns degrees from true North, 0-360.
     */
    public static double getQiblaBearing(double lat, double lon) {
        double phi1 = Math.toRadians(lat);
        double phi2 = Math.toRadians(KAABA_LAT);
        double deltaLambda = Math.toRadians(KAABA_LON - lon);

        double x = Math.sin(deltaLambda) * Math.cos(phi2);
        double y = Math.cos(phi1) * Math.sin(phi2)
                 - Math.sin(phi1) * Math.cos(phi2) * Math.cos(deltaLambda);

        double bearing = Math.toDegrees(Math.atan2(x, y));
        return (bearing + 360.0) % 360.0;
    }

    /**
     * Haversine distance to Kaaba in kilometers.
     */
    public static double getDistanceToKaabaKm(double lat, double lon) {
        double R = 6371.0;
        double phi1 = Math.toRadians(lat);
        double phi2 = Math.toRadians(KAABA_LAT);
        double dPhi = Math.toRadians(KAABA_LAT - lat);
        double dLambda = Math.toRadians(KAABA_LON - lon);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                 + Math.cos(phi1) * Math.cos(phi2)
                 * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Normalize angle to 0-360.
     */
    public static float normalize(float deg) {
        while (deg < 0)   deg += 360;
        while (deg >= 360) deg -= 360;
        return deg;
    }
}
