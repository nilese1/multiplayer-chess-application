package net.packets;

import net.ChessClient;
import net.ChessServer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

public abstract class Packet implements Serializable {
    public enum PacketType {
        INVALID(-1), LOGIN(0), DISCONNECT(1), MOVE(2);

        private final int packetID;

        PacketType(int packetID) {
            this.packetID = packetID;
        }

        public int getPacketID() {
            return packetID;
        }
    }

    public byte packetID;

    private String username;

    public Packet(int packetID, String username) {
        this.packetID = (byte) packetID;
        this.username = username;
    }

    public abstract void writeData(ChessClient client) throws IOException;

    public abstract void writeData(ChessServer server) throws IOException;

    public abstract byte[] getData();

    public byte getPacketID() {
        return packetID;
    }

    public static PacketType lookupPacket(int id) {
        for (PacketType p : PacketType.values()) {
            if (id == p.getPacketID()) {
                return p;
            }
        }

        return PacketType.INVALID;
    }

    /**
     * Used to affirm whether the client is getting sent its own packet
     * from the server
     *
     * @return is this an ack
     */
    public boolean isAck(String clientUsername) {
        return username.equals(clientUsername);
    }

    public String getUsername() { return username; }
}
