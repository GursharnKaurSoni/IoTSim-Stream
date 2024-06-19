package iotsimstream.schedulingPolicies;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;

import iotsimstream.ProvisionedSVm;
import iotsimstream.SVM;
import iotsimstream.Service;
import iotsimstream.ServiceCloudlet;
import iotsimstream.ServiceCloudletSchedulerSpaceShared;
import iotsimstream.Stream;
import iotsimstream.edge.EdgeSVM;
import iotsimstream.vmOffers.VMOffers;

/**
 * This class implements a scheduling policy that first randomly selects a datacenter, and then randomly chooses a VM offering provided by the selected datacenter.
 *
 * @author Gursharn Kaur
 */

public class CloudEdgeSchedulingPolicy extends Policy {

	List<Vm> vmOffersList;

	int vmId = 0;

	long provisioningSeed = 1310320;
	
	long randomVMSelectionSeed= 1310234;

	@Override
	public void doScheduling(LinkedHashMap<Integer, VMOffers> datacentersWithVMOffers) {
		Enumeration<Stream> dataIter = dataItems.elements();
		while (dataIter.hasMoreElements()) {
			Stream item = dataIter.nextElement();
			streamRequiredLocation.put(item.getId(), new HashSet<Integer>());
		}
		LinkedHashMap<Integer, ArrayList<Integer>> ServiceVMsMap = new LinkedHashMap<>();
		LinkedHashMap<Integer, Integer> ServiceSelectedDatacenterMap = new LinkedHashMap<>();

		Random generator = new Random(provisioningSeed);
		VMOffers vmOffers = null;
	
		for (Service service : services) {
			Integer serviceID = service.getId();

			int datacenterStartIndex = 2;
			ArrayList<Integer> selectedVMs = new ArrayList<Integer>();
			double requiredMIPS = 0;
			if (service.getUserDataProcessingRateReq() != -1) {
				double totalIn = service.getTotalSizeOfServiceInputStreams();
				if (service.getUserDataProcessingRateReq() >= totalIn)
					requiredMIPS = service.getUserDataProcessingRateReq() * service.getDataProcessingReq();
				else
					requiredMIPS = totalIn * service.getDataProcessingReq();

			} else
				requiredMIPS = service.getTotalSizeOfServiceInputStreams() * service.getDataProcessingReq();

			int randomDatacenter = generator.nextInt(datacentersWithVMOffers.size());

			int selectedDatacenterID = datacenterStartIndex + randomDatacenter;

			ServiceSelectedDatacenterMap.put(serviceID, selectedDatacenterID);

			vmOffers = datacentersWithVMOffers.get(selectedDatacenterID);
			Set setVms = vmOffers.getVmOffers().keySet();

			ArrayList<Vm> vms = new ArrayList<Vm>(setVms);
			boolean toProvisionVM = false;

			Random randomVM = new Random(randomVMSelectionSeed);
			if (!vms.isEmpty()) {

				for (int i = 0; i < vms.size(); i++) {
					int randomIndex = randomVM.nextInt(vms.size());
					Vm randomVm = vms.get(randomIndex);
					double vmMIPS = randomVm.getMips() * randomVm.getNumberOfPes();

					if (vmMIPS >= requiredMIPS) {
						toProvisionVM = true;
					}
					if (toProvisionVM) {
						selectedVMs.add(randomVm.getId());
			    		requiredMIPS = requiredMIPS - vmMIPS;
						toProvisionVM = false;
					}
					if (requiredMIPS <= 0)
						break;

				}
			} else {
				Log.print("The vms list is empty");
			}

			if (selectedVMs.isEmpty()) {
				JOptionPane.showMessageDialog(null,
						"IoTSim-Stream will be terminated because provisioning VM(s) for Service " + serviceID
								+ " is not possible with available VM offer(s)\n"
								+ "Reason: there is no VM offer in selected cloud-edge based datacenter that achieves the required MIPS for processing one stream unit (i.e. "
								+ (minDPUnit * service.getDataProcessingReq()) + " MIPS)");
				Log.print("IoTSim-Stream is terminated because provisioning VM(s) for Service " + serviceID
						+ " is not possible with available VM offer(s)\nReason: "
						+ "there is no VM offer in selected cloud-edge based  datacenter that achieves the required MIPS for processing one stream unit (i.e. "
						+ (minDPUnit * service.getDataProcessingReq()) + " MIPS)");
				System.out.println("IoTSim-Stream is terminated because provisioning VM(s) for Service " + serviceID
						+ " is not possible with available VM offer(s)\nReason: "
						+ "there is no VM offer in selected cloud-edge based datacenter that achieves the required MIPS for processing one stream unit (i.e. "
						+ (minDPUnit * service.getDataProcessingReq()) + " MIPS)");
				System.exit(0);
			}
		

			ServiceVMsMap.put(serviceID, selectedVMs);
		}

		for (Integer serviceID : ServiceVMsMap.keySet()) {
			Service service = services.get(serviceID);
			double serviceSize = service.getDataProcessingReq();

			ArrayList<Integer> vmidList = new ArrayList<Integer>();
			int placementDatacenterID = ServiceSelectedDatacenterMap.get(serviceID);

			for (Integer vmid : ServiceVMsMap.get(serviceID))

			{
				Vm instance = datacentersWithVMOffers.get(placementDatacenterID).getVM(vmid);
				double vmCost = datacentersWithVMOffers.get(placementDatacenterID).getVmOffers().get(instance);
				vmOffers = datacentersWithVMOffers.get(placementDatacenterID);
				if (vmOffers.getClass().toString().contains("Edge")) {
					EdgeSVM edgeSVM = new EdgeSVM("RasberryPI", vmId, ownerId, instance.getMips(),
							instance.getNumberOfPes(), instance.getRam(), instance.getBw(), instance.getSize(),
							"", new ServiceCloudletSchedulerSpaceShared());
					provisionAndAddCloudlet(edgeSVM, vmCost, placementDatacenterID, serviceSize, ownerId,
							serviceID, service, vmidList);
				} else {
					SVM svm = new SVM(vmId, ownerId, instance.getMips(), instance.getNumberOfPes(),
							instance.getRam(), instance.getBw(), instance.getSize(), "",
							new ServiceCloudletSchedulerSpaceShared());
					provisionAndAddCloudlet(svm, vmCost, placementDatacenterID, serviceSize, ownerId, serviceID,
							service, vmidList);
				}
				
			}

			schedulingTable.put(service.getId(), vmidList);

			for (Stream stream : service.getStreamDependencies()) {
				if (!streamRequiredLocation.containsKey(stream.getId())) {
					streamRequiredLocation.put(stream.getId(), new HashSet<Integer>());
				}
				streamRequiredLocation.get(stream.getId()).add(service.getId());

				for (ServiceCloudlet cloudlet : service.getServiceCloudlets())
					cloudlet.addRequiredInputStream(stream);
			}

			for (Stream stream : service.getOutput()) {
				if (!streamRequiredLocation.containsKey(stream.getId())) {
					streamRequiredLocation.put(stream.getId(), new HashSet<Integer>());
				}

				for (ServiceCloudlet cloudlet : service.getServiceCloudlets())
					cloudlet.addProducedOutputStream(stream);
			}
		}
	}
	

	
	private void provisionAndAddCloudlet(SVM svm, double vmCost, int placementDatacenterID, Double serviceSize, int ownerId, int serviceID, Service service, ArrayList<Integer> vmidList) {
		provisioningInfo.add(new ProvisionedSVm(svm, 0, 0, vmCost, placementDatacenterID));
		ServiceCloudlet cl = new ServiceCloudlet(cloudletCont, 1, svm.getNumberOfPes(), 0, 0,
				new UtilizationModelFull(), new UtilizationModelFull(), new UtilizationModelFull(),
				serviceSize, ownerId, serviceID);
		cl.setVmId(svm.getId());
		service.addCloudlet(cl);
		vmidList.add(svm.getId());
		cloudletCont++;
		vmId++;
}
}
