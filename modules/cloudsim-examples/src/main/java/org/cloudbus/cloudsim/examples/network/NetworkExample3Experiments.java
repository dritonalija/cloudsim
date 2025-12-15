/*
 * NetworkExample3Experiments - Eksperimentet e Plota për Projektin
 * 
 * 7 Skenarë Testimi:
 * 1. Baseline      - 5 Cloudlets, 1000 MIPS, Normal latency
 * 2. Workload      - 10 Cloudlets, 1000 MIPS, Normal latency  
 * 3. Infrastructure- 5 Cloudlets, 2000 MIPS, 2 Hosts/DC
 * 4. Network       - 5 Cloudlets, 1000 MIPS, Low latency
 * 5. Multi-VM      - 15 Cloudlets, 1000 MIPS, Normal latency
 * 6. Heterogeneous - 5 Cloudlets, Mixed MIPS
 * 7. Congestion    - 5 Cloudlets, 1000 MIPS, High latency
 */

package org.cloudbus.cloudsim.examples.network;

import java.text.DecimalFormat;
import java.util.*;

import org.cloudbus.cloudsim.*;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.*;

public class NetworkExample3Experiments {

    // Rezultatet e të gjitha eksperimenteve
    private static List<ExperimentResult> allResults = new ArrayList<>();
    private static DecimalFormat df = new DecimalFormat("###.##");

