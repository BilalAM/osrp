package gui.utils;

public class CmdUtils {
    private final StringBuilder sharedCMDBuilder = new StringBuilder();
    private final StringBuilder sharedRoutingCMDBuilder = new StringBuilder();
    private final StringBuilder sharedRIPCMDBuilder = new StringBuilder();

    private static final CmdUtils RIP_INSTANCE = new CmdUtils();
    private static final CmdUtils OSRP_INSTANCE = new CmdUtils();

    public static CmdUtils ripInstance() { return RIP_INSTANCE; }
    public static CmdUtils osrpInstance() { return OSRP_INSTANCE; }

    // Static methods delegate to RIP instance (backward compatibility for existing RIP code)
    public static StringBuilder getSharedCMDBuilder() {
        return RIP_INSTANCE.sharedCMDBuilder;
    }

    public static StringBuilder getSharedRIPCMDBuilder() {
        return RIP_INSTANCE.sharedRIPCMDBuilder;
    }

    public static StringBuilder getSharedRoutingCMDBuilder() {
        return RIP_INSTANCE.sharedRoutingCMDBuilder;
    }

    // Instance methods for protocol-specific access
    public StringBuilder getCMDBuilder() { return sharedCMDBuilder; }
    public StringBuilder getRIPCMDBuilder() { return sharedRIPCMDBuilder; }
    public StringBuilder getRoutingCMDBuilder() { return sharedRoutingCMDBuilder; }
}
