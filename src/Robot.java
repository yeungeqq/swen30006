import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


abstract public class Robot {
    protected static int count = 1;

    public int capacity;

    protected static MailRoom mailroom;
    private static boolean isMailRoomSet = false;

    final protected String id;
    protected int floor;
    protected int room;
    final protected List<MailItem> items = new LinkedList<>();

    public static void setMailRoom(MailRoom newMailRoom) {
        if (!isMailRoomSet) {
            mailroom = newMailRoom;
            isMailRoomSet = true;  
        } else {
            throw new IllegalStateException("Capacity can only be set once.");
        }
    }

    public String toString() {
        return "Id: " + id + " Floor: " + floor + ", Room: " + room + ", #items: " + numItems() + ", Load: " + capacity ;
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

    void sortItems(boolean reverse) {
        if (reverse) {
            Collections.sort(items, Collections.reverseOrder());  // Sort in reverse order
        } else {
            Collections.sort(items);  // Sort in normal order
        }
    }
}
