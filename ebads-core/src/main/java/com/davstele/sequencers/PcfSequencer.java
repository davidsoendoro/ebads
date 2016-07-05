package com.davstele.sequencers;

import com.davstele.models.Sequence;
import com.davstele.packets.PcfPacket;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dx on 5/19/16.
 */
public class PcfSequencer implements ISequencer {

    Map<String, Sequence> sequences;

    public PcfSequencer() {
        sequences = new HashMap<String, Sequence>();
    }

    /**
     * One PCF sequence is one integration cycle and one membership_new
     * @param file
     */
    public void processFile(File file) throws Exception{
        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(file.getAbsolutePath(), PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException e) {
            handle = Pcaps.openOffline(file.getAbsolutePath());
        }

        Packet packet = handle.getNextPacket();
        while(packet != null) {
            PcfPacket.Builder builder = new PcfPacket.Builder(packet.getPayload().getRawData());
            PcfPacket pcfPacket = builder.build();

            String sequenceKey = pcfPacket.getHeader().getIntegrationCycle() + "," +
                    pcfPacket.getHeader().getMembershipNew();

            Sequence sequence = sequences.get(sequenceKey);
            if(sequence == null) {
                sequence = new Sequence();
            }
            sequence.addPacket(pcfPacket);

            sequences.put(sequenceKey, sequence);
            packet = handle.getNextPacket();
        }

        handle.close();
    }

    public List<Sequence> getSequences() {
        return new ArrayList<Sequence>(sequences.values());
    }
}
