import java.util.LinkedList;

public class FlooringMailRoom extends MailRoom{
    FlooringMailRoom(int numFloors, int numRobots){
        super(numFloors, numRobots);
        
        // Initialize the idleRobots queue and add CyclingRobots to it
        idleRobots = new LinkedList<>();
        for (int i = 0; i < numRobots; i++) {
            // ADD THE COLUMN ROBOTS HERE
        }
        initializeRobots();

        // ADD FLOOR ROBOTS
    }

    public void tick(){

    }


}