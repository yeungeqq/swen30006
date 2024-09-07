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

    protected Queue<Robot> idleRobots;
    protected List<Robot> activeRobots; 
    protected List<Robot> deactivatingRobots; 

    // Constructor for MailRoom
    MailRoom(int numFloors, int numRobots) {
        // Initialize an array of lists for each floor
        waitingForDelivery = new List[numFloors];
        for (int i = 0; i < numFloors; i++) {
            waitingForDelivery[i] = new LinkedList<>();
        }
        this.numRobots = numRobots;
    }
    
    protected void initializeRobots() {
        activeRobots = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();
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
        ListIterator<MailItem> iter = waitingForDelivery[floor].listIterator();
        while (iter.hasNext()) {  // In timestamp order
            MailItem item = iter.next();
            if (item instanceof Letter) {
                Letter letter = (Letter) item;
                robot.add(letter); // Hand it over if it is Letter no matter what
                System.out.println("Letter loaded.");
            }
            if (item instanceof Parcel) {
                Parcel parcel = (Parcel) item;
                // check the weight limit before hand it over
                if (parcel.myWeight() <= robot.getCapacity()) {
                    robot.add(parcel);
                    // update the capacity of the robot
                    robot.updateCapacity(parcel.myWeight());
                    System.out.printf("Parcel loaded. Capacity updated: %f\n", robot.getCapacity());
                } else {
                    System.out.printf("Cannot load. Item Weight: %f, Available Capacity: %f\n",
                        parcel.myWeight(), robot.getCapacity());
                }
            }
            iter.remove();
        }
    }

    abstract public void tick(); 

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

    public void robotDispatch() {
        System.out.println("Dispatch at time = " + Simulation.now());
        // Need an idle robot and space to dispatch (could be a traffic jam)
        if (!idleRobots.isEmpty() && !Building.getBuilding().isOccupied(0,0)) {
            int fwei = floorWithEarliestItem();
            if (fwei >= 0) {  // Need an item or items to deliver, starting with earliest
                Robot robot = idleRobots.remove();
                loadRobot(fwei, robot);
                // Room order for left to right delivery
                robot.sort();
                activeRobots.add(robot);
                System.out.println("Dispatch @ " + Simulation.now() +
                        " of Robot " + robot.getId() + " with " + robot.numItems() + " item(s)");
                robot.place(0, 0);
            }
        }
    }

    
}
