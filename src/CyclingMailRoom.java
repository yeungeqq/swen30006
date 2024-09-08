import java.util.ArrayList;
import java.util.LinkedList;

public class CyclingMailRoom extends MailRoom{
 
    // Constructor for CyclingMailRoom
    CyclingMailRoom(int numFloors, int numRobots, float robotCapacity) {
        // Call the parent constructor to initialize the fields in MailRoom
        super(numFloors, numRobots, robotCapacity);
        
        // Initialize the idleRobots queue and add CyclingRobots to it
        idleRobots = new LinkedList<>();
        for (int i = 0; i < numRobots; i++) {
            idleRobots.add(new CyclingRobot());
        }
        activeRobots = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();
    }
}

