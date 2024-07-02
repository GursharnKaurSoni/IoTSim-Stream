package iotsimstream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import org.cloudbus.cloudsim.Vm;

import iotsimstream.vmOffers.VMOffers;

/** This class to be used by resource configuration algorithm
 *
 * @author mbarika
 */
public class DatacentersVMsServices {
    
    static LinkedHashMap<Integer, VMOffers> datacentersWithVMOffers;
    public static ArrayList<Service> services;
    static ArrayList<IotDevice> exSources;
    static LinkedHashMap<Integer, Integer> vmgidDatacenterIDMap; //each entry is global id of VM with corrsponding datacenterID; Foe example, <0,2> means 0 is the global id of vm and 2 is the corresponding datacenterID
    static LinkedHashMap<Integer, Integer> vmgidVMIDatDatacenterMap; //each entry is global vm id of VM with corresponding actual vmid in datacenter; For example, <1,2> means 1 is global id for vm and 2 is corresponding vmid in datacenter
    static LinkedHashMap<Integer, Double> serviceDPReqMap; // each entry is service id and its data processing requirement
    static LinkedHashMap<Integer, Double> serviceUserDPRateReqMap; // each entry is service id and its user data processing rate req.
    static LinkedHashMap<Integer, Double> serviceReqiredMIPSMap; // each entry is service id and its calaculated required MIPS
    static LinkedHashMap<Integer, Double> vmgidCostMap; // each entry is vm global id and its price
    static LinkedHashMap<Integer, Double> vmgidMIPSMap; // each entry is vm global id and its MIPS value
    static LinkedHashMap<Integer, Integer> vmgidBoottimeMap; // each entry is vm global id and its boot time
    static ArrayList<Integer> servicesList;
    static HashMap<Integer, Service> serviceMap;
    static int numbOfDatacenters; //number of datacenters / clouds
    static LinkedHashMap<String, Integer> vmidDatacenteridVMGIDMap; // each entry is vmid_datacenterid as string and corrsponding vmgid
    static String DPRateSelection;
    static double minDPRate;
    static LinkedHashMap<Integer, Entry<Vm,Integer>> VMOfferMap; // each entry is vm id and its MIPS value
    static int datacenterStartIndexinCloudSim=2;
    public static LinkedHashMap<Integer, Double> servicesInOutProportion= new LinkedHashMap<>(); //It will not change since we address velocity change not app structure, so the values computed once
    //static LinkedHashMap<Integer, Double> servicesInOutProportion;
    //static HashMap<Integer, Double> ServiceDPRateMap=new HashMap<>(); //Each entry is serviceid and its data processing rate based on the mapped VMs 
    //static HashMap<Integer,ExternalSource> exSourceIDs;
    //ArrayList<Service> services;

