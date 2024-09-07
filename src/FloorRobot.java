import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;

public class FloorRobot extends Robot{

    private Queue<Building.Direction> columnWaiting = new LinkedList<>();

    // private boolean waiting = true;

    // void setWaiting(boolean newWaiting) {
    //     System.out.printf("Setting waiting status to %b for robot %s on floor %d, room %d\n", newWaiting, id, floor, room);
    //     this.waiting = newWaiting;
    // }

    // boolean getWaiting() {
    //     System.out.printf("Getting waiting status for robot %s on floor %d, room %d: %b\n", id, floor, room, waiting);
    //     return this.waiting;
    // }
    
    FloorRobot(){
        super();
    }

    public void transfer(Robot robot) {  // Transfers every item assuming receiving robot has capacity
        ListIterator<MailItem> iter = robot.items.listIterator();
        while(iter.hasNext()) {
            MailItem item = iter.next();
            if (item instanceof Letter) {
                this.add(item); //Hand it over if it is Letter no matter what
            }
            if (item instanceof Parcel) {
                // check the weight limit before hand it over
                // update the avaiolable capacity of the robot
            }
            iter.remove();
        }
    }

    void tick() {
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;
        
        // Get the FlooringMailRoom instance
        FlooringMailRoom flooringMailRoom = (FlooringMailRoom) mailroom;
    
        // Check if there are any waiting column robots
        boolean leftInQueue = columnWaiting.contains(Building.Direction.LEFT);
        boolean rightInQueue = columnWaiting.contains(Building.Direction.RIGHT);
        
        // Query the mailroom only if the directions are not already in the queue
        boolean leftWaiting = false;
        boolean rightWaiting = false;
        
        if (!leftInQueue) {
            leftWaiting = flooringMailRoom.waitingColumnRobot(getFloor(), Building.Direction.LEFT);
        }
        
        if (!rightInQueue) {
            rightWaiting = flooringMailRoom.waitingColumnRobot(getFloor(), Building.Direction.RIGHT);
        }
    
        // Prioritize column robots based on waiting time and left before right
        if (leftWaiting && !leftInQueue) {
            columnWaiting.add(Building.Direction.LEFT);
        } else if (rightWaiting && !rightInQueue) {
            columnWaiting.add(Building.Direction.RIGHT);
        }
    
        // 1. If no parcels and no waiting robots, do nothing
        if (linkedList_item.isEmpty() && columnWaiting.isEmpty()) {
            // No parcels to deliver and no waiting column robots, so do nothing
            return;
        }
    
        // 2. If there are parcels to deliver, prioritize delivery
        if (!linkedList_item.isEmpty()) {
            if (room == linkedList_item.getFirst().myRoom()) {
                // On the correct floor
                    // Deliver all relevant items to this room
                    do {
                        if(linkedList_item.getFirst() instanceof Parcel){
                            Parcel p = (Parcel) linkedList_item.getFirst();
                            capacity -= p.myWeight();
                        }
                        Simulation.deliver(linkedList_item.removeFirst());
                    } while (!linkedList_item.isEmpty() && room == linkedList_item.getFirst().myRoom());  // Deliver all items for this room
            } else {
                // Move towards the next room on this floor based on direction
                int targetRoom = linkedList_item.getFirst().myRoom();
                if (targetRoom < room) {
                    move(Building.Direction.LEFT);  // Move left to reach the target room
                } else {
                    move(Building.Direction.RIGHT);  // Move right to reach the target room
                }
            }
            }
        // 3. If no parcels but waiting column robots, move towards the column robot
        if (!columnWaiting.isEmpty()) {
            Building.Direction direction = columnWaiting.peek();  // Get the direction of the waiting column robot
            int targetColumnRoom = (direction == Building.Direction.LEFT) ? 1 : Building.getBuilding().NUMROOMS;
            if(room != targetColumnRoom){
                move(direction);
            }
            else {
                flooringMailRoom.transfer(direction, this);
                columnWaiting.poll();  
            }
        }
    }
    
    boolean isAdjacentToColumnRobot(Building.Direction direction) {
        Building building = Building.getBuilding();
        int floor = this.getFloor();  // FloorRobot's current floor
        int room = this.getRoom();    // FloorRobot's current room
        
        // Assuming that the left ColumnRobot is always in room 0 and the right ColumnRobot is in the last room on the floor
        int leftmostRoom = 0;
        int rightmostRoom = Building.getBuilding().NUMROOMS + 1;  // Access the static method in a static way
    
        if (direction == Building.Direction.LEFT) {
            // Check if the FloorRobot is adjacent to the left column robot (room 0)
            return room == leftmostRoom + 1 && !building.isOccupied(floor, leftmostRoom);
        } else if (direction == Building.Direction.RIGHT) {
            // Check if the FloorRobot is adjacent to the right column robot (rightmost room)
            return room == rightmostRoom - 1 && !building.isOccupied(floor, rightmostRoom);
        }
    
        return false;  // Not adjacent if direction is invalid
    }
}
