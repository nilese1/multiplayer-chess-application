package chess;

import java.util.ArrayList;

public class PieceDirection {
    private int repeatable = 0;
    private boolean rotatable = false;
    private boolean canCapture = true;
    private boolean hasToCapture = false;
    private int dx;
    private int dy;

    public PieceDirection(int dx, int dy, int repeatable, boolean rotatable) {
        this.dx = dx;
        this.dy = dy;
        this.repeatable = repeatable;
        this.rotatable = rotatable;
    }

    public PieceDirection(int dx, int dy, int repeatable, boolean rotatable, boolean canCapture) {
        this(dx, dy, repeatable, rotatable);
        this.canCapture = canCapture;
    }

    public PieceDirection(int dx, int dy, int repeatable, boolean rotatable, boolean canCapture, boolean hasToCapture) {
        this(dx, dy, repeatable, rotatable, canCapture);
        this.hasToCapture = hasToCapture;
    }

    /**
     * Gets the move from the direction given an offset and a rotation, offset can represent a rotation if negative
     *
     * @param begX    starting x position of the move
     * @param begY    starting y position of the move
     * @return        resulting move from these changes
     */
    public ArrayList<Move> getMoves(int begX, int begY, boolean flipped, Piece[][] board) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                if (!rotatable && (i != 1 || j != 1)) continue;

                // avoid repeating moves when we rotate
                if ((i == -1 && dx == 0) || (j == -1 && dy == 0)) continue;

                for (int k = 1; k <= repeatable; k++) {
                    int flip = 1;

                    if (flipped) flip = -1;

                    int newX = i * k * dx + begX;
                    int newY = j * k * dy * flip + begY;

                    // new cords are out of bounds
                    if (newX < 0 || newX > ChessGame.GAME_SIZE - 1 || newY < 0 || newY > ChessGame.GAME_SIZE - 1) break;

                    Move toAdd = new Move(begX, begY, newX, newY);

                    // has to capture and no piece is there
                    Piece pieceToCapture = board[newY][newX];
                    if (pieceToCapture == null && hasToCapture) {
                        break;
                    }

                    possibleMoves.add(toAdd);

                    // Path is blocked by a piece
                    if (pieceToCapture != null && canCapture) {
                        toAdd.setCapture(true);
                        break;
                    }
                    else if(pieceToCapture != null) {
                        possibleMoves.remove(possibleMoves.size() - 1);
                        break;
                    }
                }
            }
        }

        return possibleMoves;
    }

    public int getRepeatable() {
        return repeatable;
    }

    public void setRepeatable(int repeatable) {
        this.repeatable = repeatable;
    }
    public boolean isRotatable() { return rotatable; }

    @Override
    public String toString() {
        return "PieceDirection{" +
                "repeatable=" + repeatable +
                ", rotatable=" + rotatable +
                ", dx=" + dx +
                ", dy=" + dy +
                '}';
    }
}
