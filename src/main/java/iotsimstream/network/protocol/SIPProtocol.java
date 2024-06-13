package iotsimstream.network.protocol;

import iotsimstream.edge.Architecture;
import iotsimstream.edge.CommonCommuniProtocol;
import iotsimstream.edge.QoS;
import iotsimstream.edge.Synchronism;

public class SIPProtocol extends CommonCommuniProtocol {
	private static final float TRANSIMISON_SPEED = 1.00f;

	public SIPProtocol() {

		super(443, new TransportProtocol[] { TransportProtocol.TCP, TransportProtocol.UDP, TransportProtocol.SCTP },
				new SecurityProtocol[] { SecurityProtocol.TLS, SecurityProtocol.SRTP }, 0, Integer.MAX_VALUE,
				new Architecture[] { Architecture.REQ_RSP }, "utf-8", "https",
				new Synchronism[] { Synchronism.ASYN, Synchronism.SYN }, new QoS[] { QoS.AMO, QoS.EO, QoS.ALO },
				TRANSIMISON_SPEED);

	}

}
