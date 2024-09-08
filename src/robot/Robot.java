package robot;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import building.Building;
import building.Building.Direction;
import mailitem.MailItem;
import mailroom.MailRoom;

abstract public class Robot {
    // Static count to generate unique robot IDs
    protected static int count = 1;

    // Capacity of the robot, possibly to limit the number of items it can carry
    public int capacity;

    // Shared mailroom for all robots
    protected static MailRoom mailroom;
    
    // Flag to ensure that the mailroom is only set once
    private static boolean isMailRoomSet = false;

    // Unique identifier for each robot
    final protected String id;
    
    // Current floor and room location of the robot
    protected int floor;
    protected int room;

    // List to store the mail items carried by the robot
    final protected List<MailItem> items = new LinkedList<>();

    // Static method to set the mailroom, but only allows it to be set once
    public static void setMailRoom(MailRoom newMailRoom) {
        if (!isMailRoomSet) {
            mailroom = newMailRoom;
            isMailRoomSet = true;  // Prevents further modification
        } else {
            throw new IllegalStateException("Mailroom can only be set once.");
        }
    }

    // Override of the toString method to display the robot's status
    public String toString() {
        return "Id: " + id + " Floor: " + floor + ", Room: " + room + ", #items: " + numItems() + ", Load: " + capacity ;
    }

    // Constructor that assigns a unique ID to the robot
    protected Robot() {
        this.id = "R" + count++;
    }

    // Getter method for the current floor of the robot
    public int getFloor() { return floor; }

    // Getter method for the current room of the robot
    public int getRoom() { return room; }

    // Checks if the robot is carrying no items
    public boolean isEmpty() { return items.isEmpty(); }

    // Places the robot at a specific location (floor and room) within the building
    public void place(int floor, int room) {
        Building building = Building.getBuilding();  // Get the building instance
        building.place(floor, room, id);  // Place the robot in the building
        this.floor = floor;  // Update the robot's floor
        this.room = room;    // Update the robot's room
    }

    // Moves the robot in a specified direction (UP, DOWN, LEFT, RIGHT)
    protected void move(Building.Direction direction) {
        Building building = Building.getBuilding();  // Get the building instance
        int dfloor, droom;
        switch (direction) {
            case UP    -> {dfloor = floor+1; droom = room;}  // Move up
            case DOWN  -> {dfloor = floor-1; droom = room;}  // Move down
            case LEFT  -> {dfloor = floor;   droom = room-1;}  // Move left
            case RIGHT -> {dfloor = floor;   droom = room+1;}  // Move right
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);  // Handle unexpected direction
        }
        
        // Check if the new position is occupied
        if (!building.isOccupied(dfloor, droom)) {
            building.move(floor, room, direction, id);  // Move the robot within the building
            floor = dfloor;  // Update the robot's new floor
            room = droom;    // Update the robot's new room
            // If the robot reaches the mailroom (floor 0), it returns to the mailroom
            if (floor == 0) {
                System.out.printf("About to return: " + this + "\n");
                mailroom.robotReturn(this);  // Notify the mailroom of the robot's return
            }
        }
    }
    
    // Abstract method to be implemented by subclasses, defines the robot's action each tick
    abstract public void tick();

    // Getter method for the robot's ID
    public String getId() {
        return id;
    }

    // Returns the number of mail items the robot is carrying
    public int numItems() {
        return items.size();
    }

    // Adds a mail item to the robot's list of carried items
    public void add(MailItem item) {
        items.add(item);
    }

    // Sorts the robot's mail items. If reverse is true, sorts in reverse order.
    public void sortItems(boolean reverse) {
        if (reverse) {
            Collections.sort(items, Collections.reverseOrder());  // Sort in reverse order
        } else {
            Collections.sort(items);  // Sort in normal order
        }
    }
}
