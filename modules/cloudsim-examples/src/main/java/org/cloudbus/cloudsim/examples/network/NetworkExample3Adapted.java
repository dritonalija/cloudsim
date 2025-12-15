/*
 * Title:        CloudSim Toolkit - NetworkExample3 Adapted
 * Description:  Versioni i adaptuar për projektin e simulimit
 * 
 * ADAPTIMET E BËRA:
 * 1. Shtuar komente në shqip
 * 2. Parametrat janë të centralizuara dhe të lehta për t'u ndryshuar
 * 3. Shtuar llogaritje të kostos
 * 4. Shtuar metrika shtesë (throughput, pritje, etj.)
 * 5. Formatim më i mirë i rezultateve
 */

package org.cloudbus.cloudsim.examples.network;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 * ╔══════════════════════════════════════════════════════════════════════════╗
 * ║           NETWOREXAMPLE3 - VERSIONI I ADAPTUAR                           ║
 * ╠══════════════════════════════════════════════════════════════════════════╣
 * ║  Ky shembull demonstron:                                                  ║
 * ║  - Krijimin e dy datacenter-eve me nga një host                          ║
 * ║  - Ekzekutimin e cloudlet-eve të dy përdoruesve                          ║
 * ║  - Përdorimin e topologjisë së rrjetit (BRITE)                           ║
 * ║  - Llogaritjen e metrikave të performancës                                ║
 * ╚══════════════════════════════════════════════════════════════════════════╝
 */
public class NetworkExample3Adapted {

    // ═══════════════════════════════════════════════════════════════════════
    // ADAPTIMI 1: PARAMETRAT E CENTRALIZUARA (të lehta për t'u ndryshuar)
    // ═══════════════════════════════════════════════════════════════════════
    
    // Parametrat e Datacenter-it
    private static final String DC_ARCH = "x86";
    private static final String DC_OS = "Linux";
    private static final String DC_VMM = "Xen";
    private static final double DC_TIME_ZONE = 10.0;
    private static final double DC_COST_PER_SEC = 3.0;        // $/sekondë
    private static final double DC_COST_PER_MEM = 0.05;       // $/MB
    private static final double DC_COST_PER_STORAGE = 0.001;  // $/MB
    private static final double DC_COST_PER_BW = 0.02;        // $/Mbps (ADAPTIM: shtuar kosto BW)
    
    // Parametrat e Host-it
    private static final int HOST_MIPS = 1000;
    private static final int HOST_RAM = 2048;         // MB
    private static final long HOST_STORAGE = 1000000; // MB
    private static final int HOST_BW = 10000;         // Mbps
    private static final int HOST_PES = 1;            // Numri i CPU cores
    
    // Parametrat e VM-së
    private static final int VM_MIPS = 250;
    private static final int VM_PES = 1;
    private static final int VM_RAM = 512;            // MB
    private static final long VM_BW = 1000;           // Mbps
    private static final long VM_SIZE = 10000;        // MB
    
    // Parametrat e Cloudlet-it
    private static final long CLOUDLET_LENGTH = 40000;  // MI
    private static final long CLOUDLET_FILE_SIZE = 300; // bytes
    private static final long CLOUDLET_OUTPUT_SIZE = 300; // bytes
    private static final int CLOUDLET_PES = 1;

    // Brokers dhe listat
    private static DatacenterBroker broker1;
    private static DatacenterBroker broker2;
    private static List<Cloudlet> cloudletList1;
    private static List<Cloudlet> cloudletList2;
    private static List<Vm> vmlist1;
    private static List<Vm> vmlist2;

