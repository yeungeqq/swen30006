import java.util.LinkedList;
import java.util.ListIterator;

public class ColumnRobot extends Robot {
    private boolean waiting = true;

    void setWaiting(boolean newWaiting) {
        System.out.printf("Setting waiting status to %b for robot %s on floor %d, room %d\n", newWaiting, id, floor, room);
        this.waiting = newWaiting;
    }

    boolean getWaiting() {
        System.out.printf("Getting waiting status for robot %s on floor %d, room %d: %b\n", id, floor, room, waiting);
        return this.waiting;
    }

    ColumnRobot() {
        super();
        System.out.printf("Initializing ColumnRobot with ID %s on floor %d, room %d\n", id, floor, room);
    }

    void transfer(Robot receivingRobot) {
        System.out.printf("Starting transfer from ColumnRobot %s on floor %d, room %d to robot %s\n", id, floor, room, receivingRobot.id);
        ListIterator<MailItem> iter = this.items.listIterator();  // Iterate over items in the current robot (this)

        while (iter.hasNext()) {
            MailItem item = iter.next();
            System.out.printf("Transferring item to robot %s from ColumnRobot %s\n", receivingRobot.id, id);

            // Transfer the item to the receiving robot
            receivingRobot.add(item);

            // Update the capacity for both robots
            float itemWeight = (item instanceof Parcel) ? ((Parcel) item).myWeight() : 0;
            System.out.printf("Updating capacities. Transferred item weight: %f\n", itemWeight);
            receivingRobot.updateCapacity(-itemWeight);  // Decrease the capacity of the receiving robot
            this.updateCapacity(itemWeight);  // Increase the capacity of the current robot

            // Remove the item from this robot's list
            iter.remove();
            System.out.printf("Removed item from ColumnRobot %s's list. Remaining items: %d\n", id, this.items.size());
        }

        System.out.printf("Finished transfer from ColumnRobot %s\n", id);
    }

    void move(Building.Direction direction) {
        System.out.printf("ColumnRobot %s attempting to move %s from floor %d, room %d\n", id, direction, floor, room);
        Building building = Building.getBuilding();
        int dfloor, droom;

        switch (direction) {
            case UP -> {
                dfloor = floor + 1;
                droom = room;
            }
            case DOWN -> {
                dfloor = floor - 1;
                droom = room;
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + direction);
        }

        if (!building.isOccupied(dfloor, droom)) { // If destination is occupied, do nothing
            System.out.printf("Move valid. ColumnRobot %s moving %s to floor %d, room %d\n", id, direction, dfloor, droom);
            building.move(floor, room, direction, id);
            floor = dfloor;
            room = droom;

            if (floor == 0) {
                System.out.printf("ColumnRobot %s returning to mailroom from floor %d, room %d\n", id, floor, room);
                mailroom.robotReturn(this);
            }
        } else {
            System.out.printf("Move blocked for ColumnRobot %s. Destination (floor %d, room %d) is occupied.\n", id, dfloor, droom);
        }
    }

    void tick() {
        System.out.printf("ColumnRobot %s ticking on floor %d, room %d. Items in list: %d\n", id, floor, room, items.size());

        // Cast items to LinkedList<MailItem> and ensure it's done correctly
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;

        if (linkedList_item.isEmpty()) {
            System.out.printf("No items left. ColumnRobot %s moving towards the mailroom.\n", id);
            // Move towards the mailroom if no items
            move(Building.Direction.DOWN);
        } else if (floor == linkedList_item.getFirst().myFloor()) {
            System.out.printf("ColumnRobot %s waiting for floor robot on correct floor %d\n", id, floor);
            // Wait for floor robot to initiate transfer
        } else {
            System.out.printf("ColumnRobot %s moving towards the correct floor. Current floor: %d, target floor: %d\n", id, floor, linkedList_item.getFirst().myFloor());
            // Move towards the correct floor
            move(Building.Direction.UP);
        }
    }
}
