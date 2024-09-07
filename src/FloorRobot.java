import java.util.LinkedList;
import java.util.Queue;

public class FloorRobot extends Robot{
    private Queue<Building.Direction> columnWaiting = new LinkedList<>();
    
    Queue<ColumnRobot> waitingColumnRobots;
    FloorRobot(){
        super();
    }  

    void tick() {
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;
    
        // Get the FlooringMailRoom instance
        FlooringMailRoom flooringMailRoom = (FlooringMailRoom) mailroom;
    
        // Always check for waiting column robots only if they are not already in the queue
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
    
        // Prioritize the left direction if both are waiting
        if (leftWaiting && !leftInQueue) {
            columnWaiting.add(Building.Direction.LEFT);  // Add left direction to the queue
        } else if (rightWaiting && !rightInQueue) {
            columnWaiting.add(Building.Direction.RIGHT);  // Add right direction to the queue
        }
    
        // If the robot has items to deliver, prioritize delivery
        if (!linkedList_item.isEmpty()) {
            // DELIVER ITEMS
            if (floor == linkedList_item.getFirst().myFloor()) {
                // On the correct floor
                if (room == linkedList_item.getFirst().myRoom()) {
                    // Deliver all relevant items to this room
                    do {
                        float itemWeight = 0;
                        if (linkedList_item.getFirst() instanceof Parcel) {
                            Parcel parcel = (Parcel) linkedList_item.getFirst();
                            itemWeight = parcel.myWeight();
                        }
                        Simulation.deliver(linkedList_item.removeFirst());
                        updateCapacity(-itemWeight);  // Update robot's carrying capacity
                    } while (!linkedList_item.isEmpty() && room == linkedList_item.getFirst().myRoom());  // Deliver all items for this room
                } else {
                    // Move towards the next room on this floor
                    move(Building.Direction.RIGHT);
                }
            } else {
                // Move towards the correct floor
                move(Building.Direction.UP);
            }
        } else {
            // If no items, move towards a waiting column robot
            if (!columnWaiting.isEmpty()) {
                Building.Direction direction = columnWaiting.peek();  // Peek at the first waiting direction
    
                // Get the column robot for this direction
                ColumnRobot columnRobot = flooringMailRoom.getColumnRobot(direction);
    
                // Check if the robot is adjacent (assuming floor robot and column robot can be adjacent)
                if (isAdjacentToColumnRobot(direction)) {
                    // Call transfer if adjacent to the column robot
                    columnRobot.transfer(this);
                    columnWaiting.poll();  // Remove the processed direction from the queue
                } else {
                    // Move one step towards the direction
                    if (direction == Building.Direction.LEFT) {
                        move(Building.Direction.LEFT);
                    } else if (direction == Building.Direction.RIGHT) {
                        move(Building.Direction.RIGHT);
                    }
                }
            } else {
                // No items to deliver and no waiting column robots, do nothing
            }
        }
    }
    

    void move(Building.Direction direction) {
        Building building = Building.getBuilding();
        int dfloor, droom;
        switch (direction) {
            case LEFT  -> {dfloor = floor;   droom = room-1;}
            case RIGHT -> {dfloor = floor;   droom = room+1;}
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }
        if (!building.isOccupied(dfloor, droom)) { // If destination is occupied, do nothing
            building.move(floor, room, direction, id);
            floor = dfloor; room = droom;
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