    public static void init(LinkedHashMap<Integer, VMOffers> dsWithVMOffers, ArrayList<Service> listServices, ArrayList<IotDevice> listEXSources, String DPRateSelection, double minDPRate) {
        
        DatacentersVMsServices.DPRateSelection=DPRateSelection;
        DatacentersVMsServices.minDPRate=minDPRate;
        
        servicesList=new ArrayList<>();
        vmgidDatacenterIDMap=new LinkedHashMap<>();
        vmgidVMIDatDatacenterMap=new LinkedHashMap<>();
        vmidDatacenteridVMGIDMap=new LinkedHashMap<>();
        
        serviceMap=new HashMap<>();
        serviceDPReqMap=new LinkedHashMap<>();
        serviceUserDPRateReqMap=new LinkedHashMap<>();
        serviceReqiredMIPSMap=new LinkedHashMap<>();
        
        vmgidCostMap=new LinkedHashMap<>();
        vmgidMIPSMap=new LinkedHashMap<>();
        vmgidBoottimeMap=new LinkedHashMap<>();
        //servicesInOutProportion= new LinkedHashMap<>();
        //this.services=services;
        datacentersWithVMOffers=dsWithVMOffers;
        services=listServices;
        exSources=listEXSources;
        numbOfDatacenters=datacentersWithVMOffers.size();
        
        serviceOutputPartsPreCompiated=new HashMap<>();
        serviceInputPartsPreCompiated=new HashMap<>();
        
        //Fill vmgidDatacenterIDMap and vmgidVMIDatDatacenterMap using datacentersWithVMOffers  as well as vmgidCostMap and vmgidMIPSMap
        System.out.println("------------------VMGID Table------------ \nVMGID\tDatacenterID\tVMID\tTotalMIPS\tCost\t\t\t\tBoottime");
        int globalVMID=0;
        for(Integer datacenterID: datacentersWithVMOffers.keySet())
        {
            //JOptionPane.showMessageDialog(null, datacenterID);
            //JOptionPane.showMessageDialog(null, this.datacentersWithVMOffers.get(3).getVmOffers().size());
            LinkedHashMap<Vm, Double> datacenterVMOffers=datacentersWithVMOffers.get(datacenterID).getVmOffers();
            LinkedHashMap<Vm, Integer> datacenterVMOffersBoottimes=datacentersWithVMOffers.get(datacenterID).getVmOffersBootTime();
           
            for(Vm vm: datacenterVMOffers.keySet())
            {
                vmgidDatacenterIDMap.put(globalVMID, datacenterID);
                vmgidVMIDatDatacenterMap.put(globalVMID, vm.getId());
                double vmCost = datacenterVMOffers.get(vm);
                int vmBoottime= datacenterVMOffersBoottimes.get(vm);
                vmgidCostMap.put(globalVMID, vmCost);
                //double vmMIPS=vm.getMips();
                double vmMIPS=vm.getMips() * vm.getNumberOfPes(); //multiple MIPS by number of PEs (because it  is an VMOffer, where after VM created, it is possible to use vm.getCurrentRequestedTotalMips())
                vmgidMIPSMap.put(globalVMID, vmMIPS);
                vmgidBoottimeMap.put(globalVMID, vmBoottime);
                vmidDatacenteridVMGIDMap.put(vm.getId()+"_"+datacenterID, globalVMID);
                //JOptionPane.showMessageDialog(null, datacenterID + " " +vm.getId() + " " + vm.getMips());
                System.out.println(globalVMID + "\t"+ datacenterID + "\t\t" + vm.getId() + "\t" + vmMIPS + "\t\t" + vmCost + "\t\t" + vmBoottime);
                
                globalVMID++;
            }
        }
        System.out.println("------------------ End of VMGID Table ------------------");
        
        //Fill serviceProcessingSizeMap and serviceUserProcessingReqMap using services
        for(Service service: services)
        {
            int serviceID= service.getId();
            serviceMap.put(serviceID, service);
            double serviceDPReq=service.getDataProcessingReq();
            double userDPRateReq=service.getUserDataProcessingRateReq();
            servicesList.add(serviceID);
            serviceDPReqMap.put(serviceID, serviceDPReq);
            serviceUserDPRateReqMap.put(serviceID, userDPRateReq);
            
            //Calculate required MIPS for a service
            double DPRateReq=0.0; //whether it is serviceInputDPRateReq or serviceUserDPRateReq
            double requiredMIPS=0.0; 
            double serviceMIPSforMiniDPRate=DatacentersVMsServices.minDPRate*serviceDPReq;
            //1. check data prcoessing rate election DPRateSelection
            if(DatacentersVMsServices.DPRateSelection.equalsIgnoreCase("user"))
                DPRateReq = userDPRateReq;
            else 
                DPRateReq=getTotalSizeOfServiceInputStreamsWithMinDPUnitConsideration(serviceID);
                //DPRateReq= getTotalSizeOfServiceInputStreams(serviceID);
            
            //2. get required MIPS, where if the value is less than miniDPUNit it will be rounded to miniDPUnit or if the value is not the multiplication of miniDPRateUnit, it will be rounded to the nearest multiplication; That's meaning if the reuqired MIPS value is 4500MIPS, miniDPRate is 1000 so that the requested rate prcoess 4.5 portions and as we will consider miniDPUnit for processing stream the 4500MIPS will be rounded to 5000MIPS in order to process 5 portions
            
            requiredMIPS= DPRateReq * serviceDPReq;
                     requiredMIPS=((int)Math.ceil(requiredMIPS/serviceMIPSforMiniDPRate))*serviceMIPSforMiniDPRate; //rouind requiredMIPs to nearest MIPS which is the multiplication of (minDPUnit * serviceSize)
            
            //3. add the calculated requiredMIPS for this service to map
            serviceReqiredMIPSMap.put(serviceID, requiredMIPS);
        }
        
    }
    
    public static int getVMIDatDatacenter(int vmgid)
    {
        return vmgidVMIDatDatacenterMap.get(vmgid);
    }
    
    public static int getDatacenterID(int vmgid)
    {
        return vmgidDatacenterIDMap.get(vmgid);
    }
    
    public static double getVMCost(int vmgid)
    {
        return vmgidCostMap.get(vmgid);
    }
    
    public static double getVMTotalMIPS(int vmgid)
    {
        return vmgidMIPSMap.get(vmgid);
    }
    
    public static int getVMBoottime(int vmgid)
    {
        return vmgidBoottimeMap.get(vmgid);
    }
    
    public static ArrayList<IotDevice> getExternalSources()
    {
        return exSources;
    }
    
    public static double getServiceDPReq(int serviceID)
    {
        return serviceDPReqMap.get(serviceID);
    }
    
    public static double getServiceUserDPRateReq(int serviceID)
    {
        return serviceUserDPRateReqMap.get(serviceID);
    }
    
