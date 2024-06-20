package iotsimstream.network.protocol;

import iotsimstream.edge.CommunicationProtocol;
import iotsimstream.edge.NetworkModel;
import iotsimstream.edge.NetworkType;
 /**
  * * <p> Modified by: Gursharn Soni </p>
  */
public class SimpleNetworkDelayCalculator implements NetworkDelayCalculationPolicy {

	@Override
	public double getNetworkDelay(NetworkType networkType, CommunicationProtocol commnProtocol) {
		double cloudletLength = 1.0;
		double distanceDelay = 0;

		float transmissionSpeed = commnProtocol.getTransmissionSpeed();
		double speedRate = networkType.getSpeedRate();

		double minSpeed = Math.min(transmissionSpeed, speedRate);
		cloudletLength = (long) minSpeed;
		double delay = cloudletLength / minSpeed;

		return delay + distanceDelay;
	}

}
