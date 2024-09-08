import java.util.*;
import java.util.concurrent.TimeUnit;

public class Simulation {
    // Map to keep track of mail items that are waiting to arrive, keyed by their arrival time
    private static final Map<Integer, List<MailItem>> waitingToArrive = new HashMap<>();
    // Current simulation time
    private static int time = 0;
    // Last possible time for mail arrivals
    public final int endArrival;
    // MailRoom instance to handle robot operations
    final public MailRoom mailroom;
    // Timeout period between each step of the simulation
    private static int timeout;

    // Counters to track delivered items and total delivery time
    private static int deliveredCount = 0;
    private static int deliveredTotalTime = 0;

    // Method to deliver a mail item and update statistics
    public static void deliver(MailItem mailItem) {
        System.out.println("Delivered: " + mailItem);
        deliveredCount++;
        // Update the total time taken for delivery
        deliveredTotalTime += now() - mailItem.myArrival();
    }

    // Add a mail item to the list of arrivals at a specific time
    void addToArrivals(int arrivalTime, MailItem item) {
        System.out.println(item.toString());
        if (waitingToArrive.containsKey(arrivalTime)) {
            waitingToArrive.get(arrivalTime).add(item);
        } else {
            LinkedList<MailItem> items = new LinkedList<>();
            items.add(item);
            waitingToArrive.put(arrivalTime, items);
        }
    }

    // Constructor for the Simulation class, initializes the mailroom, building, and generates mail items
    Simulation(Properties properties) {
        int seed = Integer.parseInt(properties.getProperty("seed"));
        Random random = new Random(seed);  // Seeded random generator for reproducibility
        this.endArrival = Integer.parseInt(properties.getProperty("mail.endarrival"));
        int numLetters = Integer.parseInt(properties.getProperty("mail.letters"));
        int numParcels = Integer.parseInt(properties.getProperty("mail.parcels"));
        int maxWeight = Integer.parseInt(properties.getProperty("mail.parcelmaxweight"));
        int numFloors = Integer.parseInt(properties.getProperty("building.floors"));
        int numRooms = Integer.parseInt(properties.getProperty("building.roomsperfloor"));
        int numRobots = Integer.parseInt(properties.getProperty("robot.number"));
        int robotCapacity = Integer.parseInt(properties.getProperty("robot.capacity"));
        timeout = Integer.parseInt(properties.getProperty("timeout"));
        MailRoom.Mode mode = MailRoom.Mode.valueOf(properties.getProperty("mode"));

        // Initialise the building with the number of floors and rooms
        Building.initialise(numFloors, numRooms);

        // Initialise the mailroom based on the selected mode
        if (mode == MailRoom.Mode.CYCLING) {
            mailroom = new CyclingMailRoom(numFloors, numRobots, robotCapacity);
        } else if (mode == MailRoom.Mode.FLOORING) {
            mailroom = new FlooringMailRoom(numFloors, robotCapacity);
        } else {
            throw new IllegalArgumentException("Invalid mode");
        }

        // Generate mail items (letters and parcels)
        generateMailItems(numLetters, numParcels, numFloors, numRooms, endArrival, maxWeight, random);

        // Set the mailroom for the robot
        Robot.setMailRoom(mailroom);
    }

    // Generate mail items (letters and parcels) with random properties
    private void generateMailItems(int numLetters, int numParcels, int numFloors, int numRooms, int endArrival, int maxWeight, Random random) {
        // Generate random letters
        for (int i = 0; i < numLetters; i++) {
            int arrivalTime = random.nextInt(endArrival) + 1;
            int floor = random.nextInt(numFloors) + 1;
            int room = random.nextInt(numRooms) + 1;
            addToArrivals(arrivalTime, new Letter(floor, room, arrivalTime));
        }

        // Generate random parcels with random weight
        for (int i = 0; i < numParcels; i++) {
            int arrivalTime = random.nextInt(endArrival) + 1;
            int floor = random.nextInt(numFloors) + 1;
            int room = random.nextInt(numRooms) + 1;
            int weight = random.nextInt(maxWeight) + 1;
            addToArrivals(arrivalTime, new Parcel(floor, room, arrivalTime, weight));
        }
    }

    // Get the current simulation time
    public static int now() {
        return time;
    }

    // Perform a step in the simulation, advancing time and processing mail arrivals and deliveries
    void step() {
        // Process any mail that is due to arrive at the current time
        if (waitingToArrive.containsKey(time))
            mailroom.arrive(waitingToArrive.get(time));

        // Advance internal mailroom processing
        mailroom.tick();
    }

    // Run the entire simulation until all mail items have been delivered
    void run() {
        // Keep running the simulation until all mail has arrived and been delivered
        while (time++ <= endArrival || mailroom.someItems()) {
            step();
            try {
                TimeUnit.MILLISECONDS.sleep(timeout);  // Pause between steps to simulate real time
            } catch (InterruptedException e) {
                // Handle sleep interruptions (optional logging)
            }
        }
        // Print final statistics after all mail items are delivered
        System.out.printf("Finished: Items delivered = %d; Average time for delivery = %.2f%n",
                deliveredCount, (float) deliveredTotalTime / deliveredCount);
    }
}
