package com.osrp.utility;

import com.osrp.beans.Weight;
import org.junit.jupiter.api.Test;

import java.io.IOException;


/**
 * @author saifasif
 */
public class NodeWeightUtilsTest {

    @Test
    public void getWeight() throws IOException {
        Weight weight = NodeWeightUtils.getWeight(SystemMetricUtils.getSystemMetrics());
        System.out.println(weight.name());
    }
}