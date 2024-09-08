package robot;
import java.util.LinkedList;
import java.util.ListIterator;

import building.Building;

import mailitem.MailItem;
import mailitem.Parcel;
import mailroom.FlooringMailRoom;
import simulation.Simulation;

public class ColumnRobot extends Robot implements Comparable<ColumnRobot>{
    private int waitingSince;
   
    // Declare COLUMN as a final variable of type Building.Direction
    public final Building.Direction COLUMN;

    public ColumnRobot(Building.Direction direction) {
        // Call the parent constructor (super())
        super(); 

        // Initialise COLUMN with the passed direction
        this.COLUMN = direction;
    }
    
    @Override
    public void tick() {
        FlooringMailRoom fl = (FlooringMailRoom) mailroom;
        // Cast items to LinkedList<MailItem> and ensure it's done correctly
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;

        if (linkedList_item.isEmpty()) {
            // Check if the robot is not already on floor zero
            if (getFloor() > 0) {
                // Move towards the mailroom (down) only if it's not on floor zero
                move(Building.Direction.DOWN);
            }
        } else if (floor == linkedList_item.getFirst().myFloor()) {
        } else {
            // Move towards the correct floor
            move(Building.Direction.UP);
            if (floor == linkedList_item.getFirst().myFloor()){
                waitingSince = Simulation.now();
                fl.columnRobotWaiting(floor, this);
            }
        }
    }
    
    protected void transfer(Robot receivingRobot) {
        ListIterator<MailItem> iter = this.items.listIterator();  // Iterate over items in the current robot (this)

        while (iter.hasNext()) {
            MailItem item = iter.next();
            
            // Transfer the item to the receiving robot
            receivingRobot.add(item);
            if (item instanceof Parcel){
                Parcel p = (Parcel) item;
                receivingRobot.capacity += p.myWeight();
                capacity -= p.myWeight();                
            }

            // Remove the item from this robot's list
            iter.remove();
        }
    }

    // Implement the compareTo method for PriorityQueue sorting
    @Override
    public int compareTo(ColumnRobot otherRobot) {
        // First, compare based on the waitingSince (tick) value
        if (this.waitingSince != otherRobot.waitingSince) {
            return Integer.compare(this.waitingSince, otherRobot.waitingSince);
        }

        // If the ticks are the same, compare based on COLUMN direction (LEFT comes before RIGHT)
        if (this.COLUMN == Building.Direction.LEFT && otherRobot.COLUMN == Building.Direction.RIGHT) {
            return -1; // LEFT comes before RIGHT
        } else if (this.COLUMN == Building.Direction.RIGHT && otherRobot.COLUMN == Building.Direction.LEFT) {
            return 1; // RIGHT comes after LEFT
        }

        // If both have the same direction, they are considered equal
        return 0;
    }
}