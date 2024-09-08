public class Letter extends MailItem {

    Letter(int floor, int room, int arrival) {
        super(floor, room, arrival);
    }

    @Override
    public String toString() {
        return super.toString() + ", Weight: " + 0 + "kg";
    }
    
}
