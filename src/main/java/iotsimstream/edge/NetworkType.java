package iotsimstream.edge;

/**
 * This class describes the link layer protocol that is used by IoT devices to communicate with the Edge devices.
 */
public enum NetworkType {

	WIFI(5d),
	WLAN(5d),
	FourG(4d),
	ThreeG(2d),
	BLUETOOTH(2d),
	LAN(3),
	Zigbee(0.5d);

	private double value;

	private NetworkType(double value) {
		this.value = value;
	}

	public double getSpeedRate() {
		return value;
	}
}