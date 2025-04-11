/** -----------------------------------------------------------
 * Created by: Amanda Jones
 * Date: 5/7/2024
 * File: Time.java
 * Description: Creates a GUI that simulates a traffic monitoring
 * system displaying vehicles, traffic lights, and the time.
 * Class: Class to create a clock to display the current time
 * ------------------------------------------------------------
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class Time implements Runnable {
    private boolean isRunning;
    private String timeDisplay = "hh:mm:ss a";
    private SimpleDateFormat timeFormat = new SimpleDateFormat(timeDisplay); 
    Date date = new Date(System.currentTimeMillis());

    public Time() {
        this.isRunning = Thread.currentThread().isAlive();
    }
    
    public String getTime() {
        date = new Date(System.currentTimeMillis());
        return timeFormat.format(date);
    }

    @Override
    public void run() {
        //While running, constantly update current time
        while (isRunning) {
            
        	TrafficGUI.timeText.setText(getTime());
        } 
    }
    
}