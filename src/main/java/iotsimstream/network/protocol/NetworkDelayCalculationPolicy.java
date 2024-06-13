package iotsimstream.network.protocol;

import iotsimstream.edge.CommunicationProtocol;
import iotsimstream.edge.NetworkType;

public interface NetworkDelayCalculationPolicy {

	public double getNetworkDelay(NetworkType networkType, CommunicationProtocol communProtocol);

}
