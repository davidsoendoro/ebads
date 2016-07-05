package com.davstele.packets;

import org.pcap4j.packet.*;
import org.pcap4j.util.ByteArrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dx on 4/24/16.
 */
public class PcfPacket extends AbstractPacket {
    private static final Logger logger = LoggerFactory.getLogger(IpV4Packet.class);
    private final PcfPacket.PcfHeader header;

    private PcfPacket(PcfPacket.Builder builder) {
        if(builder == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("builder: ").append(builder).
                    append(" builder.integrationCycle: ").append(builder.integrationCycle).
                    append(" builder.membershipNew: ").append(builder.membershipNew).
                    append(" builder.syncPriority: ").append(builder.syncPriority).
                    append(" builder.syncDomain: ").append(builder.syncDomain).
                    append(" builder.transparentClock: ").append(builder.transparentClock);
            throw new NullPointerException(sb.toString());
        }

        this.header = new PcfPacket.PcfHeader(builder);
    }

    @Override
    public PcfHeader getHeader() {
        return header;
    }

    public Builder getBuilder() {
        return new PcfPacket.Builder(this);
    }

    public static final class PcfHeader extends AbstractHeader {

        private int integrationCycle;
        private int membershipNew;
        private byte syncPriority;
        private byte syncDomain;
        private byte syncType;
        private long transparentClock;
        private final short headerChecksum;
        private final byte[] padding;

        private PcfHeader(PcfPacket.Builder builder) {
            this.integrationCycle = builder.integrationCycle;
            this.membershipNew = builder.membershipNew;
            this.syncPriority = builder.syncPriority;
            this.syncDomain = builder.syncDomain;
            this.syncType = builder.syncType;
            this.transparentClock= builder.transparentClock;

            if(builder.paddingAtBuild) {
                int mod = this.measureLengthWithoutPadding() % 4;
                if(mod != 0) {
                    this.padding = new byte[4 - mod];
                } else {
                    this.padding = new byte[0];
                }
            } else if(builder.padding != null) {
                this.padding = new byte[builder.padding.length];
                System.arraycopy(builder.padding, 0, this.padding, 0, this.padding.length);
            } else {
                this.padding = new byte[0];
            }

            if(builder.correctLengthAtBuild) {
            } else {
            }

            if(builder.correctChecksumAtBuild) {
                if (PacketPropertiesLoader.getInstance().ipV4CalcChecksum()) {
                    headerChecksum = calcHeaderChecksum();
                }
                else {
                    headerChecksum = (short)0;
                }
            } else {
                this.headerChecksum = builder.headerChecksum;
            }
        }

        private short calcHeaderChecksum() {
            // If call getRawData() here, rawData will be cached with
            // an invalid checksum in some cases.
            // To avoid it, use buildRawData() instead.
            byte[] data = buildRawData();

            return ByteArrays.calcChecksum(data);
        }

        @Override
        protected int calcLength() {
            int len = measureLengthWithoutPadding() + padding.length;
            return len;
        }

        private int measureLengthWithoutPadding() {
            int len = 0;

            return len + 28;
        }

        protected List<byte[]> getRawFields() {
            List<byte[]> rawFields = new ArrayList<byte[]>();
            rawFields.add(ByteArrays.toByteArray(this.integrationCycle));
            rawFields.add(ByteArrays.toByteArray(this.membershipNew));
            rawFields.add(ByteArrays.toByteArray(0));
            rawFields.add(ByteArrays.toByteArray(this.syncPriority));
            rawFields.add(ByteArrays.toByteArray(this.syncDomain));
            rawFields.add(ByteArrays.toByteArray(this.syncType));
            rawFields.add(ByteArrays.toByteArray(0));
            rawFields.add(ByteArrays.toByteArray((byte)0));
            rawFields.add(ByteArrays.toByteArray(this.transparentClock));
            rawFields.add(this.padding);
            return rawFields;
        }

        public int getIntegrationCycle() {
            return integrationCycle;
        }

        public int getMembershipNew() {
            return membershipNew;
        }

        public byte getSyncPriority() {
            return syncPriority;
        }

        public byte getSyncDomain() {
            return syncDomain;
        }

        public byte getSyncType() {
            return syncType;
        }

        public long getTransparentClock() {
            return transparentClock;
        }
    }

    public static final class Builder extends AbstractBuilder implements ChecksumBuilder<PcfPacket>, LengthBuilder<PcfPacket> {

        private int integrationCycle;
        private int membershipNew;
        private byte syncPriority;
        private byte syncDomain;
        private byte syncType;
        private long transparentClock;
        private short headerChecksum;
        private byte[] padding;
        private boolean correctChecksumAtBuild;
        private boolean correctLengthAtBuild;
        private boolean paddingAtBuild;

        public Builder() {
        }

        public Builder(PcfPacket packet) {
            this.integrationCycle = packet.header.integrationCycle;
            this.membershipNew = packet.header.membershipNew;
            this.syncPriority = packet.header.syncPriority;
            this.syncDomain = packet.header.syncDomain;
            this.syncType = packet.header.syncType;
            this.transparentClock = packet.header.transparentClock;
            this.padding = packet.header.padding;
        }

        public Builder(byte[] rawData) {
            byte[] integrationCycleBytes = Arrays.copyOfRange(rawData, 0, 4);
            this.integrationCycle = ByteBuffer.wrap(integrationCycleBytes).getInt();

            byte[] membershipNewBytes = Arrays.copyOfRange(rawData, 4, 8);
            this.membershipNew = ByteBuffer.wrap(membershipNewBytes).getInt();

            byte[] syncPriorityBytes = Arrays.copyOfRange(rawData, 12, 13);
            this.syncPriority = ByteBuffer.wrap(syncPriorityBytes).get();

            byte[] syncDomainBytes = Arrays.copyOfRange(rawData, 13, 14);
            this.syncDomain = ByteBuffer.wrap(syncDomainBytes).get();

            byte[] syncTypeBytes = Arrays.copyOfRange(rawData, 14, 15);
            this.syncType = ByteBuffer.wrap(syncTypeBytes).get();

            byte[] transparentClockBytes = Arrays.copyOfRange(rawData, 20, 28);
            this.transparentClock = ByteBuffer.wrap(transparentClockBytes).getLong();

            byte[] paddingBytes = Arrays.copyOfRange(rawData, 28, 46);
            this.padding = paddingBytes;
        }

        public PcfPacket.Builder correctChecksumAtBuild(boolean correctLengthAtBuild) {
            this.correctChecksumAtBuild = correctLengthAtBuild;
            return this;
        }

        public Builder correctLengthAtBuild(boolean correctLengthAtBuild) {
            this.correctLengthAtBuild = correctLengthAtBuild;
            return this;
        }

        public Builder paddingAtBuild(boolean paddingAtBuild) {
            this.paddingAtBuild = paddingAtBuild;
            return this;
        }

        @Override
        public PcfPacket build() {
            return new PcfPacket(this);
        }

        public Builder integrationCycle(int integrationCycle) {
            this.integrationCycle = integrationCycle;
            return this;
        }

        public Builder membershipNew(int membershipNew) {
            this.membershipNew = membershipNew;
            return this;
        }

        public Builder syncPriority(byte syncPriority) {
            this.syncPriority = syncPriority;
            return this;
        }

        public Builder syncDomain(byte syncDomain) {
            this.syncDomain = syncDomain;
            return this;
        }

        public Builder syncType(byte syncType) {
            this.syncType = syncType;
            return this;
        }

        public Builder transparentClock(long transparentClock) {
            this.transparentClock = transparentClock;
            return this;
        }
    }
}
