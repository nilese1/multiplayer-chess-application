package chess;

import java.io.Serializable;

public class Move implements Serializable {
    /** Whether this move is a castle */
    private boolean isCastle = false;

    private boolean isCastleKingSide = true;

    /** Whether this move is a capture */
    private boolean isCapture = false;

    /** Whether this move is a check */
    private boolean isCheck = false;

    /** Whether this move is a checkmate */
    private boolean isCheckmate = false;

    /** The piece that the piece promotes to (x if none) */
    private char promotion = 'x';

    /** beginning position of the move */
    private int begX;
    private int begY;

    /** end position of the move */
    private int endX;
    private int endY;

    /** FEN notation char of the piece */
    private char pieceType;

    public Move(boolean isCastle, int begX, int begY, boolean isCastleKingSide) {
        this.isCastle = isCastle;
        this.begX = begX;
        this.begY = begY;
        this.isCastleKingSide = isCastleKingSide;

        this.endX = isCastleKingSide ? begX + 2 : begX - 2;
        this.endY = begY;
    }

    public Move(int begX, int begY, int endX, int endY) {
        this.begX = begX;
        this.begY = begY;
        this.endX = endX;
        this.endY = endY;
    }

    public Move(int endX, int endY) {
        this(0, 0, endX, endY);
    }

    public void setCastle(boolean castle) {
        isCastle = castle;
    }

    public void setCapture(boolean capture) {
        isCapture = capture;
    }

    public void setIsCheck(boolean check) {
        isCheck = check;
    }

    public void setCheckmate(boolean checkmate) {
        isCheckmate = checkmate;
    }

    public void setPromotion(char promotion) {
        this.promotion = promotion;
    }

    public void setPieceType(char pieceType) {
        this.pieceType = pieceType;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public boolean isCapture() {
        return isCapture;
    }

    public boolean isCastle() { return isCastle; }

    public boolean isCastleKingSide() { return isCastleKingSide; }

    public char getPromotion() {
        return promotion;
    }

    public int getBegX() {
        return begX;
    }

    public int getBegY() {
        return begY;
    }

    public int getEndX() { return endX; }
    public int getEndY() { return endY; }

    public char getColor() {
        return Character.isUpperCase(pieceType) ? 'w' : 'b';
    }

    /**
     * For the purpose of updating the moves when the piece attached to it moves
     */
    public void update(int newX, int newY) {
        this.endX -= this.begX;
        this.endY -= this.begY;

        this.begX += newX;
        this.begY += newY;
        this.endX += newX;
        this.endY += newY;
    }

    /** Returns the move in PGN notation */
    @Override
    public String toString() {
        // convert y position to a letter
        char yPosLetter = (char) ('A' + endY);
        return "";
    }
}