    public static double getServiceRequiredMIPS(int serviceID)
    {
        return serviceReqiredMIPSMap.get(serviceID);
    }
    
    public static HashMap<Integer,Service> getServiceMap()
    {
        return serviceMap;
    }
    
    public static ArrayList<Integer> getServicesList()
    {
        return servicesList;
    }
    
    public static ArrayList<Service> getServices()
    {
        return services;
    }
    
    public static Service getService(int serviceID)
    {
        return services.get(serviceID);
    }
    
    public static int getNumberOfDatacenters()
    {
        return numbOfDatacenters;
    }
    
    public static int getNumOfVMOffersAtDatacenter(int datacenterID)
    {
        return datacentersWithVMOffers.get(datacenterID).getVmOffers().size();
    }
   
    
    public static int getVMGID(String vmidDatacenterID)
    {
        return vmidDatacenteridVMGIDMap.get(vmidDatacenterID);
    }
    
    public static List<Stream> getServiceInputStreams(int serviceid) //serviceid is the same as position id
    {
        return services.get(serviceid).getStreamDependencies();
    }
    
    /*public static LinkedHashMap<Integer, Double> getServicesInOutProportion()
    {
        return servicesInOutProportion;
    }*/
    
    public static boolean IsServiceMovable(int serviceid)
    {
        for(Service service: services)
            if(service.getId()==serviceid)
                return service.isMovable();
        
        return true;
    }
    
    public static int getServicePlacementDatacenter(int serviceid)
    {
        for(Service service: services)
            if(service.getId()==serviceid)
                return service.getPlacementDatacenter();
        
        return -1;
    }
    
    
    //This method works on global attrivutes
    public static double getTotalSizeOfServiceInputStreams(int serviceid)
    {
        double totalSize=0.0;
        Service service=services.get(serviceid);

        for(Stream inStream: service.getStreamDependencies())
        {
            if(isStreamProducerEXSource(inStream))
                totalSize+=inStream.getSize();
            else //producer is service
            {
                if(inStream.getReplicaProcessing().contains(serviceid))
                {
                    //Replica processing
                    totalSize+=inStream.getSize();
                }
                else if(inStream.getPartitionProcessing().containsKey(serviceid))
                {
                    //Partition processing
                    double partitionPercentage=inStream.getPartitionProcessing().get(serviceid);
                    totalSize+=inStream.getSize()* (partitionPercentage/100);
                }
            }
        }
        return totalSize;
    }
    
    //This method works on the global attributes
    public static double getTotalSizeOfServiceOutputStreams(int serviceid)
    {
        double totalSize=0.0;
        Service service=services.get(serviceid);
        for(Stream outStream: service.getOutput())
            totalSize+=outStream.getSize();
        
        return totalSize;
    }
    
    
    
    public static boolean isStreamProducerEXSource(Stream stream)
    {
        if(stream.getTypeOfProducer().equalsIgnoreCase("exsource"))
            return true;
        else
            return false;
        /*
        //boolean returnValue=false;
        for(ExternalSource exsource: exSources)
        {
            if(id == exsource.getId())
            {
                return true;
            }
        }
        
        return false;*/
    }
    
    
    //This method works on passed service not being used any global attributes
    public static double getTotalSizeOfPassedServiceInputStreams(Service service)
    {
        double totalSize=0.0;
        int serviceid= service.getId();
        
        for(Stream inStream: service.getStreamDependencies())
        {
            //if(inStream.getId() == 13 && serviceid == 5)
              //  JOptionPane.showMessageDialog(null, inStream.getSize());
            
            if(isStreamProducerEXSource(inStream))
                totalSize+=inStream.getSize();
            else //producer is service
            {
                if(inStream.getReplicaProcessing().contains(serviceid))
                {
                    //Replica processing
                    totalSize+=inStream.getSize();
                }
                else if(inStream.getPartitionProcessing().containsKey(serviceid))
                {
                    //Partition processing
                    double partitionPercentage=inStream.getPartitionProcessing().get(serviceid);
                    totalSize+=inStream.getSize()* (partitionPercentage/100);
                }
            }
        }
        return totalSize;
    }
    
    
    static HashMap<Integer,Integer> serviceOutputPartsPreCompiated=new HashMap<>();
    static HashMap<Integer,Integer> serviceInputPartsPreCompiated=new HashMap<>();
    
    //This method recompute input and output parts and save them in global attributes (serviceOutputPartsPreCompiated and serviceInputPartsPreCompiated)
    public static void recomputeTotalSizeOfServiceInputStreamsWithMinDPUnitConsideration() //it is used after velocity change
    {
        for(int i=0;i<services.size();i++)
            getTotalSizeOfServiceInputStreamsWithMinDPUnitConsideration(services.get(i).getId());
    }
    
