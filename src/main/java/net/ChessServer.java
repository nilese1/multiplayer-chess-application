package net;

import net.packets.LoginPacket;
import net.packets.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import chess.ChessGame;

public class ChessServer extends Thread {
    private int serverPort;

    private ArrayList<ClientHandler> connectedClients;

    private ServerSocket server;

    private char hostColor;

    private final int maxClients = 2;

    public ChessServer(int serverPort) {
        this.serverPort = serverPort;
        this.connectedClients = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(serverPort);

            System.out.println("Server: started at " + server.getInetAddress());

            while (true) {
                connectNewClient();
            }
        }
        catch (IOException e) {
            // make an error pane later
            System.err.println("Server Exception: " + e.getMessage());
        }
        finally {
            try {
                this.close();
            }
            catch (IOException e) {
                System.out.println("Server Exception: " + e.getMessage());
            }
        }
    }

    private void connectNewClient() throws IOException {
        Socket newClient = server.accept();

        if (connectedClients.size() >= maxClients) {
            System.err.println("Server Exception: client attempted to connect but server was full");
            return;
        }

        System.out.println("Server: New client connected at " + newClient.getInetAddress());

        ClientHandler clientHandler = new ClientHandler(newClient, this);
        connectedClients.add(clientHandler);

        clientHandler.start();
    }

    public void sendDataToAllClients(Packet data) throws IOException {
        for (ClientHandler client : connectedClients) {
            client.sendData(data);

            Packet.PacketType packetType = Packet.lookupPacket(data.getPacketID());
            System.out.println("Server: Sent " + packetType + " packet to all clients");
        }
    }

    public void setHostColor(char hostColor) {
        assert (hostColor == 'w' || hostColor == 'b') : "Host color must be either white or black (w or b)";
        this.hostColor = hostColor;
    }

    public char getHostColor() {
        return hostColor;
    }

    public void close() throws IOException {
        for (ClientHandler client : connectedClients) {
            client.close();
        }

        server.close();
    }
}
