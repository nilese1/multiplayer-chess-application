package chess;

import java.util.ArrayList;

public class Board {
    private ArrayList<ArrayList<Piece>> boardMatrix = new ArrayList<>();

    public Board() {
        for (int i = 0; i < ChessGame.GAME_SIZE; i++) {
            boardMatrix.add(new ArrayList<>());
            for (int j = 0; j < ChessGame.GAME_SIZE; j++) {
                boardMatrix.get(boardMatrix.size() - 1).add(null);
            }
        }


    }
}

