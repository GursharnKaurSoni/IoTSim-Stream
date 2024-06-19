package iotsimstream.edge;

import iotsimstream.IotDevice;

/**
 * This class extends Iot Device and specifies the type of IoT device it represents.
 * 
 * @author Gursharn Kaur
 */
public class AVSensor extends IotDevice {

	private String networkType;
	private String communicationProtocol;
	private String iotClassName;
	public static final double DATA_FREQUENCY=2;
	public static final double DATA_GENERATION_TIME=1;
	public static final int COMPLEXITY_OF_DATAPACKAGE=1;
	public static final int DATA_SIZE=2;

	public AVSensor(String name, int streamid, int ownerId, double datarate,String networkType, String communicationProtocol) {
		super(IoTType.CAR_SENSOR, name, streamid, ownerId, datarate, networkType, communicationProtocol);
	}

	public String getNetworkType() {
		return networkType;
	}

	public String getCommunicationProtocol() {
		return communicationProtocol;
	}

	public String getIotClassName() {
		return iotClassName;
	}
}
