import java.util.ArrayList;
import java.util.LinkedList;

public class CyclingMailRoom extends MailRoom{
 
    // Constructor for CyclingMailRoom
    CyclingMailRoom(int numFloors, int numRobots, float robotCapacity) {
        // Call the parent constructor to initialize the fields in MailRoom
        super(numFloors, robotCapacity);
        
        // Initialise the idleRobots queue and add CyclingRobots to it
        idleRobots = new LinkedList<>();
        for (int i = 0; i < numRobots; i++) {
            idleRobots.add(new CyclingRobot());
        }
        activeRobots = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();
    }
    
    public void handleIdleRobotTick(){
        if (!idleRobots.isEmpty()) {
            Robot nextIdleRobot = idleRobots.peek();  // Peek at the first idle robot
            Building.Direction direction;
            
            // Determine the direction for the robot
            if (nextIdleRobot instanceof ColumnRobot) {
                ColumnRobot columnRobot = (ColumnRobot) nextIdleRobot;
                direction = columnRobot.COLUMN;  // Use column direction for ColumnRobot
            } else if (nextIdleRobot instanceof CyclingRobot) {
                direction = Building.Direction.LEFT;  // Default direction for CyclingRobot
            } else {
                direction = Building.Direction.LEFT;  // Default for other robot types
            }
            robotDispatch(direction);  // Dispatch the robot
        }
    }

}

