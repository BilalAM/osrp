package gui.utils;

public class OsrpCmdUtils {
    private static StringBuilder sharedCMDBuilder = new StringBuilder();
    private static StringBuilder sharedRoutingCMDBuilder = new StringBuilder();
    private static StringBuilder sharedRIPCMDBuilder = new StringBuilder();

    private OsrpCmdUtils(){

    }
    public static StringBuilder getSharedCMDBuilder(){
        return sharedCMDBuilder;
    }

    public static StringBuilder getSharedRIPCMDBuilder() {
        return sharedRIPCMDBuilder;
    }

    public static StringBuilder getSharedRoutingCMDBuilder() {
        return sharedRoutingCMDBuilder;
    }
}
