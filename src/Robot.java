import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

abstract public class Robot {
    protected static int count = 1;
    protected float capacity;
    final protected String id;
    protected int floor;
    protected int room;
    final protected MailRoom mailroom;
    final protected List<MailItem> items = new LinkedList<>();

    public String toString() {
        return "Id: " + id + " Floor: " + floor + ", Room: " + room + ", #items: " + numItems() + ", Load: " + 0 ;
    }

    Robot(MailRoom mailroom, float capacity) {
        this.id = "R" + count++;
        this.mailroom = mailroom;
        this.capacity = capacity;
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

    abstract void move(Building.Direction direction);

    void transfer(Robot robot) {  // Transfers every item assuming receiving robot has capacity
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

    public float getCapacity() {
        return capacity;
    }

    public void updateCapacity(float itemWeight) {
        this.capacity-=itemWeight;
    }

    void sort() {
        Collections.sort(items);
    }

}
