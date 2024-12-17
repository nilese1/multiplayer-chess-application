package net.packets;

import net.ChessClient;
import net.ChessServer;

import java.io.IOException;

public class DisconnectPacket extends Packet {
    public DisconnectPacket(String username) {
        super(1, username);
    }

    @Override
    public void writeData(ChessClient client) throws IOException {

    }

    @Override
    public void writeData(ChessServer server) throws IOException {

    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }
}
