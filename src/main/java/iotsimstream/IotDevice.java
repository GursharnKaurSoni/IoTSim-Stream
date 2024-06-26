package iotsimstream;

import java.util.HashMap;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

import iotsimstream.edge.CommunicationProtocol;
import iotsimstream.edge.EdgeDataCenter;
import iotsimstream.edge.IotType;
import iotsimstream.edge.NetworkType;
import iotsimstream.network.protocol.MessageSessionRelayProtocol;
import iotsimstream.network.protocol.NetworkDelayCalculationPolicy;
import iotsimstream.network.protocol.SIPProtocol;
import iotsimstream.network.protocol.SimpleNetworkDelayCalculator;
import iotsimstream.network.protocol.XMPPProtocol;

/**
 *
 * @author Mutaz Barika
 * <p> Modified by: Gursharn Soni </p>
 */
public class IotDevice extends SimEntity {

    
    public static final int SEND_STREAM = 111111;
    public static final int STOP_SENDING_STREAM = 111112;
    Stream stream;
    double datarate; //in MB/s
    boolean stopSendingStream;
    private NetworkDelayCalculationPolicy networkDelayCalculationPolicy;
    private String networkType;
    private String communicationProtocol;

    
    
	public IotDevice(IotType type, String name, int streamid, int ownerId, double datarate, String networkType,
			String communicationProtocol) {
		super(name);
		this.datarate = datarate;
		stream = new Stream(streamid, ownerId, getId(), "exsource", this.datarate);
		stopSendingStream = false;
		this.networkType = networkType;
		this.communicationProtocol = communicationProtocol;
	}

	private NetworkType getNetworkType(String networkType) {

		NetworkType netType;
		String lowerNetworkType = networkType.toLowerCase();
		switch (lowerNetworkType) {
		case "wifi":
			netType = NetworkType.WIFI;
			break;
		case "wlan":
			netType = NetworkType.WLAN;
			break;
		case "4g":
			netType = NetworkType.FourG;
			break;
		case "3g":
			netType = NetworkType.ThreeG;
			break;
		case "bluetooth":
			netType = NetworkType.BLUETOOTH;
			break;
		case "lan":
			netType = NetworkType.LAN;
			break;
		case "zigbee":
			netType = NetworkType.Zigbee;	
		default:
			System.out.println(networkType + " networktype has not been supported yet!");
			return null;
		}
		return netType;
	}

	private CommunicationProtocol getCommunicationProtocol(String communicationProtocol) {

		CommunicationProtocol commnProtocol;
		String lowerCommunicationProtocol = communicationProtocol.toLowerCase();
		switch (lowerCommunicationProtocol) {
		case "xmpp":
			commnProtocol = new XMPPProtocol();
			break;
		case "sip":
			commnProtocol = new SIPProtocol();
			break;
		case "msrp":
			commnProtocol = new MessageSessionRelayProtocol();
			break;
		default:
			System.out.println(communicationProtocol + " protocol has not been supported yet!");
			return null;
		}
		return commnProtocol;
	}

	private double getNetworkDelay(String networkType, String communicationProtocol) {
		NetworkDelayCalculationPolicy networkDelayCalculationPolicy = new SimpleNetworkDelayCalculator();
		double networkDelay = networkDelayCalculationPolicy.getNetworkDelay(getNetworkType(networkType),
				getCommunicationProtocol(communicationProtocol));
		return networkDelay;

	}


    @Override
    public void startEntity() {
        System.out.println(getName() + " is starting...");
        Log.printConcatLine(getName(), " is starting...");
    }

    @Override
    public void processEvent(SimEvent ev) {
        
        if (ev == null){
                Log.printLine("Warning: "+CloudSim.clock()+": "+this.getName()+": Null event ignored.");
        } else {
                int tag = ev.getTag();
                switch(tag){
                        case SEND_STREAM: processSendStream(ev); break;
                        case STOP_SENDING_STREAM: processStopSendingStream(); break;
                        case CloudSimTags.END_OF_SIMULATION: break;
                        default: Log.printLine("Warning: "+CloudSim.clock()+": "+this.getName()+": Unknown event ignored. Tag: "+tag);
                }
        }
    }

    @Override
    public void shutdownEntity() {
        Log.printConcatLine(getName(), " is shutting down...");
    }
    
    private void processSendStream(SimEvent ev)
    {
        if( !stopSendingStream )
        {
            //Update the generation time of stream befroe sending
            stream.setStreamTime(CloudSim.clock());
            
            HashMap<Stream, Integer> streamPortionsVMMap= StreamSchedulingOnSVMs.getStreamPortionsSchedulingOnVMs(stream);
            
            for(Stream streamPortion: streamPortionsVMMap.keySet())
            {
                int vmid=streamPortionsVMMap.get(streamPortion);
                int datacenterid = StreamSchedulingOnSVMs.getDatacenterID(vmid);
                Object[] data=new Object[2];
                data[0]=vmid;
                data[1]=streamPortion; //stream portion
                
                //Send stream portion now
                send(datacenterid,getNetworkDelay(networkType, communicationProtocol),EdgeDataCenter.EXSOURCE_STREAM, data);
            }
            
            //Schedule next send
            schedule(getId(), 1, SEND_STREAM);
        }
    }
    
    private void processStopSendingStream()
    {
        stopSendingStream=true;
    }

    public Stream getStream() {
        return stream;
    }
    
    public double getDatarate() {
        return datarate;
    }
    
}
