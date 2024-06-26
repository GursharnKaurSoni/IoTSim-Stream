package iotsimstream.edge;

public enum QoS {
	/**
	 * at most once
	 */
    AMO,
    /**
     * at least once
     */
    ALO,
    /**
     * exactly once 
     */
    EO,
    /**
     * the feature of CoAP
     * 1.Confirmable: need ack 
	 *	Non-Confirmable: don’t need ack 
	 *	Acknowledgment: confirms the reception of a confirmable message
	 *	Reset: conform the reception of a message not being processed
     */
    CONFIRMABLE,
    /**
     *  no information is found
     * 
     */
    UNKNOWN,
    /**
     * Some loss of messages is acceptable as long as the majority are delivered.
     */
    LOSS_TOLERANT,
    /**
    * Guarantees delivery with redundancy to ensure messages are received even in the presence of network failures.
    */
   HIGH_RELIABILITY
}
