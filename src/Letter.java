public class Letter extends MailItem {
    
    Letter(int floor, int room, int arrival) {
        super(floor, room, arrival);  // Calls the MailItem constructor
    }

    // Override the toString() method to include the weight of the letter (set as 0)
    @Override
    public String toString() {
        return super.toString() + ", Weight: " + 0;  // Weight of a letter is 0 by default
    }
}
