package chess;

import exceptions.InvalidPieceException;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import net.packets.MovePacket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

public class Piece extends Group {
    /**
     * for storing directions in which each piece can go and if this pattern repeats
     * moves in this case are normalized at 0, 0
     */
    public final static Map<Character, ArrayList<PieceDirection>> PIECE_DIRECTION_MAP;

    static {
        PIECE_DIRECTION_MAP = new HashMap<Character, ArrayList<PieceDirection>>();
        PIECE_DIRECTION_MAP.put('p', new ArrayList<>(Arrays.asList(
            new PieceDirection(0, 1, 1, false, false),
            new PieceDirection(1, 1, 1, false, true, true),
            new PieceDirection(-1, 1, 1, false, true, true)
        )));
        PIECE_DIRECTION_MAP.put('b', new ArrayList<>(Arrays.asList(
            new PieceDirection(1, 1, 8, true)
        )));
        PIECE_DIRECTION_MAP.put('n', new ArrayList<>(Arrays.asList(
            new PieceDirection(1, 2, 1, true),
            new PieceDirection(2, 1, 1, true)
        )));
        PIECE_DIRECTION_MAP.put('r', new ArrayList<>(Arrays.asList(
            new PieceDirection(0, 1, 8, true),
            new PieceDirection(1, 0, 8, true)
        )));
        PIECE_DIRECTION_MAP.put('q', new ArrayList<>(Arrays.asList(
            new PieceDirection(0, 1, 8, true),
            new PieceDirection(1, 1, 8, true),
            new PieceDirection(1, 0, 8, true)
        )));
        PIECE_DIRECTION_MAP.put('k', new ArrayList<>(Arrays.asList(
            new PieceDirection(0, 1, 1, true),
            new PieceDirection(1, 1, 1, true),
            new PieceDirection(1, 0, 1, true)
        )));
    }

    /**
     * For converting fen piece notation to the image file name
     */
    public final static Map<Character, String> FEN_TO_IMG = new HashMap<Character, String>() {{
        put('p', "black-pawn.png");
        put('b', "black-bishop.png");
        put('n', "black-knight.png");
        put('r', "black-rook.png");
        put('k', "black-king.png");
        put('q', "black-queen.png");
        put('P', "white-pawn.png");
        put('B', "white-bishop.png");
        put('N', "white-knight.png");
        put('R', "white-rook.png");
        put('K', "white-king.png");
        put('Q', "white-queen.png");
    }};

    /**
     * indicates the color and type of piece using FEN format rules
     */
    private char pieceType;

    /** indicates the color of the piece alone (for readability because pieceType also does this) */
    private char color;

    /**
     * Indicates whether the piece has moved or not, useful to indicate the possibility of castling
     * or pawns moving two spaces
     */
    private boolean hasMoved = false;

    /**
     * List of all the legal moves that the piece can make
     * generated in Game class, so we can use the entire board as context for where the piece can go
     */
    private ArrayList<Move> legalMoves = new ArrayList<>();

    /**
     * used for temporary storage in storing and restoring moves
     */
    private ArrayList<Move> tempLegalMoves = new ArrayList<>();

    /**
     * Stores the game the piece is a part of so we can update the gameboard when the piece moves
     */
    ChessGame curGame;

    /**
     * position of the chess piece
     */
    private int posX;
    private int posY;

    private double scale;

    /**
     * Whether or not the piece is displayed on the opposite side
     * (posY will not be changed from this just where the piece is displayed)
     */
    private boolean flipped = false;

    ImageView ivPiece;

    Rectangle rectClickArea = new Rectangle(0, 0, scale, scale);

    /**
     * Creates a Piece
     *
     * @param pieceType represents color and type in FEN format
     */
    public Piece(char pieceType, ChessGame game) throws InvalidPieceException {
        scale = 100;

        super.setAutoSizeChildren(true);
        super.prefWidth(scale);
        super.prefHeight(scale);

        String imageFilename = FEN_TO_IMG.get(pieceType);
        if (imageFilename == null)
            throw new InvalidPieceException(pieceType + " is not a valid FEN character");

        String path = getClass().getResource("/pieces/" + imageFilename).toString();
        Image image = new Image(path);
        this.pieceType = pieceType;

        this.curGame = game;

        if (Character.isUpperCase(this.pieceType))
            color = 'w';
        else
            color = 'b';

        setImageView(image);

        setClickArea();

        super.getChildren().addAll(rectClickArea, ivPiece);

        setEventHandlers();
    }

    private void setEventHandlers() {
        super.setOnMouseDragged(dragPiece);
        super.setOnMouseReleased(dropPiece);
        super.setOnMouseClicked(e -> {
            curGame.displayPossibleMoves(legalMoves, color);
        });
        super.setOnMouseEntered(e -> {
            super.setCursor(Cursor.HAND);
        });
    }

