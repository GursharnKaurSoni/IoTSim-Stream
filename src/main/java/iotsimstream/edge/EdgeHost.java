package iotsimstream.edge;

import java.util.List;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.VmScheduler;
import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;

/**
 * A EdgeHost is edge computing node or edge servers use for computing resources located closer to the data source or end-user
 *
 * @author Gursharn Soni
 */

public class EdgeHost extends Host{
	
	/**
	 * RASPBERRY_PI, SMART_ROUTER, UDOO_BOARD, MOBILE_PHONE,
	 */
	String edgeType;
	

	public EdgeHost(String edgeType, int id, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, long storage,
			List<? extends Pe> peList, VmScheduler vmScheduler) {
		super(id, ramProvisioner, bwProvisioner, storage, peList, vmScheduler);
		this.edgeType = edgeType;
	}

}
