import java.util.LinkedList;

public class CyclingRobot extends Robot{

    // Constructor for ColumnRobot that takes a direction
    CyclingRobot() {
        super(); 
    }
    
    void tick() {
        Building building = Building.getBuilding();
        LinkedList<MailItem> linkedList_item = (LinkedList<MailItem>) items;
        if (linkedList_item.isEmpty()) {
            // Return to MailRoom
            if (room == building.NUMROOMS + 1) { // in right end column
                move(Building.Direction.DOWN);  //move towards mailroom
            } else {
                move(Building.Direction.RIGHT); // move towards right end column
            }
        } else {
            // Items to deliver
            if (floor == linkedList_item.getFirst().myFloor()) {
                // On the right floor
                if (room == linkedList_item.getFirst().myRoom()) { //then deliver all relevant items to that room
                    do {
                        Simulation.deliver(linkedList_item.removeFirst());
                    } while (!items.isEmpty() && room == linkedList_item.getFirst().myRoom());
                } else {
                    move(Building.Direction.RIGHT); // move towards next delivery
                }
            } else {
                move(Building.Direction.UP); // move towards floor
            }
        }
    }    
}
