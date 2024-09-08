import java.util.LinkedList;
import java.util.PriorityQueue;

public class FloorRobot extends Robot {

    // A priority queue to manage the waiting ColumnRobots
    private PriorityQueue<ColumnRobot> columnWaiting = new PriorityQueue<>();

    // Method to add a ColumnRobot to the waiting queue
    public void addColumnRobot(ColumnRobot columnRobot) {
        columnWaiting.add(columnRobot);
    }

    // Constructor for FloorRobot, calls the Robot superclass constructor
    FloorRobot() {
        super();
    }

    // The tick method defines what the robot should do in each time step
    void tick() {
        // Cast the items list to a LinkedList for easy removal of the first item
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;

        // 1. If there are no parcels and no waiting column robots, do nothing
        if (linkedList_item.isEmpty() && columnWaiting.isEmpty()) {
            return;  // No action needed
        }
        
        // 2. If there are parcels to deliver, prioritiSe delivery over waiting column robots
        else if (!linkedList_item.isEmpty()) {
            // Check if the robot is already at the target room
            if (room == linkedList_item.getFirst().myRoom()) {
                // Deliver all items to the current room
                do {
                    if (linkedList_item.getFirst() instanceof Parcel) {
                        Parcel p = (Parcel) linkedList_item.getFirst();
                        capacity -= p.myWeight();  // Reduce capacity by the weight of the parcel
                    }
                    // Deliver the item using the Simulation class
                    Simulation.deliver(linkedList_item.removeFirst());
                } while (!linkedList_item.isEmpty() && room == linkedList_item.getFirst().myRoom());
            } else {
                // Move towards the next room on the floor based on the item's room
                int targetRoom = linkedList_item.getFirst().myRoom();
                if (targetRoom < room) {
                    move(Building.Direction.LEFT);  // Move left to the target room
                } else {
                    move(Building.Direction.RIGHT);  // Move right to the target room
                }
            }
        }
        // 3. If there are no parcels but waiting column robots, move towards the column robot
        else if (!columnWaiting.isEmpty()) {
            Building.Direction direction = columnWaiting.peek().COLUMN;  // Get the direction of the waiting column robot
            int targetColumnRoom = (direction == Building.Direction.LEFT) ? 1 : Building.getBuilding().NUMROOMS;
            if (room != targetColumnRoom) {
                move(direction);  // Move towards the column robot
            } else {
                // Once adjacent to the column robot, transfer items and remove the robot from the queue
                columnWaiting.peek().transfer(this);
                columnWaiting.poll();
            }
        }
    }

    // Method to check if the FloorRobot is adjacent to a column robot in a specific direction
    boolean isAdjacentToColumnRobot(Building.Direction direction) {
        Building building = Building.getBuilding();
        int floor = this.getFloor();  // Current floor of the robot
        int room = this.getRoom();    // Current room of the robot

        // Assuming the left ColumnRobot is in room 0, and the right ColumnRobot is in the last room on the floor
        int leftmostRoom = 0;
        int rightmostRoom = Building.getBuilding().NUMROOMS + 1;  // Last room number

        if (direction == Building.Direction.LEFT) {
            // Check if the FloorRobot is adjacent to the left column robot (room 0)
            return room == leftmostRoom + 1 && !building.isOccupied(floor, leftmostRoom);
        } else if (direction == Building.Direction.RIGHT) {
            // Check if the FloorRobot is adjacent to the right column robot (rightmost room)
            return room == rightmostRoom - 1 && !building.isOccupied(floor, rightmostRoom);
        }

        return false;  // Return false if the direction is invalid or not adjacent
    }
}