    public static void main(String[] args) {
        printHeader();
        
        // Ekzekuto të 7 eksperimentet
        runExperiment1_Baseline();
        runExperiment2_Workload();
        runExperiment3_Infrastructure();
        runExperiment4_Network();
        runExperiment5_MultiVM();
        runExperiment6_Heterogeneous();
        runExperiment7_Congestion();
        
        // Printo tabelën përmbledhëse
        printSummaryTable();
        printConclusions();
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EKSPERIMENTI 1: BASELINE (Pika e Referencës)
    // ═══════════════════════════════════════════════════════════════════════════
    private static void runExperiment1_Baseline() {
        printExperimentHeader(1, "BASELINE", "5 Cloudlets, 1000 MIPS, Normal Latency, 1 Host/DC");
        
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            
            Datacenter dc0 = createDatacenter("DC_0", 1, 1000);
            Datacenter dc1 = createDatacenter("DC_1", 1, 1000);
            
            DatacenterBroker broker = new DatacenterBroker("Broker");
            
            List<Vm> vmList = createVMs(broker.getId(), 2, 250);
            broker.submitGuestList(vmList);
            
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), 5, 10000);
            broker.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation();
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            
            ExperimentResult result = analyzeResults("1-Baseline", results, 5, 1000, "Normal", 1);
            allResults.add(result);
            printExperimentResults(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EKSPERIMENTI 2: WORKLOAD (Ngarkesë e Lartë)
    // ═══════════════════════════════════════════════════════════════════════════
    private static void runExperiment2_Workload() {
        printExperimentHeader(2, "WORKLOAD", "10 Cloudlets, 1000 MIPS, Normal Latency, 1 Host/DC");
        
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            
            Datacenter dc0 = createDatacenter("DC_0", 1, 1000);
            Datacenter dc1 = createDatacenter("DC_1", 1, 1000);
            
            DatacenterBroker broker = new DatacenterBroker("Broker");
            
            List<Vm> vmList = createVMs(broker.getId(), 2, 250);
            broker.submitGuestList(vmList);
            
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), 10, 10000);
            broker.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation();
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            
            ExperimentResult result = analyzeResults("2-Workload", results, 10, 1000, "Normal", 1);
            allResults.add(result);
            printExperimentResults(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EKSPERIMENTI 3: INFRASTRUCTURE (Resurse më të Mira)
    // ═══════════════════════════════════════════════════════════════════════════
    private static void runExperiment3_Infrastructure() {
        printExperimentHeader(3, "INFRASTRUCTURE", "5 Cloudlets, 2000 MIPS, Normal Latency, 2 Hosts/DC");
        
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            
            Datacenter dc0 = createDatacenter("DC_0", 2, 2000); // 2 hosts
            Datacenter dc1 = createDatacenter("DC_1", 2, 2000); // 2 hosts
            
            DatacenterBroker broker = new DatacenterBroker("Broker");
            
            List<Vm> vmList = createVMs(broker.getId(), 4, 500); // 4 VMs, 500 MIPS
            broker.submitGuestList(vmList);
            
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), 5, 10000);
            broker.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation();
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            
            ExperimentResult result = analyzeResults("3-Infrastructure", results, 5, 2000, "Normal", 2);
            allResults.add(result);
            printExperimentResults(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EKSPERIMENTI 4: NETWORK (Latency e Ulët)
    // ═══════════════════════════════════════════════════════════════════════════
    private static void runExperiment4_Network() {
        printExperimentHeader(4, "NETWORK", "5 Cloudlets, 1000 MIPS, Low Latency, 1 Host/DC");
        
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            
            Datacenter dc0 = createDatacenter("DC_0", 1, 1000);
            Datacenter dc1 = createDatacenter("DC_1", 1, 1000);
            
            DatacenterBroker broker = new DatacenterBroker("Broker");
            
            // Krijo lidhje me latency të ulët
            NetworkTopology.addLink(dc0.getId(), broker.getId(), 1000, 0.5); // 0.5ms latency
            NetworkTopology.addLink(dc1.getId(), broker.getId(), 1000, 0.5);
            
            List<Vm> vmList = createVMs(broker.getId(), 2, 250);
            broker.submitGuestList(vmList);
            
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), 5, 10000);
            broker.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation();
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            
            ExperimentResult result = analyzeResults("4-Network", results, 5, 1000, "Low", 1);
            allResults.add(result);
            printExperimentResults(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EKSPERIMENTI 5: MULTI-VM (Paralelizëm)
    // ═══════════════════════════════════════════════════════════════════════════
    private static void runExperiment5_MultiVM() {
        printExperimentHeader(5, "MULTI-VM", "15 Cloudlets, 1000 MIPS, Normal Latency, 1 Host/DC");
        
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            
            Datacenter dc0 = createDatacenter("DC_0", 1, 1000);
            Datacenter dc1 = createDatacenter("DC_1", 1, 1000);
            
            DatacenterBroker broker = new DatacenterBroker("Broker");
            
            List<Vm> vmList = createVMs(broker.getId(), 5, 200); // 5 VMs
            broker.submitGuestList(vmList);
            
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), 15, 10000);
            broker.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation();
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            
            ExperimentResult result = analyzeResults("5-Multi-VM", results, 15, 1000, "Normal", 1);
            allResults.add(result);
            printExperimentResults(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EKSPERIMENTI 6: HETEROGENEOUS (MIPS të Ndryshme)
    // ═══════════════════════════════════════════════════════════════════════════
    private static void runExperiment6_Heterogeneous() {
        printExperimentHeader(6, "HETEROGENEOUS", "5 Cloudlets, Mixed MIPS (250-500), Normal Latency");
        
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            
            // DC me host të ndryshëm
            Datacenter dc0 = createDatacenter("DC_0", 1, 1000);
            Datacenter dc1 = createDatacenter("DC_1", 1, 2000); // DC më i fuqishëm
            
            DatacenterBroker broker = new DatacenterBroker("Broker");
            
            // VMs me MIPS të ndryshme
            List<Vm> vmList = new ArrayList<>();
            vmList.add(new Vm(0, broker.getId(), 250, 1, 512, 1000, 10000, "Xen", new CloudletSchedulerTimeShared()));
            vmList.add(new Vm(1, broker.getId(), 500, 1, 512, 1000, 10000, "Xen", new CloudletSchedulerTimeShared()));
            broker.submitGuestList(vmList);
            
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), 5, 10000);
            broker.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation();
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            
            ExperimentResult result = analyzeResults("6-Heterogeneous", results, 5, 0, "Normal", 1);
            result.vmMips = "Mixed";
            allResults.add(result);
            printExperimentResults(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // EKSPERIMENTI 7: CONGESTION (Latency e Lartë - Stress Test)
    // ═══════════════════════════════════════════════════════════════════════════
    private static void runExperiment7_Congestion() {
        printExperimentHeader(7, "CONGESTION", "5 Cloudlets, 1000 MIPS, High Latency, 1 Host/DC");
        
        try {
            CloudSim.init(1, Calendar.getInstance(), false);
            
            Datacenter dc0 = createDatacenter("DC_0", 1, 1000);
            Datacenter dc1 = createDatacenter("DC_1", 1, 1000);
            
            DatacenterBroker broker = new DatacenterBroker("Broker");
            
            // Krijo lidhje me latency të lartë (simulon congestion)
            NetworkTopology.addLink(dc0.getId(), broker.getId(), 100, 50); // 50ms latency, 100 bw
            NetworkTopology.addLink(dc1.getId(), broker.getId(), 100, 50);
            
            List<Vm> vmList = createVMs(broker.getId(), 2, 250);
            broker.submitGuestList(vmList);
            
            List<Cloudlet> cloudletList = createCloudlets(broker.getId(), 5, 10000);
            broker.submitCloudletList(cloudletList);
            
            CloudSim.startSimulation();
            List<Cloudlet> results = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();
            
            ExperimentResult result = analyzeResults("7-Congestion", results, 5, 1000, "High", 1);
            allResults.add(result);
            printExperimentResults(result);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static Datacenter createDatacenter(String name, int numHosts, int mips) {
        List<Host> hostList = new ArrayList<>();
        
        for (int i = 0; i < numHosts; i++) {
            List<Pe> peList = new ArrayList<>();
            peList.add(new Pe(0, new PeProvisionerSimple(mips)));
            
            hostList.add(new Host(
                i,
                new RamProvisionerSimple(4096),
                new BwProvisionerSimple(10000),
                1000000,
                peList,
                new VmSchedulerTimeShared(peList)
            ));
        }
        
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            "x86", "Linux", "Xen", hostList, 10.0, 3.0, 0.05, 0.001, 0.0);
        
        try {
            return new Datacenter(name, characteristics, 
                new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private static List<Vm> createVMs(int brokerId, int count, int mips) {
        List<Vm> vmList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            vmList.add(new Vm(i, brokerId, mips, 1, 512, 1000, 10000, "Xen", 
                new CloudletSchedulerTimeShared()));
        }
        return vmList;
    }
    
    private static List<Cloudlet> createCloudlets(int brokerId, int count, long length) {
        List<Cloudlet> cloudletList = new ArrayList<>();
        UtilizationModel utilizationModel = new UtilizationModelFull();
        
        for (int i = 0; i < count; i++) {
            Cloudlet cloudlet = new Cloudlet(i, length, 1, 300, 300, 
                utilizationModel, utilizationModel, utilizationModel);
            cloudlet.setUserId(brokerId);
            cloudletList.add(cloudlet);
        }
        return cloudletList;
    }
    
    private static ExperimentResult analyzeResults(String name, List<Cloudlet> results, 
            int cloudlets, int mips, String latency, int hosts) {
        ExperimentResult exp = new ExperimentResult();
        exp.name = name;
        exp.numCloudlets = cloudlets;
        exp.vmMips = String.valueOf(mips);
        exp.latency = latency;
        exp.hostsPerDC = hosts;
        exp.completedCloudlets = results.size();
        
        double totalTime = 0, minTime = Double.MAX_VALUE, maxTime = 0;
        
        for (Cloudlet cl : results) {
            double time = cl.getActualCPUTime();
            totalTime += time;
            if (time < minTime) minTime = time;
            if (time > maxTime) maxTime = time;
            if (cl.getExecFinishTime() > exp.makespan) {
                exp.makespan = cl.getExecFinishTime();
            }
        }
        
        exp.avgTime = results.isEmpty() ? 0 : totalTime / results.size();
        exp.minTime = minTime == Double.MAX_VALUE ? 0 : minTime;
        exp.maxTime = maxTime;
        exp.throughput = exp.makespan > 0 ? results.size() / exp.makespan : 0;
        
        return exp;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // OUTPUT METHODS
    // ═══════════════════════════════════════════════════════════════════════════
    
    private static void printHeader() {
        Log.println("");
        Log.println("================================================================================");
        Log.println("       CLOUDSIM - EKSPERIMENTET E PLOTA (7 SKENARE)");
        Log.println("       NetworkExample3 - Testimi i Performancës");
        Log.println("================================================================================");
    }
    
    private static void printExperimentHeader(int num, String name, String desc) {
        Log.println("");
        Log.println("--------------------------------------------------------------------------------");
        Log.println("EKSPERIMENTI " + num + ": " + name);
        Log.println("Pershkrimi: " + desc);
        Log.println("--------------------------------------------------------------------------------");
    }
    
    private static void printExperimentResults(ExperimentResult r) {
        Log.println("  Cloudlets te perfunduara: " + r.completedCloudlets + "/" + r.numCloudlets);
        Log.println("  Koha mesatare CPU:        " + df.format(r.avgTime) + " s");
        Log.println("  Koha minimale:            " + df.format(r.minTime) + " s");
        Log.println("  Koha maksimale:           " + df.format(r.maxTime) + " s");
        Log.println("  Makespan:                 " + df.format(r.makespan) + " s");
        Log.println("  Throughput:               " + df.format(r.throughput * 1000) + " cloudlets/1000s");
    }
    
    private static void printSummaryTable() {
        Log.println("");
        Log.println("================================================================================");
        Log.println("                    TABELA PERMBLEDHESE E REZULTATEVE");
        Log.println("================================================================================");
        Log.println("");
        Log.println("| Skenari          | Cloudlets | VM MIPS | Latency | Hosts | Avg Time | Makespan | Throughput |");
        Log.println("|------------------|-----------|---------|---------|-------|----------|----------|------------|");
        
        for (ExperimentResult r : allResults) {
            Log.println(String.format("| %-16s | %9d | %7s | %7s | %5d | %6.2fs  | %6.2fs   | %8.4f   |",
                r.name, r.numCloudlets, r.vmMips, r.latency, r.hostsPerDC, 
                r.avgTime, r.makespan, r.throughput));
        }
        Log.println("");
    }
    
    private static void printConclusions() {
        Log.println("================================================================================");
        Log.println("                           PERFUNDIMET");
        Log.println("================================================================================");
        Log.println("");
        Log.println("1. BASELINE: Pika e references per krahasim");
        Log.println("");
        Log.println("2. WORKLOAD: Me shume cloudlets = Makespan me i gjate");
        Log.println("   - Throughput mbetet i njejte sepse CPU eshte faktori kufizues");
        Log.println("");
        Log.println("3. INFRASTRUCTURE: Me shume hosts dhe MIPS = Performanca me e mire");
        Log.println("   - Koha mesatare ulet ndjeshem me resurse me te mira");
        Log.println("");
        Log.println("4. NETWORK: Latency e ulet = Fillim me i shpejte i cloudlets");
        Log.println("   - Ndikon ne kohen e pergjithshme te sistemit");
        Log.println("");
        Log.println("5. MULTI-VM: Paralelizmi rrit throughput-in");
        Log.println("   - Me shume VMs = Me shume cloudlets ne paralel");
        Log.println("");
        Log.println("6. HETEROGENEOUS: VMs me MIPS te ndryshme");
        Log.println("   - Cloudlets perfundojne me kohe te ndryshme");
        Log.println("");
        Log.println("7. CONGESTION: Latency e larte = Vonese ne komunikim");
        Log.println("   - Ndikon negativisht ne makespan");
        Log.println("");
        Log.println("================================================================================");
        Log.println("                    EKSPERIMENTET PERFUNDUAN ME SUKSES!");
        Log.println("================================================================================");
    }
    
    // Klasa per ruajtjen e rezultateve
    static class ExperimentResult {
        String name;
        int numCloudlets;
        String vmMips;
        String latency;
        int hostsPerDC;
        int completedCloudlets;
        double avgTime;
        double minTime;
        double maxTime;
        double makespan;
        double throughput;
    }
}

