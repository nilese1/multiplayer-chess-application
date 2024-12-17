package chess;

import exceptions.InvalidPieceException;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import net.ChessClient;
import net.packets.MovePacket;

import java.io.IOException;
import java.util.ArrayList;

public class ChessGame extends Pane implements Runnable {
    public final static int GAME_SIZE = 8;

    /** Color of squares set by theme */
    private Color color1 = Color.LIGHTGRAY;
    private Color color2 = Color.GREY;

    /** position in FEN format, default is the initial position for every chess game */
    private String positionFEN;

    /** Stores move history */
    private ArrayList<Move> moves = new ArrayList<>();

    /** Stores all pieces */
    private Piece[][] pieces = new Piece[GAME_SIZE][GAME_SIZE];

    /** PGN format of the current game */
    private String gamePGN = "";

    /** Width and height of the game pane */
    private double width;
    private double height;

    /** scale of squares and pieces, based off of width and height */
    private double scale;

    /** if false, it is blacks turn, otherwise its whites */
    private boolean isWhitesTurn = true;

    private boolean isGameEnded = false;

    private boolean isOpponentConnected = false;

    /** char representing whether the player is white or black (not related to race) */
    private char playerColor = 'w';

    private ArrayList<Circle> circles = new ArrayList<>();

    private ArrayList<Rectangle> board = new ArrayList<>();

    private ChessClient chessClient = null;

    /**
     * Default constructor of Game
     */
    public ChessGame() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public ChessGame(char playerColor) {
        this();
        this.playerColor = playerColor;
    }

    /**
     * Used to start a game from a position other than the default
     *
     * @param positionFEN position in FEN format
     */
    public ChessGame(String positionFEN) {
        this.positionFEN = positionFEN;

        this.width = 1000;
        this.height = 1000;

        this.scale = width / GAME_SIZE;
    }

    @Override
    public void run() {
        displayPiecesFEN();
        updateLegalMoves(false);
        updateDisplay();
    }

    public void updateDisplay() {
        getChildren().clear();

        displaySquares();

        displayPieces();
    }

    public void setChessClient(ChessClient chessClient) { this.chessClient = chessClient; }

    private void displaySquares() {
        // Display squares of game board
        for (int i = 0; i < GAME_SIZE; i++) {
            for (int j = 0; j < GAME_SIZE; j++) {
                Rectangle curSquare = new Rectangle(scale*i, scale*j, scale, scale);

                board.add(curSquare);

                // Alternate between both colors
                if ((i + j) % 2 == 0) curSquare.setFill(color1);
                else curSquare.setFill(color2);

                getChildren().add(curSquare);
            }
        }
    }

