package iotsimstream.network.protocol;

import iotsimstream.edge.Architecture;
import iotsimstream.edge.CommonCommuniProtocol;
import iotsimstream.edge.QoS;
import iotsimstream.edge.Synchronism;

/**
 * Session Initiation Protocol (SIP) is signaling protocol used for initiating, maintaining, modifying, and terminating real-time
 * sessions that involve video,voice, messaging and other communications application
 * @author  Gursharn Soni
 */
public class SIPProtocol extends CommonCommuniProtocol {
	private static final float TRANSIMISON_SPEED = 1.00f;

	public SIPProtocol() {

		super(443, new TransportProtocol[] { TransportProtocol.TCP, TransportProtocol.UDP },
				new SecurityProtocol[] { SecurityProtocol.TLS, SecurityProtocol.SRTP }, 0, Integer.MAX_VALUE,
				new Architecture[] { Architecture.REQ_RSP , Architecture.PUB_SUB}, "utf-8", "https",
				new Synchronism[] { Synchronism.ASYN, Synchronism.SYN }, new QoS[] { QoS.ALO, QoS.EO, QoS.HIGH_RELIABILITY, QoS.LOSS_TOLERANT },
				TRANSIMISON_SPEED);

	}

}
