package iotsimstream.network.protocol;

import iotsimstream.edge.Architecture;
import iotsimstream.edge.CommonCommuniProtocol;
import iotsimstream.edge.QoS;
import iotsimstream.edge.Synchronism;

/**
 * MSRP is used for transmitting a series of related instant messages in the context of a communication session. 
 * It is often used in combination with SIP to provide messaging services alongside voice and video calls
 * @author  Gursharn Soni
 */
public class MessageSessionRelayProtocol extends CommonCommuniProtocol {
	private static final float TRANSIMISON_SPEED = 1.00f;

	public MessageSessionRelayProtocol() {

		super(443, new TransportProtocol[] { TransportProtocol.TCP, TransportProtocol.UDP, TransportProtocol.SCTP },
				new SecurityProtocol[] { SecurityProtocol.TLS, SecurityProtocol.SRTP }, 0, Integer.MAX_VALUE,
				new Architecture[] { Architecture.REQ_RSP }, "utf-8", "https",
				new Synchronism[] { Synchronism.ASYN, Synchronism.SYN }, new QoS[] { QoS.AMO, QoS.EO, QoS.LOSS_TOLERANT },
				TRANSIMISON_SPEED);

	}

}
