import java.util.ListIterator;

public class FloorRobot extends Robot{
    FloorRobot(){
        super();
    }

    public void transfer(Robot robot) {  // Transfers every item assuming receiving robot has capacity
        ListIterator<MailItem> iter = robot.items.listIterator();
        while(iter.hasNext()) {
            MailItem item = iter.next();
            if (item instanceof Letter) {
                this.add(item); //Hand it over if it is Letter no matter what
            }
            if (item instanceof Parcel) {
                // check the weight limit before hand it over
                // update the avaiolable capacity of the robot
            }
            iter.remove();
        }
    }

    public void tick(){};

}
