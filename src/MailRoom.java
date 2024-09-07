import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import static java.lang.String.format;

abstract public class MailRoom {
    public static enum Mode {CYCLING, FLOORING}
    List<MailItem>[] waitingForDelivery;
    private final int numRobots;
    private final float ROBOTCAPACITY;

    protected Queue<Robot> idleRobots;
    protected List<Robot> activeRobots; 
    protected List<Robot> deactivatingRobots; 

    // Constructor for MailRoom
    MailRoom(int numFloors, int numRobots, float robotCapacity) {
        // Initialize an array of lists for each floor
        waitingForDelivery = new List[numFloors];
        for (int i = 0; i < numFloors; i++) {
            waitingForDelivery[i] = new LinkedList<>();
        }
        this.numRobots = numRobots;
        ROBOTCAPACITY = robotCapacity;
    }

    public boolean someItems() {
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                    return true;
            }
        }
        return false;
    }

    protected int floorWithEarliestItem() {
        int floor = -1;
        int earliest = Simulation.now() + 1;
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            // System.out.println(waitingForDelivery[i].toString());
            if (!waitingForDelivery[i].isEmpty()) {
                LinkedList<MailItem> linkedList = (LinkedList<MailItem>) waitingForDelivery[i];
                MailItem firstItem = linkedList.getFirst();
                int arrival = firstItem.myArrival();
                if (earliest > arrival) {
                    floor = i;
                    earliest = arrival;
                }
            }
        }
        return floor;
    }

    void loadRobot(int floor, Robot robot) {
        double currentLoad = 0;  // Temporary tally of the total load added to the robot
        ListIterator<MailItem> iter = waitingForDelivery[floor].listIterator();
        while (iter.hasNext()) {  // In timestamp order
            MailItem item = iter.next();
            if (item instanceof Letter) {
                Letter letter = (Letter) item;
                robot.add(letter); 
            }
            else if (item instanceof Parcel) {
                Parcel parcel = (Parcel) item;
                // check the weight limit before hand it over
                if (currentLoad + parcel.myWeight() <= ROBOTCAPACITY) {
                    currentLoad += parcel.myWeight();
                    robot.add(parcel);
                    robot.capacity += parcel.myWeight();
                
                }
                else{  continue;}
            }
            iter.remove();
            
        }
    }
    


    public void tick() {
        // Tick all active robots
        for (Robot activeRobot : activeRobots) {
            //System.out.printf("About to tick: " + activeRobot.toString() + "\n");
            activeRobot.tick();
        }
    
        // Check the direction of the next idle robot
        if(this instanceof CyclingMailRoom){
            if (!idleRobots.isEmpty()) {
                Robot nextIdleRobot = idleRobots.peek();  // Peek at the first robot in the queue
                Building.Direction direction;
        
                // Check if the robot is a ColumnRobot or CyclingRobot
                if (nextIdleRobot instanceof ColumnRobot) {
                    // If it's a ColumnRobot, use its COLUMN direction
                    ColumnRobot columnRobot = (ColumnRobot) nextIdleRobot;
                    direction = columnRobot.COLUMN;  // Get the COLUMN direction
                } else if (nextIdleRobot instanceof CyclingRobot) {
                    // If it's a CyclingRobot, use a default or predefined direction (e.g., LEFT)
                    direction = Building.Direction.LEFT;  // Default or specific logic for CyclingRobot
                } else {
                    // Default case, in case more robot types are added
                    direction = Building.Direction.LEFT;  // Default direction
                }
        
                // Dispatch a robot if conditions are met
                robotDispatch(direction);
            }
        }
        else {
            if (!idleRobots.isEmpty()) {

                // Create a copy of the idleRobots queue to avoid modifying it during iteration
                List<Robot> robotsToDispatch = new ArrayList<>(idleRobots);

                // Iterate through the copy of idle robots
                for (Robot nextIdleRobot : robotsToDispatch) {
                    Building.Direction direction = null;

                    // Check if the robot is a ColumnRobot
                    if (nextIdleRobot instanceof ColumnRobot) {
                        ColumnRobot columnRobot = (ColumnRobot) nextIdleRobot;
                        direction = columnRobot.COLUMN;  // Get the COLUMN direction
                    }

                    // Dispatch the robot if conditions are met
                    robotDispatch(direction);
                }
            }
        }
        
    
        // Handle returning (deactivating) robots and move them to idleRobots
        ListIterator<Robot> iter = deactivatingRobots.listIterator();
        while (iter.hasNext()) {  // In timestamp order
            Robot robot = iter.next();
            iter.remove();
            activeRobots.remove(robot);
            idleRobots.add(robot);
        }
    }

    public void robotReturn(Robot robot) {
        Building building = Building.getBuilding();
        int floor = robot.getFloor();
        int room = robot.getRoom();
        assert floor == 0 && room == building.NUMROOMS+1: format("robot returning from wrong place - floor=%d, room ==%d", floor, room);
        assert robot.isEmpty() : "robot has returned still carrying at least one item";
        building.remove(floor, room);
        deactivatingRobots.add(robot);
    }

    void arrive(List<MailItem> items) {
        for (MailItem item : items) {
            waitingForDelivery[item.myFloor()-1].add(item);
            if (item instanceof Parcel) {
                Parcel parcel = (Parcel) item;
                System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %.2f\n",
                        parcel.myArrival(), parcel.myFloor(), parcel.myRoom(), parcel.myWeight());
            } else if (item instanceof Letter) {
                System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %d\n",
                        item.myArrival(), item.myFloor(), item.myRoom(), 0);
            }
        }
    }

    public void robotDispatch(Building.Direction direction) {
        System.out.println("Dispatch at time = " + Simulation.now());
    
        // Determine the room based on the direction
        int room = (direction == Building.Direction.LEFT) ? 0 : Building.getBuilding().NUMROOMS + 1;
    
        // Check if there's an idle robot and if there's no traffic jam at the start location
        if (!idleRobots.isEmpty() && !Building.getBuilding().isOccupied(0, room)) {
            int fwei = floorWithEarliestItem();
            if (fwei >= 0) {  // If there are items to deliver

                Robot robot = idleRobots.remove();
                loadRobot(fwei, robot);
    
                // Sort the items based on the direction
                if (direction == Building.Direction.RIGHT) {
                    robot.sortReverse();  // Sort in reverse order for right direction
                } else {
                    robot.sort();  // Sort normally for left direction
                }
    
                activeRobots.add(robot);
                System.out.println("Dispatch @ " + Simulation.now() +
                        " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                
                // Place the robot in the correct room
                robot.place(0, room);
            }
        }
    }
    
}
