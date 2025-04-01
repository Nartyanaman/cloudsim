package org.cloudbus.cloudsim;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeneticAlgorithm {

    private final List<Vm> vmList;
    private final List<Cloudlet> cloudletList;
    private final Map<Vm, VmMetrics> vmMetricsMap;
    private final int populationSize = 10;
    private final Random rand = new Random();

    public GeneticAlgorithm(List<Vm> vmList, List<Cloudlet> cloudletList, Map<Vm, VmMetrics> vmMetricsMap) {
        this.vmList = vmList;
        this.cloudletList = cloudletList;
        this.vmMetricsMap = vmMetricsMap;
    }

    public int[] run() {
        List<int[]> population = initializePopulation();
        int[] bestChromosome = population.get(0);
        // Initialize with first individual
        double bestFitness = fitness(bestChromosome);

        int generations = 50;
        for (int gen = 0; gen < generations; gen++) {
            List<int[]> newPopulation = new ArrayList<>();
            for (int i = 0; i < populationSize; i++) {
                int[] parent1 = select(population);
                int[] parent2 = select(population);
                int[] child = crossover(parent1, parent2);
                mutate(child);
                newPopulation.add(child);
                double fitness = fitness(child);
                if (fitness > bestFitness) {
                    bestFitness = fitness;
                    bestChromosome = child.clone();
                }
            }
            population = newPopulation;
        }
        return bestChromosome;
    }

    private List<int[]> initializePopulation() {
        List<int[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            int[] chromosome = new int[cloudletList.size()];
            for (int j = 0; j < chromosome.length; j++) {
                chromosome[j] = rand.nextInt(vmList.size());
            }
            population.add(chromosome);
        }
        return population;
    }

    private int[] select(List<int[]> population) {
        int i = rand.nextInt(population.size());
        int j = rand.nextInt(population.size());
        return fitness(population.get(i)) > fitness(population.get(j)) ? population.get(i) : population.get(j);
    }

    private int[] crossover(int[] p1, int[] p2) {
        int[] child = new int[p1.length];
        int point = rand.nextInt(p1.length);
        System.arraycopy(p1, 0, child, 0, point);
        if (p2.length - point >= 0) System.arraycopy(p2, point, child, point, p2.length - point);
        return child;
    }

    private void mutate(int[] chromosome) {
        for (int i = 0; i < chromosome.length; i++) {
            double mutationRate = 0.1;
            if (rand.nextDouble() < mutationRate) {
                chromosome[i] = rand.nextInt(vmList.size());
            }
        }
    }

    private double fitness(int[] chromosome) {
        double totalCost = 0, totalReliability = 0, totalBandwidth = 0;
        for (int j : chromosome) {
            Vm vm = vmList.get(j);
            VmMetrics m = vmMetricsMap.get(vm);
            if (m != null) {
                totalCost += m.cost;
                totalReliability += m.reliability;
                totalBandwidth += m.bandwidth;
            }
        }
        if (totalCost == 0) totalCost = 1.0; // Avoid division by zero
        return (totalBandwidth / totalCost) + (totalReliability / chromosome.length);
    }
}
