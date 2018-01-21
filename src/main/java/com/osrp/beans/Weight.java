package com.osrp.beans;

/**
 * @author saifasif
 */
public enum Weight {

    ONE(1, "Highly available"),
    TWO(2, "Highly available"),
    THREE(3, "Highly available"),
    FOUR(4, "Moderately available"),
    FIVE(5, "Moderately available"),
    SIX(6, "low available"),
    SEVEN(7, "not available");

    int weight;
    String message;

    Weight(int weight, String message) {
        this.weight = weight;
        this.message = message;
    }
}
