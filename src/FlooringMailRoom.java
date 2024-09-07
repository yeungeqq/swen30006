import java.util.ArrayList;
import java.util.List;


public class FlooringMailRoom extends MailRoom{
    List<ColumnRobot> columnRobots = new ArrayList<>();
    List<FloorRobot> floorRobots = new ArrayList<>();
 
    FlooringMailRoom(int numFloors, int numRobots) {
        super(numFloors, numRobots);
        
        // Initialize floor robots for each floor
        for (int i = 1; i <= numFloors; i++) {
            FloorRobot floorRobot = new FloorRobot();
            floorRobot.place(i, 1);  // Place the robot on its respective floor
            floorRobots.add(floorRobot);
        }

        // Initialize the left and right column robots
        ColumnRobot leftColumnRobot = new ColumnRobot();
        ColumnRobot rightColumnRobot = new ColumnRobot();

        leftColumnRobot.place(1, 0);
        rightColumnRobot.place(1, Building.getBuilding().NUMROOMS + 1);

        // Add the left column robot first, then the right one
        columnRobots.add(leftColumnRobot);  // Left column robot at index 0
        columnRobots.add(rightColumnRobot);  // Right column robot at index 1
    }

    @Override
    public void tick() {
        robotDispatch();  // dispatch column robots if conditions are met

        // Create a new list to hold all robots (column + floor)
        List<Robot> allRobots = new ArrayList<>();
        allRobots.addAll(columnRobots);  // Add column robots
        allRobots.addAll(floorRobots);   // Add floor robots

        // Loop through all robots and execute the tick method
        for (Robot robot : allRobots) {
            System.out.printf("About to tick: %s\n", robot.toString());
            robot.tick();
        }
    }

    @Override
    public void robotDispatch() {
        System.out.println("Dispatch at time = " + Simulation.now());
    
        // Check if there's a waiting column robot and space to dispatch
        ColumnRobot leftColumnRobot = (ColumnRobot) columnRobots.get(0); // Assuming left is at index 0
        ColumnRobot rightColumnRobot = (ColumnRobot) columnRobots.get(1); // Assuming right is at index 1
    
        // If left column robot is waiting and there is space, dispatch the left robot
        if (leftColumnRobot.getWaiting() && !Building.getBuilding().isOccupied(0, 0)) {
            int fwei = floorWithEarliestItem();
            if (fwei >= 0) { // There are items to deliver
                loadRobot(fwei, leftColumnRobot);
                leftColumnRobot.sort();
                System.out.println("Dispatching left column robot");
                leftColumnRobot.place(0, 0);
                leftColumnRobot.setWaiting(false);
            }
        } 
        // Otherwise, check and dispatch the right column robot if waiting
        if (rightColumnRobot.getWaiting() && !Building.getBuilding().isOccupied(0, Building.getBuilding().NUMROOMS + 1)) {
            int fwei = floorWithEarliestItem();
            if (fwei >= 0) { // There are items to deliver
                loadRobot(fwei, rightColumnRobot);
                rightColumnRobot.sort();
                System.out.println("Dispatching right column robot");
                rightColumnRobot.place(0, 0);
                rightColumnRobot.setWaiting(false);
            }
        }
    }

    @Override
    public void robotReturn(Robot robot) {
        Building building = Building.getBuilding();
        int floor = robot.getFloor();
        int room = robot.getRoom();
        
        // Assert the robot is returning from the correct location
        assert floor == 0 && room == building.NUMROOMS + 1 : String.format("robot returning from wrong place - floor=%d, room=%d", floor, room);
        
        // Assert that the robot is empty (no items)
        assert robot.isEmpty() : "robot has returned still carrying at least one item";
        
        // Remove the robot from the building (assuming building.remove() exists)
        building.remove(floor, room);
        
        // Cast the robot to ColumnRobot and set it to waiting
        if (robot instanceof ColumnRobot) {
            ColumnRobot columnRobot = (ColumnRobot) robot;
            columnRobot.setWaiting(true);  // Assuming setWaiting is a method in ColumnRobot
        } else {
            throw new IllegalArgumentException("robotReturn expected a ColumnRobot");
        }
    }
    

    @Override
    void arrive(List<MailItem> items) {
        for (MailItem item : items) {
            waitingForDelivery[item.myFloor()-1].add(item);
            if (item instanceof Parcel) {
                Parcel parcel = (Parcel) item;
                System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %.2f\n",
                        parcel.myArrival(), parcel.myFloor(), parcel.myRoom(), parcel.myWeight());
            } else if (item instanceof Letter) {
                System.out.printf("Item: Time = %d Floor = %d Room = %d Weight = %d\n",
                        item.myArrival(), item.myFloor(), item.myRoom(), 0);
            }
        }
    }

    boolean waitingColumnRobot(int floor, Building.Direction direction) {
        ColumnRobot columnRobot;
    
        // Determine the correct ColumnRobot based on the direction
        if (direction == Building.Direction.LEFT) {
            columnRobot = (ColumnRobot) columnRobots.get(0);  // Left column robot
        } else if (direction == Building.Direction.RIGHT) {
            columnRobot = (ColumnRobot) columnRobots.get(1);  // Right column robot
        } else {
            // If direction is neither LEFT nor RIGHT, return false
            return false;
        }
    
        // Check if the column robot is waiting and at the correct floor
        return columnRobot.getWaiting() && columnRobot.getFloor() == floor;
    }

    ColumnRobot getColumnRobot(Building.Direction direction) {
        // Assuming columnRobots is a list where index 0 is the left robot and index 1 is the right robot
        if (direction == Building.Direction.LEFT) {
            return (ColumnRobot) columnRobots.get(0);  // Return the left column robot
        } else if (direction == Building.Direction.RIGHT) {
            return (ColumnRobot) columnRobots.get(1);  // Return the right column robot
        }
        return null;  // Return null if the direction is invalid
    }
}
