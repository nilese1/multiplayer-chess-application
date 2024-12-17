package timer;

public class ChessTimer extends Thread {
    private int maxTimems;
    private int timeLeftms;

    private int incrementSeconds = 0;

    private boolean isPaused = false;

    /**
     * how long thread waits to update timer
     * increase if performance issues
     */
    final static int SLEEPMS = 1;

    public ChessTimer() {
        final int defaultSeconds = 10*60;
        timeLeftms = defaultSeconds*1000;
    }
    
    public ChessTimer(int hours, int minutes, int seconds) {
        int maxTimeSeconds = hours*3600 + minutes*60 + seconds; 

        this.maxTimems = maxTimeSeconds*1000;

    }

    public ChessTimer(int hours, int minutes, int seconds, int incrementSeconds) {
        this(hours, minutes, seconds);
        this.incrementSeconds = incrementSeconds;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();

        synchronized (this) {
            while (timeLeftms > 0) {
                try {
                    Thread.sleep(ChessTimer.SLEEPMS);
                }
                catch (InterruptedException ignored) {}

                long endTime = System.currentTimeMillis();
                timeLeftms -= endTime - startTime;

                startTime = endTime;
            }
        }
   }

   private void toggleTimer() throws InterruptedException {
        if (isPaused) {
            this.notify();
            return;
        }

        this.wait();
   }

   public void setTimeLeftms(int timeLeftms) {
        this.timeLeftms = timeLeftms;
   }

   public void increment() {
        this.timeLeftms += incrementSeconds*1000;
   }

    // Display in this context means the time that will be displayed on the clock, not the actual time left
    public int displayHoursLeft() {
        int seconds = timeLeftms/1000;

        return seconds/3600;
    }

    public int displayMinutesLeft() {
        int seconds = displayHoursLeft()*3600 - timeLeftms/1000;
        return seconds/60;    
    }

    public int displaySecondsLeft() {
        int seconds = displayHoursLeft()*3600 - displayMinutesLeft()*60 - timeLeftms/1000;
        return seconds;
    }
}