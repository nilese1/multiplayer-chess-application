package net.packets;

import net.ChessClient;
import net.ChessServer;

import java.io.IOException;

public class LoginPacket extends Packet {
    private boolean isHost;
    private char playerColor;
    
    public LoginPacket(String username, boolean isHost, char playerColor) {
        super(0, username);
        this.isHost = isHost;
        this.playerColor = playerColor;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setPlayerColor(char playerColor) {
        this.playerColor = playerColor;
    }

    public char getPlayerColor() {
        return playerColor;
    }

    @Override
    public void writeData(ChessClient client) throws IOException {
        client.sendData(this);
    }

    @Override
    public void writeData(ChessServer server) throws IOException {
        server.sendDataToAllClients(this);
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }
}
