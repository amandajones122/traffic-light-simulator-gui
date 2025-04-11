/** -----------------------------------------------------------
 * Created by: Amanda Jones
 * Date: 5/7/2024
 * File: TrafficGUI.java
 * Description: Creates a GUI that simulates a traffic monitoring
 * system displaying vehicles, traffic lights, and the time.
 * Class: GUI creation class
 * ------------------------------------------------------------
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Map;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.Date;


public class TrafficGUI extends JFrame {
	private static final long serialVersionUID = 0L;

    private final JLabel time = new JLabel("Current time: ");
    public static JLabel timeText = new JLabel();
    private final JButton start = new JButton("Start");
    private final JButton pause = new JButton("Pause");
    private final JButton addCar = new JButton("Add Car");
    private final JButton addIntersection = new JButton("Add Intersection");
    
    private static boolean isRunning;
    private static final AtomicBoolean simIsRunning = new AtomicBoolean(false);
    
    private enum State { stopped, paused, running }
    private State state = State.stopped;
    private final Map<TrafficLights.State, Color> stateColors = Map.of(
        TrafficLights.State.started, Color.green,
        TrafficLights.State.stopSoon, Color.yellow,
        TrafficLights.State.stopped, Color.red);

    private final Timer t = new Timer(100, this::forSeconds);
    private TrafficSim simulator;
    public static ArrayList<Vehicles> cars = new ArrayList<>();
    public static ArrayList<TrafficLights> trafficLights = new ArrayList<>();
    public static Random random = new Random();
    static Thread gui;

    TrafficGUI() {
    	isRunning = Thread.currentThread().isAlive();
        pause.setEnabled(false);
        pause.addActionListener(this::toPause);
        start.addActionListener(this::toStart);
        addCar.addActionListener(this::addVehicle);
        addIntersection.addActionListener(this::addTrafficLight);

        JPanel j = new JPanel();
        j.setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.setLayout(new FlowLayout());

        top.add(time);
        top.add(timeText);
        top.add(start);
        top.add(pause);
        top.add(addCar);
        top.add(addIntersection);
        j.add(top, BorderLayout.NORTH);
        j.add(new Highway(), BorderLayout.CENTER);
        getContentPane().add(j);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Traffic GUI");
        setSize(1500, 500);
        setVisible(true);
    }
    private void addTrafficLight (ActionEvent event) {
    	int numLights = random.nextInt(3, 4), distance = 1000 / numLights;

        for (int i = 0; i < numLights; i++) {
            int x = (i * distance);
            TrafficLights l = new TrafficLights(x, TrafficLights.State.getRandom(random));
            trafficLights.add(l);
        }
    }
    //method to add vehicles with button press
    private void addVehicle (ActionEvent event) {
            int x = 15;
            cars.add(new Vehicles(x, random));
   }
    //method for when pause is pressed
    private void toPause(ActionEvent event) {
        state = switch (state) {
            case running -> {
                pause.setText("Continue");
                t.stop();
                yield State.paused;
            }
            case paused -> {
                pause.setText("Pause");
                t.start();
                yield State.running;
            }
            default -> throw new IllegalStateException();
        };
    }
    //method for when start is pressed
    private void toStart(ActionEvent event) {
        state = switch (state) {
            case stopped -> {
                start.setText("Stop");
                pause.setEnabled(true);
                simulator = new TrafficSim();
                t.start();
                yield State.running;
            }
            case running, paused -> {
                start.setText("Start");
                pause.setText("Pause");
                pause.setEnabled(false);
                t.stop();
                simulator = null;
                yield State.stopped;
            }
        };
    }
    //method to show traffic light seconds remaining
    private void forSeconds (ActionEvent event) {
        simulator.tick();
        getContentPane().repaint();
    }
    //class to establish a linear pathway for the vehicles
    private class Highway extends JPanel {
    	private static final long serialVersionUID = 0L;
        private record TextExtent(
            int y, int x1, int x2
        ) { }
        //establishes traffic light size and drop down line
        private static final int
            LIGHT_RADIUS = 20,
            LIGHT_DIAMETER = 2*LIGHT_RADIUS,
            LIGHT_Y = 150,
            LIGHT_STICK_HEIGHT = 60,
            CAR_Y = 200,
            CAR_TEXT_Y = 270;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (simulator == null) return;

            for (TrafficLights light: simulator.getLights())
                paintTrafficLight(g, light);

            FontMetrics metrics = g.getFontMetrics();
            List<TextExtent> lines = new ArrayList<>();

            for (Vehicles car: simulator.getCars())
                paintVehicle(g, metrics, car, lines);
        }

        private void paintTrafficLight(Graphics g, TrafficLights light) {
            g.setColor(Color.BLACK);
            g.drawString(Integer.toString(light.getSeconds()), light.x, LIGHT_Y - 60);
            Color c = stateColors.get(light.getState());
            g.setColor(c);
            g.fillOval(light.x, LIGHT_Y, LIGHT_DIAMETER, LIGHT_DIAMETER);
            g.drawLine(
                light.x + LIGHT_RADIUS, LIGHT_Y + LIGHT_RADIUS,
                light.x + LIGHT_RADIUS, LIGHT_Y + LIGHT_STICK_HEIGHT
            );
        }

        private void paintVehicle(Graphics g, FontMetrics metrics, Vehicles car, List<TextExtent> lines) {
            Color c = car.getColor();
            g.setColor(c);
            int x = (int)car.getXValue();
            g.fillRect(x, CAR_Y, 20, 10);

            String desc = "(%.0f, 0) %.2f mph".formatted(
                car.getXValue(), car.getSpeed()
            );
            int w = metrics.stringWidth(desc),
                x2 = x + w,
                y;

            for (y = 0;; y++) {
                boolean found = false;
                for (TextExtent text: lines) {
                    if (y == text.y && x < text.x2 && x2 > text.x1) {
                        found = true;
                        break;
                    }
                }
                if (!found) break;
            }

            lines.add(new TextExtent(y, x, x2));

            g.setColor(Color.black);
            g.drawString(
                desc, x,
                CAR_TEXT_Y + metrics.getHeight()*y
            );
        }
    }
    public class TrafficSim {
        private int ticks = 0;

        public TrafficSim() {
        	//this sets the required distance and initiates lights
            int numLights = random.nextInt(1, 4), distance = 1000 / numLights;

            for (int i = 0; i < numLights; i++) {
                int x = (i + 1) * distance;
                TrafficLights l = new TrafficLights(x, TrafficLights.State.getRandom(random));
                trafficLights.add(l);
            }

            for (int i = 0; i < 3; i++) {
                int x = random.nextInt(500);
                cars.add(new Vehicles(x, random));
            }
        }

        public List<TrafficLights> getLights() {
            return Collections.unmodifiableList(trafficLights);
        }

        public List<Vehicles> getCars() {
            return Collections.unmodifiableList(cars);
        }

        private TrafficLights getLight(double x) {
            for (TrafficLights light: trafficLights) {
                double diff = light.x - x;
                if (diff < 5 && diff > -1)
                    return light;
            }
            return null;
        }
        //method to remove vehicles once they've traversed the axis
        public void tick() {
            List<Vehicles> toRemove = new ArrayList<>();
            for (Vehicles car: cars) {
                double x = car.getXValue();
                TrafficLights light = getLight(x);
                car.setLight(light);
                car.distance();
                if (car.finished())
                    toRemove.add(car);
            }
            for (Vehicles car: toRemove)
                cars.remove(car);
            ticks++;
            if (ticks % 10 == 0) {
                for (TrafficLights light: trafficLights)
                    light.readSeconds();
            }
        }
    }
    public static void main(String[] args) {
        new TrafficGUI();
    }
}