    /**
     * Metoda kryesore - pika e fillimit të simulimit
     */
    public static void main(String[] args) {
        printHeader();
        
        try {
            // ═══════════════════════════════════════════════════════════════
            // HAPI 1: INICIALIZIMI I CLOUDSIM
            // ═══════════════════════════════════════════════════════════════
            Log.println("► HAPI 1: Inicializimi i CloudSim...");
            int numUsers = 2;
            Calendar calendar = Calendar.getInstance();
            boolean traceFlag = false;
            
            CloudSim.init(numUsers, calendar, traceFlag);
            Log.println("  ✓ CloudSim u inicializua me " + numUsers + " përdorues");

            // ═══════════════════════════════════════════════════════════════
            // HAPI 2: KRIJIMI I DATACENTER-EVE
            // ═══════════════════════════════════════════════════════════════
            Log.println("\n► HAPI 2: Krijimi i Datacenter-eve...");
            Datacenter datacenter0 = createDatacenter("Datacenter_Prishtina");
            Datacenter datacenter1 = createDatacenter("Datacenter_Tirane");
            Log.println("  ✓ U krijuan 2 datacenter-e");

            // ═══════════════════════════════════════════════════════════════
            // HAPI 3: KRIJIMI I BROKERS (NDËRMJETËSVE)
            // ═══════════════════════════════════════════════════════════════
            Log.println("\n► HAPI 3: Krijimi i Broker-ave...");
            broker1 = new DatacenterBroker("Broker_Kompania_A");
            broker2 = new DatacenterBroker("Broker_Kompania_B");
            Log.println("  ✓ Broker1: " + broker1.getName() + " (ID=" + broker1.getId() + ")");
            Log.println("  ✓ Broker2: " + broker2.getName() + " (ID=" + broker2.getId() + ")");

            // ═══════════════════════════════════════════════════════════════
            // HAPI 4: KRIJIMI I MAKINAVE VIRTUALE
            // ═══════════════════════════════════════════════════════════════
            Log.println("\n► HAPI 4: Krijimi i VM-ve...");
            vmlist1 = new ArrayList<>();
            vmlist2 = new ArrayList<>();

            Vm vm1 = new Vm(0, broker1.getId(), VM_MIPS, VM_PES, VM_RAM, VM_BW, 
                           VM_SIZE, DC_VMM, new CloudletSchedulerTimeShared());
            Vm vm2 = new Vm(0, broker2.getId(), VM_MIPS, VM_PES, VM_RAM, VM_BW, 
                           VM_SIZE, DC_VMM, new CloudletSchedulerTimeShared());

            vmlist1.add(vm1);
            vmlist2.add(vm2);

            broker1.submitGuestList(vmlist1);
            broker2.submitGuestList(vmlist2);
            
            Log.println("  ✓ VM1: " + VM_MIPS + " MIPS, " + VM_RAM + " MB RAM");
            Log.println("  ✓ VM2: " + VM_MIPS + " MIPS, " + VM_RAM + " MB RAM");

            // ═══════════════════════════════════════════════════════════════
            // HAPI 5: KRIJIMI I CLOUDLET-EVE (DETYRAVE)
            // ═══════════════════════════════════════════════════════════════
            Log.println("\n► HAPI 5: Krijimi i Cloudlet-eve...");
            cloudletList1 = new ArrayList<>();
            cloudletList2 = new ArrayList<>();

            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet1 = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE, 
                utilizationModel, utilizationModel, utilizationModel);
            cloudlet1.setUserId(broker1.getId());

            Cloudlet cloudlet2 = new Cloudlet(0, CLOUDLET_LENGTH, CLOUDLET_PES, 
                CLOUDLET_FILE_SIZE, CLOUDLET_OUTPUT_SIZE, 
                utilizationModel, utilizationModel, utilizationModel);
            cloudlet2.setUserId(broker2.getId());

            cloudletList1.add(cloudlet1);
            cloudletList2.add(cloudlet2);

            broker1.submitCloudletList(cloudletList1);
            broker2.submitCloudletList(cloudletList2);
            
            Log.println("  ✓ Cloudlet1: " + CLOUDLET_LENGTH + " MI");
            Log.println("  ✓ Cloudlet2: " + CLOUDLET_LENGTH + " MI");
            Log.println("  ✓ Koha e pritur: " + (CLOUDLET_LENGTH / VM_MIPS) + " sekonda");

            // ═══════════════════════════════════════════════════════════════
            // HAPI 6: KONFIGURIMI I TOPOLOGJISË SË RRJETIT
            // ═══════════════════════════════════════════════════════════════
            Log.println("\n► HAPI 6: Konfigurimi i Topologjisë së Rrjetit...");
            NetworkTopology.buildNetworkTopology(
                NetworkExample1.class.getClassLoader().getResource("topology.brite").getPath());

            // Mapimi i entiteteve me nyjet BRITE
            NetworkTopology.mapNode(datacenter0.getId(), 0);  // DC_Prishtina → Node 0
            NetworkTopology.mapNode(datacenter1.getId(), 2);  // DC_Tirane → Node 2
            NetworkTopology.mapNode(broker1.getId(), 3);      // Kompania_A → Node 3
            NetworkTopology.mapNode(broker2.getId(), 4);      // Kompania_B → Node 4
            
            Log.println("  ✓ Datacenter_Prishtina → Node 0");
            Log.println("  ✓ Datacenter_Tirane → Node 2");
            Log.println("  ✓ Broker_Kompania_A → Node 3");
            Log.println("  ✓ Broker_Kompania_B → Node 4");

            // ═══════════════════════════════════════════════════════════════
            // HAPI 7: FILLIMI I SIMULIMIT
            // ═══════════════════════════════════════════════════════════════
            Log.println("\n► HAPI 7: Fillimi i Simulimit...");
            double startTime = System.currentTimeMillis();
            
            CloudSim.startSimulation();

            // ═══════════════════════════════════════════════════════════════
            // HAPI 8: MBLEDHJA E REZULTATEVE
            // ═══════════════════════════════════════════════════════════════
            List<Cloudlet> results1 = broker1.getCloudletReceivedList();
            List<Cloudlet> results2 = broker2.getCloudletReceivedList();

            CloudSim.stopSimulation();
            
            double endTime = System.currentTimeMillis();
            double simulationTime = (endTime - startTime) / 1000.0;

            // ═══════════════════════════════════════════════════════════════
            // HAPI 9: PRINTIMI DHE ANALIZA E REZULTATEVE (ADAPTIM)
            // ═══════════════════════════════════════════════════════════════
            printResults(results1, broker1.getName());
            printResults(results2, broker2.getName());
            
            // ADAPTIMI 2: Llogaritja e metrikave shtesë
            printMetrics(results1, results2, simulationTime);
            
            // ADAPTIMI 3: Llogaritja e kostos
            printCostAnalysis(results1, results2);

            printFooter();
            
        } catch (Exception e) {
            e.printStackTrace();
            Log.println("✗ Gabim: " + e.getMessage());
        }
    }

    /**
     * ADAPTIMI: Krijo datacenter me parametra të centralizuara
     */
    private static Datacenter createDatacenter(String name) {
        List<Host> hostList = new ArrayList<>();
        List<Pe> peList = new ArrayList<>();

        // Krijimi i PE-ve sipas numrit të konfiguruar
        for (int i = 0; i < HOST_PES; i++) {
            peList.add(new Pe(i, new PeProvisionerSimple(HOST_MIPS)));
        }

        // Krijimi i Host-it
        hostList.add(new Host(
            0,
            new RamProvisionerSimple(HOST_RAM),
            new BwProvisionerSimple(HOST_BW),
            HOST_STORAGE,
            peList,
            new VmSchedulerTimeShared(peList)
        ));

        // Karakteristikat e Datacenter-it
        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
            DC_ARCH, DC_OS, DC_VMM, hostList, DC_TIME_ZONE, 
            DC_COST_PER_SEC, DC_COST_PER_MEM, DC_COST_PER_STORAGE, DC_COST_PER_BW);

        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, 
                new VmAllocationPolicySimple(hostList), new LinkedList<>(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    /**
     * ADAPTIMI: Printim i formatuar i rezultateve
     */
    private static void printResults(List<Cloudlet> list, String userName) {
        DecimalFormat dft = new DecimalFormat("###.##");
        
        Log.println("\n╔══════════════════════════════════════════════════════════════════╗");
        Log.println("║  REZULTATET PËR: " + userName);
        Log.println("╠══════════════════════════════════════════════════════════════════╣");
        Log.println("║  Cloudlet │ Status  │ DC ID │ VM ID │ Koha CPU │ Fillimi │ Fundi  ║");
        Log.println("╠══════════════════════════════════════════════════════════════════╣");
        
        for (Cloudlet cloudlet : list) {
            String status = cloudlet.getStatus() == Cloudlet.CloudletStatus.SUCCESS ? "SUCCESS" : "FAILED ";
            Log.println("║     " + cloudlet.getCloudletId() + 
                "    │ " + status + 
                " │   " + cloudlet.getResourceId() + 
                "   │   " + cloudlet.getGuestId() + 
                "   │  " + dft.format(cloudlet.getActualCPUTime()) + "s" +
                "   │ " + dft.format(cloudlet.getExecStartTime()) + "s" +
                "  │ " + dft.format(cloudlet.getExecFinishTime()) + "s ║");
        }
        Log.println("╚══════════════════════════════════════════════════════════════════╝");
    }

    /**
     * ADAPTIMI 2: Llogaritja dhe printimi i metrikave
     */
    private static void printMetrics(List<Cloudlet> list1, List<Cloudlet> list2, double simTime) {
        Log.println("\n╔══════════════════════════════════════════════════════════════════╗");
        Log.println("║                    METRIKAT E PERFORMANCËS                        ║");
        Log.println("╠══════════════════════════════════════════════════════════════════╣");
        
        int totalCloudlets = list1.size() + list2.size();
        double totalCpuTime = 0;
        double maxFinishTime = 0;
        
        for (Cloudlet cl : list1) {
            totalCpuTime += cl.getActualCPUTime();
            if (cl.getExecFinishTime() > maxFinishTime) maxFinishTime = cl.getExecFinishTime();
        }
        for (Cloudlet cl : list2) {
            totalCpuTime += cl.getActualCPUTime();
            if (cl.getExecFinishTime() > maxFinishTime) maxFinishTime = cl.getExecFinishTime();
        }
        
        double avgCpuTime = totalCpuTime / totalCloudlets;
        double throughput = totalCloudlets / maxFinishTime;
        
        DecimalFormat dft = new DecimalFormat("###.####");
        
        Log.println("║  Numri total i Cloudlet-eve:      " + totalCloudlets + "                           ║");
        Log.println("║  Koha mesatare e CPU:             " + dft.format(avgCpuTime) + " sekonda              ║");
        Log.println("║  Koha maksimale e përfundimit:    " + dft.format(maxFinishTime) + " sekonda           ║");
        Log.println("║  Throughput:                      " + dft.format(throughput) + " cloudlets/sek      ║");
        Log.println("║  Koha reale e simulimit:          " + dft.format(simTime) + " sekonda               ║");
        Log.println("╚══════════════════════════════════════════════════════════════════╝");
    }

    /**
     * ADAPTIMI 3: Llogaritja e kostos
     */
    private static void printCostAnalysis(List<Cloudlet> list1, List<Cloudlet> list2) {
        Log.println("\n╔══════════════════════════════════════════════════════════════════╗");
        Log.println("║                    ANALIZA E KOSTOS                               ║");
        Log.println("╠══════════════════════════════════════════════════════════════════╣");
        
        DecimalFormat dft = new DecimalFormat("###.##");
        double totalCost = 0;
        
        for (Cloudlet cl : list1) {
            double cpuCost = cl.getActualCPUTime() * DC_COST_PER_SEC;
            double bwCost = (CLOUDLET_FILE_SIZE + CLOUDLET_OUTPUT_SIZE) * DC_COST_PER_BW / 1000;
            double cost = cpuCost + bwCost;
            totalCost += cost;
            Log.println("║  Cloudlet " + cl.getCloudletId() + " (User1): CPU=$" + 
                dft.format(cpuCost) + " + BW=$" + dft.format(bwCost) + 
                " = $" + dft.format(cost) + "          ║");
        }
        
        for (Cloudlet cl : list2) {
            double cpuCost = cl.getActualCPUTime() * DC_COST_PER_SEC;
            double bwCost = (CLOUDLET_FILE_SIZE + CLOUDLET_OUTPUT_SIZE) * DC_COST_PER_BW / 1000;
            double cost = cpuCost + bwCost;
            totalCost += cost;
            Log.println("║  Cloudlet " + cl.getCloudletId() + " (User2): CPU=$" + 
                dft.format(cpuCost) + " + BW=$" + dft.format(bwCost) + 
                " = $" + dft.format(cost) + "          ║");
        }
        
        Log.println("╠══════════════════════════════════════════════════════════════════╣");
        Log.println("║  KOSTOJA TOTALE: $" + dft.format(totalCost) + "                                      ║");
        Log.println("╚══════════════════════════════════════════════════════════════════╝");
    }

    private static void printHeader() {
        Log.println("\n");
        Log.println("╔══════════════════════════════════════════════════════════════════╗");
        Log.println("║                                                                  ║");
        Log.println("║          CLOUDSIM - NETWORKEXAMPLE3 ADAPTED                      ║");
        Log.println("║          Simulimi i Cloud Computing me Topologji Rrjeti          ║");
        Log.println("║                                                                  ║");
        Log.println("╠══════════════════════════════════════════════════════════════════╣");
        Log.println("║  Versioni: 1.0                                                   ║");
        Log.println("║  Projekti: Simulimi i dy datacenter-eve me dy përdorues         ║");
        Log.println("╚══════════════════════════════════════════════════════════════════╝\n");
    }

    private static void printFooter() {
        Log.println("\n╔══════════════════════════════════════════════════════════════════╗");
        Log.println("║              SIMULIMI PËRFUNDOI ME SUKSES!                        ║");
        Log.println("╚══════════════════════════════════════════════════════════════════╝\n");
    }
}

