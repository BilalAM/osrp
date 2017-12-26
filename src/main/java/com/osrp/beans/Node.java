package com.osrp.beans;

/**
 * @author saifasif
 */
public class Node {

    private String Id;
    private double cpuLoad;
    private double memLoad;
    private Weight weight;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public double getCpuLoad() {
        return cpuLoad;
    }

    public void setCpuLoad(double cpuLoad) {
        this.cpuLoad = cpuLoad;
    }

    public double getMemLoad() {
        return memLoad;
    }

    public void setMemLoad(double memLoad) {
        this.memLoad = memLoad;
    }

    public Weight getWeight() {
        return weight;
    }

    public void setWeight(Weight weight) {
        this.weight = weight;
    }
}
