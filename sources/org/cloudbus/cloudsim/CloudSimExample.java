package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.text.DecimalFormat;
import java.util.*;

public class CloudSimExample {

    public static void main(String[] args) {
        Log.printLine("Starting CloudSimExample with Genetic Algorithm...");

        try {
            int numUsers = 1;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;

            CloudSim.init(numUsers, calendar, traceFlag);

            Datacenter datacenter0 = createDatacenter("Datacenter_0");
            DatacenterBroker broker = new DatacenterBroker("Broker");
            int brokerId = broker.getId();

            List<Vm> vmList = createVms(brokerId);
            List<Cloudlet> cloudletList = createCloudlets(brokerId);

            Map<Integer, VmMetrics> csvData = VmMetricsLoader.load("C:/Users/narty/Downloads/data.csv");
            if (csvData == null || csvData.isEmpty()) {
                Log.printLine("Failed to load VM metrics from CSV. Exiting...");
                return;
            }

            Map<Vm, VmMetrics> vmMetricsMap = new HashMap<>();
            for (Vm vm : vmList) {
                VmMetrics metrics = csvData.get((int) vm.getId());
                if (metrics != null) {
                    vmMetricsMap.put(vm, metrics);
                } else {
                    Log.printLine("No metrics found for VM ID " + vm.getId() + ". Skipping...");
                }
            }

            if (vmMetricsMap.isEmpty()) {
                Log.printLine("No valid VM metrics found. Exiting...");
                return;
            }

            GeneticAlgorithm ga = new GeneticAlgorithm(vmList, cloudletList, vmMetricsMap);
            int[] bestMapping = ga.run();
            if (bestMapping == null) {
                Log.printLine("Genetic Algorithm failed to find a valid mapping. Exiting...");
                return;
            }

            for (int i = 0; i < cloudletList.size(); i++) {
                if (i < bestMapping.length && bestMapping[i] >= 0 && bestMapping[i] < vmList.size()) {
                    cloudletList.get(i).setVmId(vmList.get(bestMapping[i]).getId());
                } else {
                    Log.printLine("Invalid mapping for cloudlet " + i + ". Skipping...");
                }
            }

            broker.submitVmList(vmList);
            broker.submitCloudletList(cloudletList);

            CloudSim.startSimulation();
            CloudSim.stopSimulation();

            printCloudletList(broker.getCloudletReceivedList());

            Log.printLine("CloudSimExample finished!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("Unwanted errors happened.");
        }
    }

    private static Datacenter createDatacenter(String name) throws Exception {
        List<Host> hostList = new ArrayList<>();

        int numberOfHosts = 2;
        int hostMips = 3000; // More MIPS per core
        int numCores = 2; // Multiple cores
        int ram = 8192;
        long storage = 1000000;
        int bw = 10000;

        for (int i = 0; i < numberOfHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            for (int j = 0; j < numCores; j++) {
                peList.add(new Pe(j, new PeProvisionerSimple(hostMips)));
            }

            Host host = new Host(
                    i,
                    new RamProvisionerSimple(ram),
                    new BwProvisionerSimple(bw),
                    storage,
                    peList,
                    new VmSchedulerTimeShared(peList)
            );

            hostList.add(host);
        }

        String arch = "x86";
        String os = "Linux";
        String vmm = "Xen";
        double timeZone = 10.0;
        double cost = 3.0;
        double costPerMem = 0.05;
        double costPerStorage = 0.001;
        double costPerBw = 0.0;
        LinkedList<Storage> storageList = new LinkedList<>();

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, timeZone, cost, costPerMem, costPerStorage, costPerBw
        );

        return new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
    }



    private static List<Vm> createVms(int brokerId) {
        List<Vm> vmlist = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            int vmid = i;
            int mips = 1000 + i * 500;
            long size = 10000;
            int ram = 2048 + i * 512;
            long bw = 1000;
            int pesNumber = 1;
            String vmm = "Xen";

            Vm vm = new Vm(vmid, brokerId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            vmlist.add(vm);
        }
        return vmlist;
    }

    private static List<Cloudlet> createCloudlets(int brokerId) {
        List<Cloudlet> cloudletList = new ArrayList<>();
        UtilizationModel utilizationModel = new UtilizationModelFull();

        for (int i = 0; i < 8; i++) {
            int id = i;
            long length = 400000;
            long fileSize = 300;
            long outputSize = 300;

            Cloudlet cloudlet = new Cloudlet(id, length, 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }

    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent + "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        StringBuilder outputForLLM = new StringBuilder(); // collect output for LLM

        for (Cloudlet value : list) {
            cloudlet = value;
            StringBuilder line = new StringBuilder();

            line.append(indent).append(cloudlet.getCloudletId()).append(indent).append(indent);
            if (cloudlet.getStatus() == Cloudlet.SUCCESS) {
                line.append("SUCCESS");
                line.append(indent).append(indent).append(cloudlet.getResourceId());
                line.append(indent).append(indent).append(indent).append(cloudlet.getVmId());
                line.append(indent).append(indent).append(dft.format(cloudlet.getActualCPUTime()));
                line.append(indent).append(indent).append(dft.format(cloudlet.getExecStartTime()));
                line.append(indent).append(indent).append(dft.format(cloudlet.getFinishTime()));

                Log.printLine(line.toString());
                outputForLLM.append(line).append("\n");
            }
        }

        // Add LLM feedback at the end
        try {
            Log.printLine("\n🤖 Asking Ollama for feedback...");
            String feedback = LLMService.getLLMFeedback(outputForLLM.toString());
            Log.printLine("\n🧠 Ollama's Feedback:");
            Log.printLine(feedback);
        } catch (Exception e) {
            Log.printLine("❌ Could not get feedback from LLM: " + e.getMessage());
        }
    }

}
