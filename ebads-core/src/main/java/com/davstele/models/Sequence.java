package com.davstele.models;

import org.pcap4j.packet.Packet;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dx on 5/19/16.
 */
public class Sequence {
    List<Packet> packets;
    private byte[] allBytes;

    public Sequence() {
        packets = new ArrayList<Packet>();
    }

    public void addPacket(Packet packet) {
        packets.add(packet);
    }

    public byte[] getAllBytes() {
        int totalSize = 0;
        for(Packet packet : packets) {
            totalSize += packet.getRawData().length;
        }
        byte[] allBytes = new byte[totalSize];
        ByteBuffer buffer = ByteBuffer.wrap(allBytes);
        for(Packet packet : packets) {
            buffer.put(packet.getRawData());
        }
        return allBytes;
    }
}
