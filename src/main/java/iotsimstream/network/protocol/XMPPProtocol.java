package iotsimstream.network.protocol;

import iotsimstream.edge.Architecture;
import iotsimstream.edge.CommonCommuniProtocol;
import iotsimstream.edge.QoS;
import iotsimstream.edge.Synchronism;

public class XMPPProtocol extends CommonCommuniProtocol {
	private static final float TRANSIMISON_SPEED = 3.00f;

	public XMPPProtocol() {

		super(5222, new TransportProtocol[] { TransportProtocol.TCP },
				new SecurityProtocol[] { SecurityProtocol.TLS }, 0, Integer.MAX_VALUE,
				new Architecture[] { Architecture.REQ_RSP, Architecture.PUB_SUB }, "utf-8", "XMPP",
				new Synchronism[] { Synchronism.ASYN }, new QoS[] { QoS.UNKNOWN },
				TRANSIMISON_SPEED);

	}

}