    //This method compute input and output parts and save them in global attributes (serviceOutputPartsPreCompiated and serviceInputPartsPreCompiated)
    //This method is called in loop by init() method, so that the global attributes will be filled-in. Thus, any subsequent will get values from these global attributed
    //Important Note: This method is intended to work where each service has one output which is defined in our problem modelling
    public static double getTotalSizeOfServiceInputStreamsWithMinDPUnitConsideration(int serviceID)
    {
        double newServiceInputStreamSize=0;
            Service ser=null;
        for(Service service: services)
            if(service.getId()==serviceID)
                ser=service;
           
        int sumInputPortions=0; //it is number of service input stream portions
        for(Stream stream:ser.getStreamDependencies())
        {
            //We need to use isStreamProducerEXSource method to avoid conflict between producer 0 and service 0 since both of them has the same start index
            if(!isStreamProducerEXSource(stream) && serviceOutputPartsPreCompiated.containsKey(stream.getProducerid()))
            {
                
                if(stream.getReplicaProcessing().contains(serviceID))
                    sumInputPortions+=serviceOutputPartsPreCompiated.get(stream.getProducerid());
                
                //if(serviceID==3)
                  //  JOptionPane.showMessageDialog(null, serviceID + " \n" + proportion);
            }
            else
            {
            	IotDevice exsource=null;
                for(IotDevice ex:exSources)
                    if(ex.getId()==stream.getProducerid())
                    {
                        exsource=ex;
                        break;
                    }

                if(exsource!=null)
                {
                    //double proportion =  ser.getOutput().get(0).getSize()/(getTotalSizeOfServiceInputStreams(serviceID));
                    double newSize= Math.ceil(exsource.getStream().getSize()/minDPRate) * minDPRate;
                    int parts= (int) Math.ceil(newSize/minDPRate);
                    sumInputPortions+=parts;
                }
                //no else is needed since it will be pre=computed
            }
        }
        

        //Calculate new service output to be stored in hashmap; it should take in to consideration the input parts and output parts as this affect the downstream size
        double proportion =  ser.getOutput().get(0).getSize()/getTotalSizeOfServiceInputStreams(serviceID);
        int OutputPortionsbasedProportion = (int) Math.ceil(((proportion * minDPRate)/minDPRate)); //get output parts according to the proportion
        int newOutputPortions= OutputPortionsbasedProportion * sumInputPortions;


        serviceOutputPartsPreCompiated.put(serviceID, newOutputPortions);

        //New service input 
        newServiceInputStreamSize = sumInputPortions * minDPRate;
        serviceInputPartsPreCompiated.put(serviceID, sumInputPortions);
        //if(serviceID==3)
        //JOptionPane.showMessageDialog(null, serviceID + "\n"+ newOutputPortions + "\n" + newServiceInputStreamSize + "\n" + proportion);
        //if(serviceID==0)
          //  JOptionPane.showMessageDialog(null, getTotalSizeOfServiceInputStreams(serviceID) + "\n" + newServiceInputStreamSize + "\n" + ser.getOutput().get(0).getSize() + "\n" + newOutputSize);
        
        
        return newServiceInputStreamSize;
    }
    
    
    public static double getStreamSizeBasedonProcessingTyep(Stream stream, int serviceID)
    {
        double returnValue=0.0;
        
        if(stream.getReplicaProcessing().contains(serviceID))
        {
            //Replica processing
            returnValue=stream.getSize();
        }
        else if(stream.getPartitionProcessing().containsKey(serviceID))
        {
            //Partition processing
            double partitionPercentage=stream.getPartitionProcessing().get(serviceID);
            returnValue= stream.getSize() * (partitionPercentage/100);
        }
        
        
        /* using atLeastMinDPRate method
        //check if stream size at least miniDPRate
        if(returnValue>=minDPRate)
            return returnValue;
        else
            return minDPRate;*/
        
        return atLeastMinDPRate(returnValue);
    }
    
    private static double atLeastMinDPRate(double streamSize)
    {   
        //originalStreamID is only for printing purpose
        
        double  correctSize=streamSize;
        if(correctSize<minDPRate)
            correctSize=minDPRate;
            //System.out.println("-Note: the size of remain portion for stream#" + originalStreamID + " is " + StreamSize + ", which is less than miniDPUnit, so that the minDPUnit is considered ");
        
        return correctSize;
    }


    public static double getMinDPRate() {
        return minDPRate;
    }

    public static String getDPRateSelelction() {
        return DPRateSelection;
    }    
   
    public static int getDatacenterStartIndexinCloudSim()
    {
        return datacenterStartIndexinCloudSim;
    }
    
    public static void assignServicesInOutProportion(LinkedHashMap<Integer, Double> par)
    {
        servicesInOutProportion=par;
    }
    
   
}