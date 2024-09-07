import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

abstract public class Robot {
    protected static int count = 1;


    protected static float capacity;
    private static boolean isCapacitySet = false;

    protected static MailRoom mailroom;
    private static boolean isMailRoomSet = false;



    final protected String id;
    protected int floor;
    protected int room;
    final protected List<MailItem> items = new LinkedList<>();


    // Static method to set capacity dynamically, only once
    public static void setCapacity(float newCapacity) {
        if (!isCapacitySet) {
            capacity = newCapacity;
            isCapacitySet = true;  // Mark capacity as set
        } else {
            throw new IllegalStateException("Capacity can only be set once.");
        }
    }

    public static void setMailRoom(MailRoom newMailRoom) {
        if (!isMailRoomSet) {
            mailroom = newMailRoom;
            isMailRoomSet = true;  // Mark capacity as set
        } else {
            throw new IllegalStateException("Capacity can only be set once.");
        }
    }


    public String toString() {
        return "Id: " + id + " Floor: " + floor + ", Room: " + room + ", #items: " + numItems() + ", Load: " + 0 ;
    }

    Robot() {
        this.id = "R" + count++;
    }

    int getFloor() { return floor; }
    int getRoom() { return room; }
    boolean isEmpty() { return items.isEmpty(); }

    public void place(int floor, int room) {
        Building building = Building.getBuilding();
        building.place(floor, room, id);
        this.floor = floor;
        this.room = room;
    }

    void move(Building.Direction direction) {
        Building building = Building.getBuilding();
        int dfloor, droom;
        switch (direction) {
            case UP    -> {dfloor = floor+1; droom = room;}
            case DOWN  -> {dfloor = floor-1; droom = room;}
            case LEFT  -> {dfloor = floor;   droom = room-1;}
            case RIGHT -> {dfloor = floor;   droom = room+1;}
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }
        if (!building.isOccupied(dfloor, droom)) { // If destination is occupied, do nothing
            building.move(floor, room, direction, id);
            floor = dfloor; room = droom;
            if (floor == 0) {
                System.out.printf("About to return: " + this + "\n");
                mailroom.robotReturn(this);
            }
        }
    }
    
    abstract void tick();
    public String getId() {
        return id;
    }

    public int numItems () {
        return items.size();
    }

    public void add(MailItem item) {
        items.add(item);
    }

    // public float getCapacity() {
    //     return capacity;
    // }

    // public void updateCapacity(float itemWeight) {
    //     this.capacity-=itemWeight;
    // }

    void sort() {
        Collections.sort(items);
    }

    void sortReverse() {
        // Assuming 'items' is a list that contains the robot's items
        Collections.sort(items, Collections.reverseOrder());  // Sort in reverse order
    }
    

}
