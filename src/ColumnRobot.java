import java.util.LinkedList;
import java.util.ListIterator;

public class ColumnRobot extends Robot {
    private boolean waiting = false;

    void setWaiting(boolean newWaiting) {
        this.waiting = newWaiting;
    }

    boolean getWaiting() {
        return this.waiting;
    }
    
    // Declare COLUMN as a final variable of type Building.Direction
    public final Building.Direction COLUMN;

    // Constructor for ColumnRobot that takes a direction
    ColumnRobot(Building.Direction direction) {
        // Call the parent constructor (super())
        super(); 

        // Initialize COLUMN with the passed direction
        this.COLUMN = direction;
    }

    @Override
    void tick() {

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
                setWaiting(true);
                }
        }
    }
    void transfer(Robot receivingRobot) {
        ListIterator<MailItem> iter = this.items.listIterator();  // Iterate over items in the current robot (this)

        while (iter.hasNext()) {
            MailItem item = iter.next();

            // Transfer the item to the receiving robot
            receivingRobot.add(item);

            // Remove the item from this robot's list
            iter.remove();
        }
    }
}