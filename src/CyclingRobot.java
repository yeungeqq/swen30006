import java.util.LinkedList;

public class CyclingRobot extends Robot{

    CyclingRobot(MailRoom mailroom, float capacity){
        super(mailroom, capacity);
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
                            float itemWeight = 0;
                            if (linkedList_item.getFirst() instanceof Parcel) {
                                Parcel parcel = (Parcel) linkedList_item.getFirst();
                                itemWeight = parcel.myWeight();
                            }
                            Simulation.deliver(linkedList_item.removeFirst());
                            updateCapacity(-itemWeight);
                        } while (!items.isEmpty() && room == linkedList_item.getFirst().myRoom());
                    } else {
                        move(Building.Direction.RIGHT); // move towards next delivery
                    }
                } else {
                    move(Building.Direction.UP); // move towards floor
                }
            }
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
    
}
