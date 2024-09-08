import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FlooringMailRoom extends MailRoom{
    private List<FloorRobot> floorRobots = new ArrayList<>();

    FlooringMailRoom(int numFloors, int numRobots, float robotCapacity){
        super(numFloors, numRobots, robotCapacity);
        activeRobots = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();
        
    // Initialize the idleRobots queue and add CyclingRobots to it
    idleRobots = new LinkedList<>();

    // Create and place the ColumnRobot for the LEFT direction
    ColumnRobot leftColumnRobot = new ColumnRobot(Building.Direction.LEFT);
    idleRobots.add(leftColumnRobot);

    // Create and place the ColumnRobot for the RIGHT direction
    ColumnRobot rightColumnRobot = new ColumnRobot(Building.Direction.RIGHT);
    idleRobots.add(rightColumnRobot);


        // ADD FLOOR ROBOTS
        for (int i = 1; i <= numFloors; i++) {
            FloorRobot floorRobot = new FloorRobot();
            floorRobot.place(i, 1);  // Place the robot on its respective floor
            floorRobots.add(floorRobot);
        }
    }
    
    void transfer(Building.Direction direction, FloorRobot floorRobot){
        // Iterate over the activeRobots list
        for (Robot activeRobot : activeRobots) {
            // Check if the active robot is an instance of ColumnRobot
            if (activeRobot instanceof ColumnRobot) {
                ColumnRobot columnRobot = (ColumnRobot) activeRobot;
    
                // Check if the direction matches
                if (columnRobot.COLUMN == direction) {
                    // Check if the column robot is waiting and at the correct floor
                    columnRobot.transfer(floorRobot);
                }
            }
        }

    }

    @Override
    public void tick() {
        // Floor Robots
        for (FloorRobot activeRobot : floorRobots) {
            System.out.printf("About to tick: " + activeRobot.toString() + "\n");
            activeRobot.tick();
        }
        
        // Column Robots
        super.tick();

    }

    public void columnRobotWaiting(int level, ColumnRobot columnRobot){
        for (FloorRobot floorRobot : floorRobots) {
            if (floorRobot.getFloor() == level){
                floorRobot.addColumnRobot(columnRobot);
                return;
            }
        }
    }

}