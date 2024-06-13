package iotsimstream.edge;

import org.cloudbus.cloudsim.CloudletScheduler;

import iotsimstream.SVM;

public class EdgeSVM extends SVM {

	// type could be "filtering" or "statistics"
	private String type;

	public EdgeSVM(String type,int id, int userId, double mips, int numberOfPes, int ram, long bw, long size, String vmm, CloudletScheduler cloudletScheduler){
		super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
		this.type =type;

	}

}
