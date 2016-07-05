package com.davstele.sequencers;

import com.davstele.models.Sequence;
import org.pcap4j.core.PcapHandle;
import org.pcap4j.core.PcapNativeException;
import org.pcap4j.core.Pcaps;
import org.pcap4j.packet.Packet;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dx on 5/19/16.
 */
public class BinarySequencer implements ISequencer {

    Sequence sequence;

    public BinarySequencer() {
        sequence = new Sequence();
    }

    public void processFile(File file) throws Exception {

        PcapHandle handle;
        try {
            handle = Pcaps.openOffline(file.getAbsolutePath(), PcapHandle.TimestampPrecision.NANO);
        } catch (PcapNativeException e) {
            handle = Pcaps.openOffline(file.getAbsolutePath());
        }

        Packet packet = handle.getNextPacket();
        while(packet != null) {
            sequence.addPacket(packet);

            packet = handle.getNextPacket();
        }

        handle.close();
    }

    public List<Sequence> getSequences() {
        List<Sequence> sequences = new ArrayList<Sequence>();
        sequences.add(sequence);
        return sequences;
    }
}
