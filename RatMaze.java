import java.util.*;

public class RatMaze {
    private Set<Integer> visitedRooms;
    private Set<int[]> usedDoors;
    private Stack<Object> myImportantPath;
    private int[][] doors;

    public RatMaze(int[] rooms, int[][] doors) {
        this.doors = doors;
        this.visitedRooms = new HashSet<>();
        this.usedDoors = new HashSet<>();
        this.myImportantPath = new Stack<>();
    }

    public String ratAlgorithm(int startRoom, int destinationRoom) {
        int currentRoom = startRoom;
        myImportantPath.push(startRoom);
        iHaveBeenThere(startRoom);

        printStack();
        System.out.println("---");

        while (currentRoom != destinationRoom) {
            int[] nextDoor = whereIsDoor(currentRoom);
            currentRoom = nextDoor[1]; // current room = room next door led to
            
            if (wasIThere(currentRoom)) {
                // if we are in a room we have already been to: backtrack
                if (myImportantPath.size() < 2) {
                    // if unable to, then we exhausted all options and cannot find a path
                    return "Unable to find path";
                } else {
                    System.out.println("No more doors or entered already visited room - Backtrack:");
                    printStack();

                    myImportantPath.pop(); // remove that room
                    myImportantPath.pop(); // remove door that led to that room
                    currentRoom = (int) myImportantPath.peek(); // currentRoom = room we were in before this room

                    printStack();
                    System.out.println("---");
                }
            } else {
                // else, we entered a new room
                // add new room to iHaveBeenThere
                iHaveBeenThere(currentRoom);
                
                System.out.println("New room:");
                printStack();
                System.out.println("---");
            }
        }
        return "Path Found";
    }

    private int[] whereIsDoor(int currentRoom) {
        for (int[] door : doors) {
            // if a door is connected to our room (both directions) and not used, "use" it
            if (door[0] == currentRoom && !isDoorUsed(door)) {
                // add door to iHaveUsedThisDoor (both directions)
                int[] reverse = {door[1], door[0]};
                iHaveUsedThisDoor(door);
                iHaveUsedThisDoor(reverse);

                // add door and room where door led to to path
                myImportantPath.push(door);
                myImportantPath.push(door[1]);

                return door;
            }
            if (door[1] == currentRoom && !isDoorUsed(door)) {
                // add door to iHaveUsedThisDoor (both directions)
                int[] reverse = {door[1], door[0]};
                iHaveUsedThisDoor(door);
                iHaveUsedThisDoor(reverse);

                // add door and room where door led to to path
                myImportantPath.push(reverse);
                myImportantPath.push(door[0]);

                return reverse;
            }
        }

        // if no applicable doors, return the room we are currently at
        int[] current = {currentRoom, currentRoom};
        return current;
    }

    private void iHaveBeenThere(int room) {
        visitedRooms.add(room);
    }

    private void iHaveUsedThisDoor(int[] door) {
        usedDoors.add(door);
    }

    private boolean wasIThere(int room) {
        return visitedRooms.contains(room);
    }

    private boolean isDoorUsed(int[] door) {
        for (int[] usedDoor : usedDoors) {
            if (Arrays.equals(usedDoor, door)) {
                return true;
            }
        }
        return false;
    }
    
    public void printStack() {
        StringBuilder sb = new StringBuilder("[");
        for (Object item : myImportantPath) {
            if (item instanceof int[]) {
                sb.append(Arrays.toString((int[]) item));
            } else if (item instanceof Integer) {
                sb.append(item);
            }
            sb.append(", ");
        }
        if (!myImportantPath.isEmpty()) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("]");
        System.out.println("Stack: " + sb.toString());
    }

    public static void main(String[] args) {
        System.out.println("Base case: room is same as destination room");
        int[] rooms = {1, 2};
        int[][] doors = {{1,2}};
        RatMaze rat = new RatMaze(rooms, doors);
        System.out.println(rat.ratAlgorithm(1, 1));

        System.out.println();

        System.out.println("Example where there exists a path (same example as in lecture notes 13):");
        int[] rooms1 = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        int[][] doors1 = {{1,2}, {2,5}, {5,6}, {4,5}, {3,6}, {4,7}, {7,8}, {8,9}};
        RatMaze rat1 = new RatMaze(rooms1, doors1);
        System.out.println(rat1.ratAlgorithm(1, 9));

        System.out.println();

        System.out.println("Example where there does exists a path, " + 
                            "first path taken is a loop -> backtrack a room -> path to end:");
        int[] rooms2 = {1, 2, 3, 4, 5};
        int[][] doors2 = {{1,2}, {2,3}, {3, 4}, {4, 2}, {4, 5}};
        RatMaze rat2 = new RatMaze(rooms2, doors2);
        System.out.println(rat2.ratAlgorithm(1, 5));

        System.out.println();

        System.out.println("Example where there does exists a path, " + 
                            "first path taken is a loop -> backtrack fully -> path to end:");
        int[] rooms3 = {1, 2, 3, 4, 5};
        int[][] doors3 = {{1,2}, {2,3}, {3, 4}, {4, 2}, {2, 5}};
        RatMaze rat3 = new RatMaze(rooms3, doors3);
        System.out.println(rat3.ratAlgorithm(1, 5));

        System.out.println();

        System.out.println("Example where there does not exists a path:");
        int[] rooms4 = {1, 2, 3, 4, 5};
        int[][] doors4 = {{1,2}, {2,3}, {3, 4}, {4, 2}};
        RatMaze rat4 = new RatMaze(rooms4, doors4);
        System.out.println(rat4.ratAlgorithm(1, 5));
    }
}
