package net;

import net.packets.LoginPacket;
import net.packets.MovePacket;
import net.packets.Packet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import chess.Move;

class ClientHandler extends Thread {
    private Socket socket;

    private ChessServer server;

    ObjectOutputStream output;
    ObjectInputStream input;

    public ClientHandler(Socket socket, ChessServer server) throws IOException {
        this.socket = socket;
        this.server = server;

        this.output = new ObjectOutputStream(socket.getOutputStream());
        this.output.flush();

        this.input = new ObjectInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        try {
            while (true) {
                Packet data = (Packet) input.readObject();
                System.out.println("Client Handler is listening...");

                handlePacket(data);
            }
        }
        catch (ClassNotFoundException | IOException e) {
            System.err.println("Client Handler Exception: " + e.getMessage());
        }
        finally {
            try {
                this.close();
            }
            catch (IOException e) {
                System.err.println("Client Handler Exception: " + e.getMessage());
            }
        }
    }

    private void handlePacket(Packet data) throws IOException, ClassNotFoundException {
        Packet.PacketType packetType = Packet.lookupPacket(data.getPacketID());
        System.out.println("Client Handler received a " + packetType + " packet");

        if (packetType == Packet.PacketType.LOGIN && ((LoginPacket) data).isHost()) {
            server.setHostColor(((LoginPacket) data).getPlayerColor());
        }
        else if (packetType == Packet.PacketType.LOGIN && !((LoginPacket) data).isHost()) {
            changeLoginPacketColor((LoginPacket) data);
        }

        // Send packet to all clients
        server.sendDataToAllClients(data);

        // Send host color to server
        if (packetType == Packet.PacketType.LOGIN && ((LoginPacket) data).isHost()) {
            server.setHostColor(((LoginPacket) data).getPlayerColor());
        }

    }

    private void changeLoginPacketColor(LoginPacket data) {
        char hostColor = server.getHostColor();
        
        if (hostColor == 'w')
            data.setPlayerColor('b');
        else if (hostColor == 'b')
            data.setPlayerColor('w');
    }

    public void sendData(Packet data) throws IOException {
        output.writeObject(data);
    }

    public void close() throws IOException {
        socket.close();
        input.close();
        output.close();
    }
}