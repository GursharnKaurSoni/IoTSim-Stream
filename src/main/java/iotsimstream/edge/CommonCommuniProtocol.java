package iotsimstream.edge;

import iotsimstream.network.protocol.SecurityProtocol;
import iotsimstream.network.protocol.TransportProtocol;

public abstract class CommonCommuniProtocol implements CommunicationProtocol {
	

	protected int runningPort;
	protected TransportProtocol[] transportPortProtocol;
	protected SecurityProtocol[] securityProtocol;
	protected int headSize;
	protected long maxMessageSize;
	protected Architecture[] architecture;
	protected String encoding;
	protected String name;
	protected Synchronism[] synchronism;
	protected QoS[] qos;
	protected float batteryConsumptionSpeed;
	protected float transmissionSpeed;

	public CommonCommuniProtocol(int runningPort, TransportProtocol[] transportPortProtocol,
			SecurityProtocol[] securityProtocol, int headSize, long maxMessageSize, Architecture[] architecture,
			String encoding, String name, Synchronism[] synchronism, QoS[] qos,
			float transmissionSpeed) {
		super();
		this.runningPort = runningPort;
		this.transportPortProtocol = transportPortProtocol;
		this.securityProtocol = securityProtocol;
		this.headSize = headSize;
		this.maxMessageSize = maxMessageSize;
		this.architecture = architecture;
		this.encoding = encoding;
		this.name = name;
		this.synchronism = synchronism;
		this.qos = qos;
		this.transmissionSpeed = transmissionSpeed;
	}

	public TransportProtocol[] getSupportedTransPortProtocol() {
		return transportPortProtocol;
	}

	public Architecture[] getSupportedArchitecture() {
		return architecture;
	}

	public String getEncoding() {
		return "utf-8";
	}

	@Override
	public SecurityProtocol[] getSupportedSecurityProtocol() {
		return securityProtocol;
	}

	@Override
	public float getTransmissionSpeed() {
		return transmissionSpeed;
	}

	@Override
	public float getBatteryDrainageRate() {
		return batteryConsumptionSpeed;
	}

	@Override
	public int getHeadSize() {
		return headSize;
	}

	@Override
	public long getMaxMessageSize() {
		return maxMessageSize;
	}

	@Override
	public String getProtocolName() {

		return this.getClass().getSimpleName().substring(0, this.getClass().getSimpleName().lastIndexOf('P'));
	}

	@Override
	public int getRunningPort() {
		return runningPort;
	}

	@Override
	public Synchronism[] getSupportedSynchronism() {
		return synchronism;
	}

	@Override
	public QoS[] getSupportedQoS() {
		return qos;
	}

}