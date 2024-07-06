package iotsimstream.vmOffers;

import java.util.LinkedHashMap;

/**
 * This class provide different VM options offered by a particular Edge provider
 *
 * @author Gursharn Soni
 */

import org.cloudbus.cloudsim.Vm;

import iotsimstream.Properties;
	
public class VMOffersEdgeDatacenter1  extends VMOffers{
	
	double baseMips=Double.parseDouble(Properties.EDGE_MIPS_PERCORE.getProperty(1));
        long vmBw=1000;
	
	@Override
	public LinkedHashMap<Vm, Double> getVmOffers() {
		
		if(vmOffersTable.size()==0)
                {
                    //Note that price is in cents per second
                    vmOffersTable.put(new Vm(0,0, baseMips,1, 1024,vmBw,  8192,"",null), (double)  ((0.05*100)/3600)); //Small
                    vmOffersTable.put(new Vm(1,0, baseMips,2,  4096,vmBw,  18432,"",null), (double)  ((0.22*100)/3600)); //Medium
                    vmOffersTable.put(new Vm(2,0, baseMips,4,  8192,vmBw,  34816,"",null), (double)  ((0.34*100)/3600)); //Large
                    vmOffersTable.put(new Vm(3,0, baseMips,9,  32678,vmBw,  69632,"",null), (double)  ((0.85*100)/3600)); //XLarge
                }
                
		return vmOffersTable;
	}

        
        @Override
        public double getCost(double mips, int pes, int memory, long bw) {
            for(Vm vm: vmOffersTable.keySet())
            {
                if(vm.getMips() == mips
                        && vm.getNumberOfPes() == pes
                        && vm.getRam() == memory
                        && vm.getBw() == bw
                        )
                    return vmOffersTable.get(vm);
            }
            
            return 0;
            
        }
        
	@Override
	public long getBootTime() {
		return Long.parseLong(Properties.EDGE_VM_DELAY.getProperty());
	}
	
	@Override
	public LinkedHashMap<Vm,Integer> getVmOffersBootTime() {
		LinkedHashMap<Vm, Integer> vmOffersBootTimeTable= new LinkedHashMap<Vm, Integer>();
		for(Vm vm: vmOffersTable.keySet())
        {
            int boottime= 0;
            vmOffersBootTimeTable.put(vm, boottime);
		}
		return vmOffersBootTimeTable;
	}

}
