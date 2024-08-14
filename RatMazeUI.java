import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.util.*;

public class RatMazeUI extends JFrame {
    private JTextField roomsField;
    private JTextField doorsField;
    private JTextField startRoomField;
    private JTextField destinationRoomField;
    private JTextArea outputArea;

    // setup UI
    public RatMazeUI() {
        // create 5x2 grid for input fields and button
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));

        // create label and corresponding fields
        inputPanel.add(new JLabel("Rooms (comma-separated, integers):"));
        roomsField = new JTextField();
        inputPanel.add(roomsField);
        
        inputPanel.add(new JLabel("Doors (format: 1-2, 3-4, ...):"));
        doorsField = new JTextField();
        inputPanel.add(doorsField);
        
        inputPanel.add(new JLabel("Start Room:"));
        startRoomField = new JTextField();
        inputPanel.add(startRoomField);
        
        inputPanel.add(new JLabel("Destination Room:"));
        destinationRoomField = new JTextField();
        inputPanel.add(destinationRoomField);

        // create button and corresponding event
        JButton findPathButton = new JButton("Find Path");
        findPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findPath();
            }
        });
        inputPanel.add(findPathButton);

        // add 5x2 grid to UI
        add(inputPanel, BorderLayout.NORTH);

        // create and add output area to UI
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.CENTER);
    }

    // handles button click event
    private void findPath() {
        outputArea.setText("");

        // try to find path
        try {
            // get rooms from rooms field
            int[] rooms = Arrays.stream(roomsField.getText().split(",")).mapToInt(Integer::parseInt).toArray();

            // get doors from doors field and convert into correct form [[1-2], [3-4]...]
            String[] doorsInput = doorsField.getText().split(",");
            int[][] doors = new int[doorsInput.length][2];
            for (int i = 0; i < doorsInput.length; i++) {
                String[] door = doorsInput[i].split("-");
                doors[i][0] = Integer.parseInt(door[0].trim());
                doors[i][1] = Integer.parseInt(door[1].trim());
            }

            // get start and destination from corresponding fields
            int startRoom = Integer.parseInt(startRoomField.getText().trim());
            int destinationRoom = Integer.parseInt(destinationRoomField.getText().trim());

            // create new RatMaze class with the inputted rooms and doors
            RatMaze ratMaze = new RatMaze(rooms, doors);
            // run algorithm
            String result = ratMaze.ratAlgorithm(startRoom, destinationRoom);
            System.out.print(result);
            // output result
            outputArea.append(result + "\n");
        }
        // catch errors in input 
        catch (Exception e) {
            outputArea.append("Invalid input. Please check your entries and try again.\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RatMazeUI ui = new RatMazeUI();
            ui.setSize(500, 500);
            ui.setTitle("Rat Maze");
            ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ui.setVisible(true);
        });
    }

    static class RatMaze {
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
            StringBuilder output = new StringBuilder();
            int currentRoom = startRoom;
            myImportantPath.push(startRoom);
            iHaveBeenThere(startRoom);

            printStack(output);
            output.append("---\n");

            while (currentRoom != destinationRoom) {
                int[] nextDoor = whereIsDoor(currentRoom);
                currentRoom = nextDoor[1]; // current room = room next door led to

                if (wasIThere(currentRoom)) {
                    // if we are in a room we have already been to: backtrack
                    if (myImportantPath.size() < 2) {
                        // if unable to, then we exhausted all options and cannot find a path
                        return output.append("Unable to find path").toString();
                    } else {
                        output.append("No more doors or entered already visited room - Backtrack:\n");
                        printStack(output);

                        myImportantPath.pop(); // remove that room
                        myImportantPath.pop(); // remove door that led to that room
                        currentRoom = (int) myImportantPath.peek(); // currentRoom = room we were in before this room

                        printStack(output);
                        output.append("---\n");
                    }
                } else {
                    // else, we entered a new room
                    // add new room to iHaveBeenThere
                    iHaveBeenThere(currentRoom);

                    output.append("New room:\n");
                    printStack(output);
                    output.append("---\n");
                }
            }
            return output.append("Path Found").toString();
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
        
        private void printStack(StringBuilder output) {
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
            output.append("Stack: ").append(sb.toString()).append("\n");
        }
    }
}