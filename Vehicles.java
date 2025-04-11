/** -----------------------------------------------------------
 * Created by: Amanda Jones
 * Date: 5/7/2024
 * File: Vehicles.java
 * Description: Creates a GUI that simulates a traffic monitoring
 * system displaying vehicles, traffic lights, and the time.
 * Class: Class to create and control vehicles
 * ------------------------------------------------------------
 */
import java.awt.Color;
import java.util.Random;

public class Vehicles {
	//establishes the colors the vehicles can be
    private static final Color[] colors = {
        Color.black, Color.blue, Color.lightGray, Color.gray, 
        Color.orange, Color.magenta, Color.pink };
    private final double speed;
    private final Color color;
    private double x;
    private TrafficLights light;
    //constructor for Vehicle class
    public Vehicles (double x, double speed, Color color) {
        this.x = x;
        this.speed = speed;
        this.color = color;
    }
    //constructor for Vehicles class
    public Vehicles (double x, Random rand) {
        this(x, randomSpeed(rand), randomColor(rand));
    }
    //method to assign a random speed to the vehicles
    private static double randomSpeed(Random rand) {
        return rand.nextDouble(0, 25);
    }
    //method to assign a random color to the vehicles
    private static Color randomColor(Random rand) {
        return colors[rand.nextInt(colors.length)];
    }
    //method get the x value
    public double getXValue() {
        return x;
    }
    //method to get the vehicle speed
    public double getSpeed() {
        if (!isStopped()) {
            return speed;
        }
        return 0;
    }
    //method to get the vehicle color
    public Color getColor() {
        return color;
    }
    //method to stop vehicle at red light
    private boolean isStopped() {
        return light != null && light.getState() == TrafficLights.State.stopped;
    }
    //method to calculate the x value
    public void distance() {
        x += getSpeed() * 0.1;
    }
    //method to set traffic light
    public void setLight(TrafficLights light) {
        this.light = light;
    }
    //method for when vehicles traverse the entire linear area
    public boolean finished() {
        return x >= 1200;
    }
}