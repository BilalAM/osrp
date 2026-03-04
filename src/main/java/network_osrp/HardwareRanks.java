package network_osrp;

/**
 * Maps hardware utilization metrics (CPU and RAM) to a rank from 1-7.
 * Rank 1 = most available (lightest load), Rank 7 = least available (heaviest load).
 * Uses the average of CPU and RAM percentages for a clean, gap-free classification.
 */
public class HardwareRanks {
    public static final int MOST_LIGHTEST = 1;
    public static final int MORE_LIGHT = 2;
    public static final int LIGHT = 3;
    public static final int MODERATE = 4;
    public static final int HEAVY = 5;
    public static final int MORE_HEAVY = 6;
    public static final int MOST_HEAVIEST = 7;

    public static int getRank(HardwarePollerPacket pollerPacket) {
        double cpu = pollerPacket.getCpuPercentage();
        double ram = pollerPacket.getRamPercentage();
        double combined = (cpu + ram) / 2.0;

        if (combined >= 90) return MOST_HEAVIEST;
        if (combined >= 80) return MORE_HEAVY;
        if (combined >= 70) return HEAVY;
        if (combined >= 50) return MODERATE;
        if (combined >= 30) return LIGHT;
        if (combined >= 15) return MORE_LIGHT;
        return MOST_LIGHTEST;
    }
}
