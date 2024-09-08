package mailroom;
import java.util.ArrayList;
import java.util.LinkedList;

import building.Building;
import robot.ColumnRobot;
import robot.CyclingRobot;
import robot.Robot;

public class CyclingMailRoom extends MailRoom {

    public CyclingMailRoom(int numFloors, int numRobots, float robotCapacity) {
        // Call the parent constructor (MailRoom) to initialize the number of floors and robot capacity
        super(numFloors, robotCapacity);
        
        // Initialise the idleRobots queue and populate it with CyclingRobots
        idleRobots = new LinkedList<>();
        for (int i = 0; i < numRobots; i++) {
            idleRobots.add(new CyclingRobot());  // Add the specified number of CyclingRobots to the idle queue
        }
        
        // Initialise lists to track active and deactivating robots
        activeRobots = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();
    }

    // Method to handle dispatching idle robots when they are available
    @Override
    protected void handleIdleRobotTick() {
        if (!idleRobots.isEmpty()) {
            Robot nextIdleRobot = idleRobots.peek();  // Peek at the first idle robot in the queue (but don't remove it)
            Building.Direction direction;

            // Determine the appropriate direction for the robot
            if (nextIdleRobot instanceof ColumnRobot) {
                ColumnRobot columnRobot = (ColumnRobot) nextIdleRobot;
                direction = columnRobot.COLUMN;  // Use the specific column direction for a ColumnRobot
            } else if (nextIdleRobot instanceof CyclingRobot) {
                direction = Building.Direction.LEFT;  // Default dispatch direction for CyclingRobots
            } else {
                direction = Building.Direction.LEFT;  // Default direction for any other robot types
            }

            // Dispatch the robot with the determined direction
            robotDispatch(direction);
        }
    }
}
