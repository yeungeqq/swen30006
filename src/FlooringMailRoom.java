import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FlooringMailRoom extends MailRoom{
    private List<FloorRobot> floorRobots = new ArrayList<>();

    FlooringMailRoom(int numFloors, float robotCapacity){
        super(numFloors, robotCapacity);
        activeRobots = new ArrayList<>();
        deactivatingRobots = new ArrayList<>();
        
    // Initialise the idleRobots queue and add CyclingRobots to it
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

    public void handleIdleRobotTick(){
        // Dispatch robots for non-cycling mailrooms
        if (!idleRobots.isEmpty()) {
            List<Robot> robotsToDispatch = new ArrayList<>(idleRobots);  // Copy idle robots
            for (Robot nextIdleRobot : robotsToDispatch) {
                Building.Direction direction = null;

                // Determine the direction for ColumnRobot
                if (nextIdleRobot instanceof ColumnRobot) {
                    ColumnRobot columnRobot = (ColumnRobot) nextIdleRobot;
                    direction = columnRobot.COLUMN;
                }
                robotDispatch(direction);  // Dispatch the robot
            }
        }
    }

}