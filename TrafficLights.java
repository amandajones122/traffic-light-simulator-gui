/** -----------------------------------------------------------
 * Created by: Amanda Jones
 * Date: 5/7/2024
 * File: TrafficLights.java
 * Description: Creates a GUI that simulates a traffic monitoring
 * system displaying vehicles, traffic lights, and the time.
 * Class: Class to create and control traffic lights
 * ------------------------------------------------------------
 */
import java.util.Random;

public class TrafficLights {
	Thread thread;
	//class to determine the state of the traffic light
    public enum State {
        started, stopSoon, stopped;
        
        public State getNext() {
            int i = (ordinal() + 1) % values().length;
            return values()[i];
        }
        //sets the initial traffic light state to a random color
        public static State getRandom(Random rand) {
            int i = rand.nextInt(values().length);
            return values()[i];
        }
    }

    private State state;
    private int seconds;
    public final int x;
    //constructor for TrafficLights class
    public TrafficLights(int x, State initialState) {
        this.x = x;
        setState(initialState);
    }
    //method to reads the seconds and set the state
    public void readSeconds() {
        if (seconds >= 1)
            seconds -= 1;
        else setState(state.getNext());
    }
    //methods to return seconds remaining on light
    public int getSeconds() {
        return seconds;
    }
    //method to set the state to started, stopSoon, stopped
    public void setState(State state) {
        this.state = state;

        seconds = switch (state) {
            case stopped -> 10;
            case started -> 10;
            default -> 5;
        };
    }
    //method to return what the state is
    public State getState() {
        return state;
    }

}