package org.cloudbus.cloudsim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class VmMetricsLoader {

    public static Map<Integer, VmMetrics> load(String path) {
        Map<Integer, VmMetrics> metricsMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            br.readLine(); // Skip the header

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length >= 4) {
                    int id = Integer.parseInt(values[0].trim());
                    double cost = Double.parseDouble(values[1].trim());
                    double reliability = Double.parseDouble(values[2].trim());
                    double bandwidth = Double.parseDouble(values[3].trim());

                    metricsMap.put(id, new VmMetrics(cost, reliability, bandwidth));
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.err.println("Error loading VM metrics: " + e.getMessage());
            return null;
        }

        return metricsMap;
    }
}
