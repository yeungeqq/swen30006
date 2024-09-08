import java.util.LinkedList;
import java.util.PriorityQueue;

public class FloorRobot extends Robot{

    private PriorityQueue<ColumnRobot> columnWaiting = new PriorityQueue<>();

    public void addColumnRobot(ColumnRobot columnRobot) {
        columnWaiting.add(columnRobot);
    }

    FloorRobot(){
        super();
    }

    void tick() {
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;
                
        // 1. If no parcels and no waiting robots, do nothing
        if (linkedList_item.isEmpty() && columnWaiting.isEmpty()) {
            // No parcels to deliver and no waiting column robots, so do nothing
            return;
        }
    
        // 2. If there are parcels to deliver, prioritize delivery
        else if (!linkedList_item.isEmpty()) {
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
        else if (!columnWaiting.isEmpty()) {
            Building.Direction direction = columnWaiting.peek().COLUMN;  // Get the direction of the waiting column robot
            int targetColumnRoom = (direction == Building.Direction.LEFT) ? 1 : Building.getBuilding().NUMROOMS;
            if(room != targetColumnRoom){
                move(direction);
            }
            else {
                columnWaiting.peek().transfer(this);
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
