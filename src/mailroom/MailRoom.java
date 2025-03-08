package mailroom;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

import building.Building;
import building.Building.Direction;
import mailitem.Letter;
import mailitem.MailItem;
import mailitem.Parcel;
import robot.CyclingRobot;
import robot.Robot;
import simulation.Simulation;

import static java.lang.String.format;

abstract public class MailRoom {
    // Enum to define the mode of operation for the MailRoom
    public static enum Mode {CYCLING, FLOORING}
    
    // Array of lists for storing mail items waiting for delivery, indexed by floor
    List<MailItem>[] waitingForDelivery;
    
    // Queues and lists to manage robot states: idle, active, and deactivating
    protected Queue<Robot> idleRobots;
    protected List<Robot> activeRobots;
    protected List<Robot> deactivatingRobots;

    // Constructor for MailRoom
    @SuppressWarnings("unchecked")
    protected MailRoom(int numFloors) {
        // InitialiSe an array of lists to hold mail items for each floor
        waitingForDelivery = new List[numFloors];
        for (int i = 0; i < numFloors; i++) {
            waitingForDelivery[i] = new LinkedList<>();
        }
    }

    // Method to check if there are any mail items waiting for delivery
    public boolean someItems() {
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // Find the floor that has the earliest arriving mail item
    protected int floorWithEarliestItem() {
        int floor = -1;
        int earliest = Simulation.now() + 1; // Initialize to a time in the future
        for (int i = 0; i < Building.getBuilding().NUMFLOORS; i++) {
            if (!waitingForDelivery[i].isEmpty()) {
                LinkedList<MailItem> linkedList = (LinkedList<MailItem>) waitingForDelivery[i];
                MailItem firstItem = linkedList.getFirst();
                int arrival = firstItem.myArrival();
                // Update the floor if an earlier arrival time is found
                if (earliest > arrival) {
                    floor = i;
                    earliest = arrival;
                }
            }
        }
        return floor;
    }

    // Method to load a robot with mail items from a specific floor
    private void loadRobot(int floor, Robot robot) {
        int currentLoad = 0;  // Track the current load on the robot
        ListIterator<MailItem> iter = waitingForDelivery[floor].listIterator();
        while (iter.hasNext()) {  // Iterate through mail items in timestamp order
            MailItem item = iter.next();
            if (item instanceof Letter) {
                // Add letter to robot
                Letter letter = (Letter) item;
                robot.add(letter);
            } else if (item instanceof Parcel) {
                // Check if parcel's weight exceeds robot's capacity
                Parcel parcel = (Parcel) item;
                if (currentLoad + parcel.myWeight() <= robot.maxCapacity) {
                    currentLoad += parcel.myWeight();
                    robot.add(parcel);
                    robot.capacity += parcel.myWeight();
                } else {
                    continue;  // Skip if parcel exceeds capacity
                }
            }
            iter.remove();  // Remove the item once added to the robot
        }
    }

    abstract protected void handleIdleRobotTick();

    // Method to handle each simulation tick (update)
    public void tick() {
        // Update all active robots
        for (Robot activeRobot : activeRobots) {
            if(activeRobot instanceof CyclingRobot){
                System.out.printf("About to tick: " + activeRobot.toString() + "\n");
            }
            activeRobot.tick();
        }

        handleIdleRobotTick();

        // Handle deactivating robots and move them to idle state
        ListIterator<Robot> iter = deactivatingRobots.listIterator();
        while (iter.hasNext()) {
            Robot robot = iter.next();
            iter.remove();
            activeRobots.remove(robot);  // Remove from active list
            idleRobots.add(robot);  // Add to idle list
        }
    }

    // Method to handle when a robot returns from delivery
    public void robotReturn(Robot robot) {
        Building building = Building.getBuilding();
        int floor = robot.getFloor();
        int room = robot.getRoom();
        // Assertions to ensure robot is returning from the correct location
        assert floor == 0 && room == building.NUMROOMS + 1 : format("robot returning from wrong place - floor=%d, room ==%d", floor, room);
        assert robot.isEmpty() : "robot has returned still carrying at least one item";
        building.remove(floor, room);
        deactivatingRobots.add(robot);  // Move the robot to deactivating state
    }

    // Method to handle the arrival of mail items into the mailroom
    public void arrive(List<MailItem> items) {
        for (MailItem item : items) {
            // Add item to the appropriate floor's waiting list
            waitingForDelivery[item.myFloor() - 1].add(item);
            // Log parcel details
            if (item instanceof Parcel) {
                Parcel parcel = (Parcel) item;
                System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %d\n",
                        parcel.myArrival(), parcel.myFloor(), parcel.myRoom(), parcel.myWeight());
            } else if (item instanceof Letter) {
                System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %d\n",
                        item.myArrival(), item.myFloor(), item.myRoom(), 0);
            }
        }
    }

    // Method to dispatch a robot to deliver items
    protected void robotDispatch(Building.Direction direction) {
        System.out.println("Dispatch at time = " + Simulation.now());

        // Determine the room based on the direction
        int room = (direction == Building.Direction.LEFT) ? 0 : Building.getBuilding().NUMROOMS + 1;

        // Check if there is an idle robot and no traffic jam at the start location
        if (!idleRobots.isEmpty() && !Building.getBuilding().isOccupied(0, room)) {
            int fwei = floorWithEarliestItem();  // Get the floor with the earliest mail item
            if (fwei >= 0) {  // If there are items to deliver
                Robot robot = idleRobots.remove();  // Get the next idle robot
                loadRobot(fwei, robot);  // Load the robot with mail items

                // Sort items for delivery based on the direction
                if (direction == Building.Direction.RIGHT) {
                    robot.sortItems(true);
                } else {
                    robot.sortItems(false); // Normal sort for left direction
                }

                activeRobots.add(robot);  // Add robot to active list
                System.out.println("Dispatch @ " + Simulation.now() +
                        " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                robot.place(0, room);  // Place the robot in the start room
            }
        }
    }
}
