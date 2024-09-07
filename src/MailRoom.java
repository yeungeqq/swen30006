import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;

abstract public class MailRoom {
    public static enum Mode {CYCLING, FLOORING}
    List<MailItem>[] waitingForDelivery;
    Queue<Robot> idleRobots;
    private final int numRobots;

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

    MailRoom(int numFloors, int numRobots) {
        waitingForDelivery = new List[numFloors];
        for (int i = 0; i < numFloors; i++) {
            waitingForDelivery[i] = new LinkedList<>();
        }
        this.numRobots = numRobots;
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
    abstract public void robotDispatch();
    abstract public void robotReturn(Robot robot);
    abstract void arrive(List<MailItem> items);

    
}
