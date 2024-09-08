package mailroom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import building.Building;
import robot.ColumnRobot;
import robot.FloorRobot;
import robot.Robot;

public class FlooringMailRoom extends MailRoom {
    // List to store all floor robots
    private List<FloorRobot> floorRobots = new ArrayList<>();

    // Constructor to initialize the FlooringMailRoom with a specific number of floors and robot capacity
    public FlooringMailRoom(int numFloors, float robotCapacity) {
        super(numFloors, robotCapacity);
        
        // Initialize the lists for active, deactivating, and idle robots
        activeRobots = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();
        idleRobots = new LinkedList<>();  // Use a LinkedList for managing idle robots
        
        // Create and add ColumnRobots for the LEFT and RIGHT directions to the idle robots queue
        ColumnRobot leftColumnRobot = new ColumnRobot(Building.Direction.LEFT);
        idleRobots.add(leftColumnRobot);
        
        ColumnRobot rightColumnRobot = new ColumnRobot(Building.Direction.RIGHT);
        idleRobots.add(rightColumnRobot);

        // Create and add FloorRobots for each floor
        for (int i = 1; i <= numFloors; i++) {
            FloorRobot floorRobot = new FloorRobot();
            floorRobot.place(i, 1);  // Place the FloorRobot on the respective floor at room 1
            floorRobots.add(floorRobot);  // Add the FloorRobot to the list
        }
    }

    // Override the tick method to define the behavior of the mailroom in each time step
    @Override
    public void tick() {
        // Process each FloorRobot's tick (behavior)
        for (FloorRobot activeRobot : floorRobots) {
            System.out.printf("About to tick: " + activeRobot.toString() + "\n");
            activeRobot.tick();
        }
        
        // Call the tick method of the parent class (MailRoom) to handle ColumnRobots
        super.tick();
    }

    // Method to notify the FloorRobot on a specific level that a ColumnRobot is waiting
    public void columnRobotWaiting(int level, ColumnRobot columnRobot) {
        for (FloorRobot floorRobot : floorRobots) {
            if (floorRobot.getFloor() == level) {
                // Add the ColumnRobot to the waiting list of the FloorRobot on the specified level
                floorRobot.addColumnRobot(columnRobot);
                return;
            }
        }
    }

    // Method to handle dispatching idle robots
    protected void handleIdleRobotTick() {
        // If there are idle robots, dispatch them
        if (!idleRobots.isEmpty()) {
            List<Robot> robotsToDispatch = new ArrayList<>(idleRobots);  // Copy the list of idle robots
            for (Robot nextIdleRobot : robotsToDispatch) {
                Building.Direction direction = null;

                // Check if the robot is a ColumnRobot and determine its direction
                if (nextIdleRobot instanceof ColumnRobot) {
                    ColumnRobot columnRobot = (ColumnRobot) nextIdleRobot;
                    direction = columnRobot.COLUMN;  // Get the direction of the ColumnRobot
                }
                
                // Dispatch the robot to handle tasks in the given direction
                robotDispatch(direction);
            }
        }
    }
}
