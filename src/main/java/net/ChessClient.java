package net;

import net.packets.LoginPacket;
import net.packets.MovePacket;
import net.packets.Packet;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.locks.Lock;

import chess.ChessGame;
import chess.Move;
import javafx.application.Platform;

public class ChessClient extends Thread {
    private InetAddress serverIP;
    private int serverPort;

    private ChessGame game;

    private Socket client;

    private ObjectInputStream input;
    private ObjectOutputStream output;

    private String username;
    private boolean isHost;

    public ChessClient(InetAddress serverIP, int serverPort, String username, boolean isHost) throws IOException {
        this.serverIP = serverIP;
        this.serverPort = serverPort;

        this.username = username;
        this.isHost = isHost;
        this.game = new ChessGame();
        game.setChessClient(this);

        // If client is not host, opponent is already connected
        if (!isHost)
            game.connectOpponent();

        this.client = new Socket(serverIP.getHostName(), serverPort);

        System.out.println("Client: Connected to server at " + serverIP);

        this.output = new ObjectOutputStream(client.getOutputStream());
        this.output.flush();

        this.input = new ObjectInputStream(client.getInputStream());
    }

    @Override
    public void run() {
        try {
            loginToServer();

            while (true) {
                Packet data = (Packet) input.readObject();
                handlePacket(data);
            }
        }
        catch (IOException e) {
            // make an error pane later
            System.err.println("Client Error: " + e.getMessage());
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                this.close();
            }
            catch (IOException e) {
                System.out.println("Client Exception: " + e.getMessage());
            }
        }
    }

    public void loginToServer() throws IOException {
        System.out.println("Client: Logging into server");
        sendData(new LoginPacket(username, isHost, game.getPlayerColor()));
    }

    public void loginFromClient(LoginPacket data) {
        System.out.println(data.getUsername() + " has logged in!");
        game.connectOpponent();
    }

    public void sendData(Packet data) throws IOException {
        output.writeObject(data);

        String packetType = String.valueOf(Packet.lookupPacket(data.getPacketID()));
        System.out.println(packetType + " Packet Sent");
    }

    public void handlePacket(Packet data) {
        Packet.PacketType packetType = Packet.lookupPacket(data.getPacketID());

        // If client is not host, it will receive its color from the server
        if (!isHost && packetType == Packet.PacketType.LOGIN && data.isAck(username)) {
            setClientColor((LoginPacket) data);
            return;
        }

        if (data.isAck(username))
            return;

        System.out.println(packetType + " Packet received");

        switch(packetType) {
        case LOGIN:
            loginFromClient((LoginPacket) data);
            break;
        case DISCONNECT:
            break;

        case MOVE:
            updateBoard((MovePacket) data);
            break;

        case INVALID:
            System.err.println("Invalid packet received!");
        }
    }

    public void setClientColor(char color) {
        game.setPlayerColor(color);
    }

    public void setClientColor(LoginPacket data) {
        game.setPlayerColor(data.getPlayerColor());
    }

    private void updateBoard(MovePacket movePacket) {
        Platform.runLater(() -> {
            game.move(movePacket.getMove());
        });
    }

    public ChessGame getGame() { return game; }

    public String getUsername() { return username; }

    public void close() throws IOException {
        client.close();
        input.close();
        output.close();
    }
}

