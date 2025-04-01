package org.cloudbus.cloudsim;



public class VmMetrics {
    public double cost;
    public double reliability;
    public double bandwidth;

    public VmMetrics(double cost, double reliability, double bandwidth) {
        this.cost = cost;
        this.reliability = reliability;
        this.bandwidth = bandwidth;
    }
}

