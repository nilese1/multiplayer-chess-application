package timer;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ChessTimerPane extends Pane implements Runnable {
    private ChessTimer gameTimer;
    private StringProperty timerText;
    private Color color;

    public ChessTimerPane() {
        this.gameTimer = new ChessTimer();
        this.color = Color.WHITE;
    }
    
    public ChessTimerPane(int hours, int minutes, int seconds, Color color) {
        this.gameTimer = new ChessTimer(hours, minutes, seconds);
        this.color = color;
    }

    public ChessTimerPane(int hours, int minutes, int seconds, int incrementSeconds, Color color) {
        this.gameTimer = new ChessTimer(hours, minutes, seconds, incrementSeconds);
        this.color = color;
    }

    @Override
    public void run() {
        timerText = new SimpleStringProperty();
    }

    public void updateTimer() {
        int hrs = gameTimer.displayHoursLeft();
        int mins = gameTimer.displayMinutesLeft();
        int secs = gameTimer.displaySecondsLeft();
        
        String timeString = hrs + ":" + mins + ":" + secs;
        timerText.set(timeString);
    }

    public void setTimer(ChessTimer other) {
        this.gameTimer = other;
        updateTimer();
    }
}
