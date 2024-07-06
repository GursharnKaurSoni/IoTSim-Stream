package iotsimstream.vmOffers;

import java.util.LinkedHashMap;

/**
 * This class provide different VM options offered by a particular Edge provider
 *
 * @author Gursharn Soni
 */

import org.cloudbus.cloudsim.Vm;

import iotsimstream.Properties;

public class VMOffersEdgeDatacenter2  extends VMOffers{
	
	double baseMips=Double.parseDouble(Properties.EDGE_MIPS_PERCORE.getProperty(1));
        long vmBw=1000;
	
	@Override
	public LinkedHashMap<Vm, Double> getVmOffers() {
		
		if(vmOffersTable.size()==0)
                {
                    //Note that price is in cents per second
                    vmOffersTable.put(new Vm(0,0, baseMips,2, 2048,vmBw,  6092,"",null), (double)  ((0.08*100)/3600)); //Small
                    vmOffersTable.put(new Vm(1,0, baseMips,4, 4096,vmBw,  16432,"",null), (double)  ((0.35*100)/3600)); //Medium
                    vmOffersTable.put(new Vm(2,0, baseMips,6, 6144,vmBw,  34716,"",null), (double)  ((0.65*100)/3600)); //Large
                    vmOffersTable.put(new Vm(3,0, baseMips,9, 9216,vmBw,  57632,"",null), (double)  ((0.90*100)/3600)); //XLarge
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
