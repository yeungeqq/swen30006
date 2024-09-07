import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class CyclingMailRoom extends MailRoom{
 
    // Constructor for CyclingMailRoom
    CyclingMailRoom(int numFloors, int numRobots) {
        // Call the parent constructor to initialize the fields in MailRoom
        super(numFloors, numRobots);
        
        // Initialize the idleRobots queue and add CyclingRobots to it
        idleRobots = new LinkedList<>();
        for (int i = 0; i < numRobots; i++) {
            idleRobots.add(new CyclingRobot(CyclingMailRoom.this));
        }
        initializeRobots();
    }

    @Override
    public void tick() {
        for (Robot activeRobot : activeRobots) {
            System.out.printf("About to tick: " + activeRobot.toString() + "\n"); activeRobot.tick();
        }
        robotDispatch();  // dispatch a robot if conditions are met
        // These are returning robots who shouldn't be dispatched in the previous step
        ListIterator<Robot> iter = deactivatingRobots.listIterator();
        while (iter.hasNext()) {  // In timestamp order
            Robot robot = iter.next();
            iter.remove();
            activeRobots.remove(robot);
            idleRobots.add(robot);
        }
    }
}

