package network_osrp;

public class HardwareRanks {
    private static final int MOST_LIGHEST = 1;
    private static final int MORE_LIGHT = 2;
    private static final int LIGHT = 3;
    private static final int MODERATE = 4;
    private static final int HEAVY = 5;
    private static final int MORE_HEAVY = 6;
    private static final int MOST_HEAVIEST = 7;




    public static int getRank(HardwarePollerPacket pollerPacket) {
        double cpu = pollerPacket.getCpuPercentage();
        double ram = pollerPacket.getRamPercentage();

        if (isMostHeaviest(cpu,ram)) {
            return MOST_HEAVIEST;
        } else if (isMoreHeavy(cpu,ram)) {
            return MORE_HEAVY;
        } else if (isHeavy(cpu,ram)) {
            return HEAVY;
        } else if (isModerate(cpu,ram)) {
            return MODERATE;
        } else if (isLight(cpu,ram)) {
            return LIGHT;
        } else if (isMoreLight(cpu,ram)) {
            return MORE_LIGHT;
        } else if (isMostLight(cpu,ram)) {
            return MOST_LIGHEST;
        }
        return 0;
    }

    private static boolean isMostHeaviest(double cpu , double ram) {
        return cpu >= 90 && cpu <= 100 && ram >= 90 && ram <= 100;
    }
    private static boolean isMoreHeavy(double cpu , double ram) {
        return cpu >= 80 && cpu <= 89 && ram >= 80 && ram <= 89;
    }
    private static boolean isHeavy(double cpu , double ram) {
        return cpu >= 70 && cpu <= 79 && ram >= 70 && ram <= 79;
    }
    private static boolean isModerate(double cpu , double ram) {
        return cpu >= 50  && ram >= 50 ;
    }
    private static boolean isLight(double cpu , double ram) {
        return cpu >= 30 && cpu <= 49 && ram >= 30 && ram <= 49;
    }
    private static boolean isMoreLight(double cpu , double ram) {
        return cpu >= 25 && cpu <= 29 && ram >= 25 && ram <= 29;
    }
    private static boolean isMostLight(double cpu , double ram) {
        return cpu >= 0 && cpu <= 24 && ram >= 0 && ram <= 24;
    }

}
