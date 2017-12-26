package com.osrp.utility;


import com.osrp.beans.Node;
import com.osrp.beans.Weight;

/**
 * @author saifasif
 */
public class NodeWeightUtils {


    public static Weight getWeight(Node node) {
        SystemMetricUtils.getSystemMetrics(node);
        return Weight.FOUR;
    }
}