    public void resize(double height) {
        scale = height / GAME_SIZE;

        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece == null) continue;
                piece.setScale(scale);
            }
        }

        super.getChildren().clear();

        // Display squares of game board
        displaySquares();

        displayPieces();
    }

    /**
     * displays all the pieces using the FEN string
     */
    public void displayPiecesFEN() {
        // Evaluate FEN string
        int y = 0, x = 0;
        for (int i = 0; i < positionFEN.length(); i++) {
            char cur = positionFEN.charAt(i);
            if (cur == ' ') {
                break;
            }
            else if (cur >= '0' && cur <= '9') {
                x += Integer.parseInt(String.valueOf(cur));
            }
            else if (cur == '/') {
                y++;
                x = 0;
            }
            else {
                try {
                    Piece curPiece = new Piece(cur, this, x, y);

                    pieces[y][x] = curPiece;
                    curPiece.setScale(scale);
                    curPiece.setX(x);
                    curPiece.setY(y);
                }
                catch (InvalidPieceException ex) {
                    System.err.println(ex.getMessage());
                }

                x++;
            }
        }
        displayPieces();
    }
    
    public void clearDisplayPossibleMoves() {
        getChildren().removeAll(circles);
        circles.clear();
    }
    
    public void displayPossibleMoves(ArrayList<Move> moves, char color) {
        clearDisplayPossibleMoves();

        if ((color != 'w' && isWhitesTurn) || (color == 'w' && !isWhitesTurn))
            return;

        // Recommended to be less than 1
        double circleScale = 0.4;

        double captureStrokeWidth = 10.0;
        Color circleColor = Color.rgb(150, 150, 150, 0.5);

        for (Move move : moves) {
            displayMoveCircle(move, captureStrokeWidth, circleColor, circleScale);
        }

        getChildren().addAll(circles);
    }

    private void displayMoveCircle(Move move, double captureStrokeWidth, Color circleColor, double circleScale) {
        // location of the move circle
        int moveX = (int) (move.getEndX() * scale + scale / 2);
        int moveY = (int) (move.getEndY() * scale + scale / 2);

        if (playerColor != 'w') {
            moveY = (int) ((7 - move.getEndY()) * scale + scale / 2);
        }

        Circle circle;

        if (move.isCapture()) {
            circle = new Circle(moveX, moveY, scale / 2 - captureStrokeWidth);

            circle.setFill(Color.TRANSPARENT);
            circle.setStroke(circleColor);
            circle.setStrokeWidth(captureStrokeWidth);
        }
        else {
            circle = new Circle(moveX, moveY, (scale / 2) * circleScale);
            circle.setFill(circleColor);
        }

        circles.add(circle);
    }

    /**
     * displays all the pieces using the piece array list
     */
    public void displayPieces() {
        for (Piece[] row : pieces) {
            for (Piece col : row) {
                if (col == null) continue;
                
                // if you're black the pieces should be flipped so the pieces start on your side
                if (playerColor != 'w') {
                    col.setFlipped(true);    
                }
                else {
                    col.setFlipped(false);
                }

                col.updatePosition();
                getChildren().add(col);
            }
        }
    }

    public void printAllLegalMoves() {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece == null) continue;

                System.out.println("----------------------");
                System.out.println(piece);

                System.out.print("{ ");
                for (Move move : piece.getLegalMoves()) {
                    System.out.print(move.getEndX() + " " + move.getEndY() + ", ");
                }
                System.out.println(" }");
            }
        }
    }

    public boolean isLegal(Move move, boolean recursion) {
        // Cannot capture pieces of the same color
        Piece startPiece = pieces[move.getBegY()][move.getBegX()];
        Piece endPiece = pieces[move.getEndY()][move.getEndX()];
        if (endPiece != null && startPiece.getColor() == endPiece.getColor()) return false;

        // Cannot move into check
        char pieceType = Character.toLowerCase(startPiece.getPieceType());
        if (!recursion && isInCheckAfterMove(move)) {
            return false;
        }

        return true;
    }

    /**
     * Gives all pieces all of their legal moves
     */
    public void updateLegalMoves(boolean recursion) {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                updateLegalMovesForPiece(piece, recursion);
            }
        }
    }

    private void updateLegalMovesForPiece(Piece piece, boolean recursion) {
        if (piece == null) return;

        piece.wipeLegalMoves();

        ArrayList<PieceDirection> directions = new ArrayList<>(Piece.PIECE_DIRECTION_MAP.get(Character.toLowerCase(piece.getPieceType())));

        ArrayList<Move> possibleMoves = new ArrayList<>();

        char pieceType = Character.toLowerCase(piece.getPieceType());

        if (pieceType == 'p') {
            // Extra move on first move
            addFirstPawnMove(piece, directions);

            // En Passant
        }

        if (pieceType == 'k') {
            possibleMoves.addAll(getCastles(piece));
        }

        for (PieceDirection direction : directions) {
            ArrayList<Move> moves = direction.getMoves(piece.getPosX(), piece.getPosY(), isFlipped(), pieces);

            for (Move move : moves) if (isLegal(move, recursion)) possibleMoves.add(move);
        }

        piece.addLegalMoves(possibleMoves);
    }

    private void addFirstPawnMove(Piece piece, ArrayList<PieceDirection> directions) {
        if (!piece.hasPieceMoved()) {
            directions.remove(0);
            directions.add(new PieceDirection(0, 1, 2, false, false));
        }
    }

    private ArrayList<Move> getCastles(Piece king) {
        ArrayList<Move> toReturn = new ArrayList<>();

        if (isCastlePossible(king, 'k')) {
            toReturn.add(new Move(true, king.getPosX(), king.getPosY(), true));
        }

        if (isCastlePossible(king, 'q')) {
            toReturn.add(new Move(true, king.getPosX(), king.getPosY(), false));
        }

        return toReturn;
    }

    private boolean isCastlePossible(Piece king, char side) {
        assert (side == 'k' || side == 'q');

        int rookX = (side == 'k') ? 7 : 0;
        int kingY = king.getPosY();

        // If king or rooks move, castle is no longer possible
        if (king.hasPieceMoved()) return false;

        Piece rook = pieces[kingY][rookX];
        boolean isPieceRook = rook != null && Character.toLowerCase(rook.getPieceType()) == 'r';

        if (!isPieceRook || rook.hasPieceMoved()) return false;

        // direction of the rook if it was there
        int sideX = (side == 'k') ? 1 : -1;

        for (int i = king.getPosX() + sideX; i > 0 && i < 7; i += sideX) {
            Piece curPiece = pieces[kingY][i];

            // Cannot castle through a piece or through check
            if (curPiece != null || canSomeoneBlockCastle(i, kingY, king.getColor())) return false;
        }

        return true;
    }

    private boolean canSomeoneBlockCastle(int x, int y, char kingColor) {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece == null || piece.getColor() == kingColor) continue;

                for (Move move : piece.getLegalMoves()) {
                    if (move.getEndX() == x && move.getEndY() == y) return true;
                }
            }
        }

        return false;
    }

    public void move(Move toMove) {
        clearDisplayPossibleMoves();

        if (isGameEnded) return;

        Piece pieceToReturn = updateBoard(toMove);
        if (toMove.isCastle()) castle(toMove);

        isWhitesTurn = !isWhitesTurn;

        // change turn if not multiplayer game
        if (chessClient == null) {
            playerColor = playerColor == 'w' ? 'b' : 'w';
            updateDisplay();
        }

        // To prevent a bug that wouldn't recognize a piece was in check if it was higher in the pieces array than
        // the checking piece, parameter is x because we're trying to find if someone is in check independent of color
        if (isSomeoneInCheck('x')) {
            updateLegalMoves(false);
        }

        moves.add(toMove);

        getChildren().remove(pieceToReturn);

        // printAllLegalMoves();

        

        char result = getGameResult();
        if (result != 'n') {
            endGame(result);
        }
    }

    private Piece updateBoard(Move toMove) {
        Piece pieceToMove = pieces[toMove.getBegY()][toMove.getBegX()];
        Piece pieceToReturn = pieces[toMove.getEndY()][toMove.getEndX()];

        pieces[toMove.getEndY()][toMove.getEndX()] = pieces[toMove.getBegY()][toMove.getBegX()];
        pieces[toMove.getBegY()][toMove.getBegX()] = null;

        pieceToMove.move(toMove.getEndX(), toMove.getEndY());

        updateLegalMoves(false);
        return pieceToReturn;
    }

    private void castle(Move toMove) {
        assert toMove.isCastle();

        // we already move the king in update board, so all we have to do is essentially move the rook
        // The rook always is one space to the left or right of the king depending on king side and queen side
        // castling respectively

        Move rookMove;

        if (toMove.isCastleKingSide()) {
            rookMove = new Move(7, toMove.getEndY(), toMove.getEndX() - 1, toMove.getEndY());
        }
        else {
            rookMove = new Move(0, toMove.getEndY(), toMove.getEndX() + 1, toMove.getEndY());
        }

        updateBoard(rookMove);
    }

    private boolean isInCheckAfterMove(Move toMove) {
        // Perform the move
        Piece pieceToMove = pieces[toMove.getBegY()][toMove.getBegX()];
        Piece pieceToCapture = pieces[toMove.getEndY()][toMove.getEndX()];

        storeAllLegalMoves();

        pieces[toMove.getEndY()][toMove.getEndX()] = pieces[toMove.getBegY()][toMove.getBegX()];
        pieces[toMove.getBegY()][toMove.getBegX()] = null;

        pieceToMove.testMove(toMove.getEndX(), toMove.getEndY());

        updateLegalMoves(true);

        boolean result = isSomeoneInCheck(pieceToMove.getColor());

        // Pretend it never happened
        pieces[toMove.getBegY()][toMove.getBegX()] = pieces[toMove.getEndY()][toMove.getEndX()];
        pieces[toMove.getEndY()][toMove.getEndX()] = pieceToCapture;

        pieceToMove.testMove(toMove.getBegX(), toMove.getBegY());

        restoreAllLegalMoves();

        return result;
    }

    /**
     * These two methods are workarounds to calling updateLegalMoves to restore the moves as it alters the moves
     * undesirably
     */
    private void storeAllLegalMoves() {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece == null) continue;

                piece.storeLegalMoves();
            }
        }
    }

    private void restoreAllLegalMoves() {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece == null) continue;

                piece.restoreLegalMoves();
            }
        }
    }

    public boolean isFlipped() {
        return playerColor == 'w';
    }

    public boolean isWhitesTurn() {
        return isWhitesTurn;
    }

    public boolean isPlayersTurn() {
        return (isWhitesTurn && playerColor == 'w') || (!isWhitesTurn && playerColor == 'b');
    }

    public boolean isOpponentConnected() {
        return chessClient == null || isOpponentConnected;
    }

    public void connectOpponent() {
        isOpponentConnected = true;
    }

    public void disconnectOpponent() {
        isOpponentConnected = false;
    }

    public char getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(char playerColor) {
        assert (playerColor == 'w' || playerColor == 'b') : "Player color must be either white or black (w or b)";
        this.playerColor = playerColor;
    }

    /**
     * SLOW SLOW SLOW LSOW LSWOSLWOLSSLOW
     * @return
     */
    public boolean isSomeoneInCheck(char color) {
        for (Piece[] row : pieces) {
            for (Piece piece : row) {
                if (piece == null || piece.getColor() == color) continue;

                for (Move move : piece.getLegalMoves()) {
                    if (!move.isCapture()) continue;

                    Piece capturingPiece = pieces[move.getEndY()][move.getEndX()];
                    if (capturingPiece == null) continue;

                    char pieceType = capturingPiece.getPieceType();
                    boolean isCapturingKing = Character.toLowerCase(pieceType) == 'k';

                    if (isCapturingKing) return true;
                }
            }
        }

        return false;
    }

    public boolean isSomeoneInCheckmate(char color) {
        return isSomeoneInCheck(color) && noMovesLeft(color);
    }

    private boolean noMovesLeft(char color) {
        for (Piece[] row: pieces) {
            for (Piece piece : row) {
                if (piece == null) continue;
                if (piece.getColor() == color && !piece.getLegalMoves().isEmpty()) return false;
            }
        }

        return true;
    }

    /**
     * Returns the game result in the form of a character
     *
     * @return The result of the game in the following format
     * b: black wins
     * w: white wins
     * s: stalemate
     * f: draw by 50 move rule
     * t: draw by threefold repetition
     * n: game has not yet reached a result
     */
    public char getGameResult() {
        // check if black wins
        if (isSomeoneInCheckmate('w'))
            return 'b';

        // check if white wins
        if (isSomeoneInCheckmate('b'))
            return 'w';

        // check if stalemate by no moves left is reached
        boolean drawByStalemate = noMovesLeft('w') || noMovesLeft('b');

        // stalemate by 50 move rule

        // stalemate by threefold repetition

        if (drawByStalemate)
            return 's';

        return 'n';
    }

    private void endGame(char result) {
        isGameEnded = true;

        Text tResult = new Text();

        tResult.setX(super.getWidth() / 2);
        tResult.setY(super.getHeight() / 2);

        tResult.setScaleX(scale / 20);
        tResult.setScaleY(scale / 20);

        tResult.setWrappingWidth(scale);

        if (result == 'b') {
            tResult.setText("Black has won by checkmate!");
        }
        else if (result == 'w') {
            tResult.setText("White has won by checkmate!");
        }
        else if (result == 's') {
            tResult.setText("Game is drawn by stalemate!");
        }

        getChildren().add(tResult);
    }

    public ChessClient getChessClient() {
        return chessClient;
    }
}