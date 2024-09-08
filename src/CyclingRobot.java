import java.util.LinkedList;

public class CyclingRobot extends Robot {

    CyclingRobot() {
        super(); 
    }

    // This method defines the behavior of the CyclingRobot in each time step (tick)
    void tick() {
        Building building = Building.getBuilding();  // Get the building instance
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;  // Cast items to LinkedList for easy access

        // 1. If there are no items to deliver, return to the mailroom or move to the rightmost column
        if (linkedList_item.isEmpty()) {
            // Check if the robot is at the rightmost end (i.e., room at the last column)
            if (room == building.NUMROOMS + 1) {
                move(Building.Direction.DOWN);  // Move down towards the mailroom if at the rightmost column
            } else {
                move(Building.Direction.RIGHT);  // Otherwise, keep moving right to reach the rightmost column
            }
        } else {
            // 2. If there are items to deliver
            // Check if the robot is on the correct floor for the first item
            if (floor == linkedList_item.getFirst().myFloor()) {
                // On the correct floor
                if (room == linkedList_item.getFirst().myRoom()) {
                    // Deliver all relevant items to the current room
                    do {
                        Simulation.deliver(linkedList_item.removeFirst());  // Deliver the item
                    } while (!items.isEmpty() && room == linkedList_item.getFirst().myRoom());  // Continue delivering items for this room
                } else {
                    move(Building.Direction.RIGHT);  // Move right towards the next delivery room
                }
            } else {
                move(Building.Direction.UP);  // Move up towards the correct floor
            }
        }
    }
}
