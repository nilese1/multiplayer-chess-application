package net.packets;

import net.ChessClient;
import net.ChessServer;

import java.io.IOException;

import chess.Move;

public class MovePacket extends Packet {
    Move move;

    public MovePacket(Move move, String username) {
        super(2, username);

        this.move = move;
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

    public Move getMove() {
        return move;
    }
}