    private void setClickArea() {
        rectClickArea = new Rectangle(0, 0, scale, scale);
        rectClickArea.setFill(Color.TRANSPARENT);
        rectClickArea.setStroke(Color.TRANSPARENT);
    }

    private void setImageView(Image image) {
        ivPiece = new ImageView(image);
        ivPiece.setImage(image);
        ivPiece.setPreserveRatio(true);
    }

    /**
     * Creates a Piece
     *
     * @param pieceType represents color and type in FEN format
     * @param posX      the posX position of the piece
     * @param posY      the posY position of the piece
     */
    public Piece(char pieceType, ChessGame game, int posX, int posY) throws InvalidPieceException {
        this(pieceType, game);
        this.posX = posX;
        this.posY = posY;
    }

    EventHandler<MouseEvent> dragPiece = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            curGame.displayPossibleMoves(legalMoves, color);

            Piece.super.setCursor(Cursor.CLOSED_HAND);

            setTranslateX(e.getSceneX() - scale / 2);
            setTranslateY(e.getSceneY() - scale / 2);
        }
    };

    EventHandler<MouseEvent> dropPiece = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            int x = (int) (e.getSceneX() / scale);
            int y = (int) (e.getSceneY() / scale);

            if (flipped)
                y = 7 - y;

            Move move = getLegalMove(x, y);

            System.out.println("connected: " + curGame.isOpponentConnected());
            System.out.println("turn: " + curGame.isPlayersTurn());
            System.out.println("piece turn: " + isPiecesTurn());
            System.out.println("move: " + move);

            // Piece is in correct square so we perform a move
            if (curGame.isOpponentConnected() && curGame.isPlayersTurn() && isPiecesTurn() && move != null) {
                curGame.move(move);

                if (curGame.getChessClient() != null) {
                    try {
                        curGame.getChessClient().sendData(new MovePacket(move, curGame.getChessClient().getUsername()));
                    }
                    catch (IOException ignored) {
                        System.err.println("Game: unable to send data to server");
                    }
                }
            }
            // Piece is in an incorrect square, so we put it back where it belongs
            else {
                setX(posX);
                setY(posY);
            }
        }
    };

    public void setPieceType(char pieceType) {
        this.pieceType = pieceType;
    }

    public void move(int posX, int posY) {
        this.setX(posX);
        this.setY(posY);

        this.hasMoved = true;
    }

    protected void testMove(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void setScale(double scale) {
        this.scale = scale;

        ivPiece.setFitWidth(scale);
        ivPiece.setFitHeight(scale);

        rectClickArea.setWidth(scale);
        rectClickArea.setHeight(scale);

        setX(posX);
        setY(posY);
    }

    public void setX(int posX) {
        super.setTranslateX(posX * scale);
        this.posX = posX;
    }

    public void setY(int posY) {
        int flippedY = flipped ? 7 - posY : posY;

        super.setTranslateY(flippedY * scale);
        this.posY = posY;
    }

    public void setFlipped(boolean flipped) {
        this.flipped = flipped;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosX() {
        return posX;
    }

    public char getColor() {
        return color;
    }

    public char getPieceType() {
        return pieceType;
    }

    public void updatePosition() {
        setX(posX);
        setY(posY);
    }

    public boolean hasPieceMoved() { return hasMoved; }

    public ArrayList<Move> getLegalMoves() {
        return legalMoves;
    }

    public void addLegalMoves(ArrayList<Move> legalMoves) {
        this.legalMoves.addAll(legalMoves);
    }

    public void addLegalMoves(Move legalMove) {
        this.legalMoves.add(legalMove);
    }

    public boolean isPiecesTurn() {
        return (color == 'w' && curGame.isWhitesTurn()) || (color == 'b' && !curGame.isWhitesTurn());
    }

    public void storeLegalMoves() {
        tempLegalMoves.clear();
        tempLegalMoves.addAll(legalMoves);
    }

    public void restoreLegalMoves() {
        legalMoves.clear();
        legalMoves.addAll(tempLegalMoves);
    }

    public void wipeLegalMoves() { legalMoves.clear(); }

    public Move getLegalMove(int x, int y) {
        for (Move move : legalMoves) {
            if (move.getEndX() == x && move.getEndY() == y) return move;
        }

        return null;
    }

    @Override
    public String toString() {
        return "Piece{" +
                "pieceType=" + pieceType +
                ", color=" + color +
                ", hasMoved=" + hasMoved +
                ", posX=" + posX +
                ", posY=" + posY +
                '}';
    }
}
