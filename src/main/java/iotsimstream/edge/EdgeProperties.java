package iotsimstream.edge;

import iotsimstream.Configuration;

public enum EdgeProperties {

	SIMULATION_TIME("edge.simulation.time"), 
	SCHEDULING_POLICY("edge.scheduling.policy"),
    EDGE_HOST_TYPE("edge.host.type"),
	EDGE_HOST_LOCATION_X("edge.host.location.x"), 
	EDGE_HOST_LOCATION_Y("edge.host.location.y"),
	EDGE_HOST_LOCATION_Z("edge.host.location.z"),
	EDGE_VM_TYPE("edge.vm.type"),
	EDGE_HOST("edge.hosts"),
	EDGE_HOST_CORES("edge.host.cores"),
	EDGE_MIPS_PERCORE("edge.host.cores.mips"),
	EDGE_HOST_STORAGE("edge.host.storage"),
	EDGE_DATACENTER_NAME("edge.datacenter.name"),
	EDGE_SCHEDULING_INTERVAL("edge.scheduling.interval"),
	EDGE_HOST_MEMORY("edge.host.memory"),
	EDGE_DATACENTER_PROXIMITY("edge.datacenter.proximity"),
	EDGE_VM_DELAY("edge.vm.delay"),
	EDGE_EXTERNAL_BANDWIDTH("edge.external.bandwidth"),
	EDGE_EXTERNAL_LATENCY("edge.external.latency"),
	VM_OFFERS("edge.vm.offers"),

	;

	private String key;
	private Configuration configuration = Configuration.INSTANCE;

	EdgeProperties(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}

	public String getProperty() {
		return configuration.getProperty(this.key);
	}

	public String getProperty(int edgeDS) {
		return configuration.getProperty(this.key + "#" + edgeDS);
	}
	public void setProperty(String value) {
		configuration.setProperty(this.key, value);
	}
}
