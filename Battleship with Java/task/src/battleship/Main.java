package battleship;

import java.util.Arrays;
import java.util.Scanner;

/* Hyperskill Battleship with Java study project Stage 3/6 completed - https://hyperskill.org/projects/383/stages/2281/implement

Take a shot at a prepared game field. You need to indicate the coordinates of the target,
and the program should then display a message about a hit or a miss.
If the shell misses the target and falls in the water, this cell should be marked with an M, and a
successful strike is marked by an X. After this shot, the game should be stopped.

If the player managed to hit a ship, the game should display a message You hit a ship!; otherwise, the message is You missed!
 */
public class Main {


    enum ShipType {
        AIRCRAFT_CARRIER(5, "Aircraft Carrier"), BATTLESHIP(4, "Battleship"), SUBMARINE(3, "Submarine"), CRUISER(3, "Cruiser"), DESTROYER(2, "Destroyer");
        private final int length;
        private final String name;

        ShipType(int length, String name) {
            this.length = length;
            this.name = name;
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[][] field = createField(10, 10);
        printField(field);

        placeAllShips(scanner, field);
        System.out.println("The Game starts!");
        printField(field);
        shootShip(scanner, field);
        printField(field);
        scanner.close();

    }

    /**
     * Asks for input coordinates, validates them and check if a ship was shot or missed.
     * Updates the field with the missed coordinate "M" or hit ship with "X"
     *
     * @param scanner {@link  Scanner} to be used for input
     * @param field   2D String array of the field
     */
    static void shootShip(Scanner scanner, String[][] field) {
        System.out.println("Take a shot!");
        String coordinate;
        do {
            coordinate = scanner.nextLine();
            int column = Integer.parseInt(coordinate.substring(1)) - 1; // Extract full number
            int row = coordinate.charAt(0) - 'A';
            if (column >= 10 || row >= 10 || column < 0 || row < 0) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
            } else {
                if (field[row][column].equals("O")) {
                    field[row][column] = "X";
                    System.out.println("You hit a ship!");
                } else {
                    field[row][column] = "M";
                    System.out.println("You missed!");
                }
                break;
            }
        } while (true);
    }

    /**
     * Asks the user to input coordinates for all ships necessary
     *
     * @param scanner {@link Scanner} to be used for the input
     * @param field   2D Array field to add the ship to
     */
    static void placeAllShips(Scanner scanner, String[][] field) {
        inputShip(scanner, field, ShipType.AIRCRAFT_CARRIER);
        inputShip(scanner, field, ShipType.BATTLESHIP);
        inputShip(scanner, field, ShipType.SUBMARINE);
        inputShip(scanner, field, ShipType.CRUISER);
        inputShip(scanner, field, ShipType.DESTROYER);
    }

    /**
     * Asks the user to input coordinates for a single ship
     *
     * @param scanner  {@link Scanner} to be used for input
     * @param field    2D Array field to place the ship on
     * @param shipType {@link ShipType} obj of the ship the user needs to place
     */
    static void inputShip(Scanner scanner, String[][] field, ShipType shipType) {
        System.out.println("Enter the coordinates of the " + shipType.name + "(" + shipType.length + " cells):");
        do {
            ShipCoordinates coordinates = null;
            try {
                coordinates = enterCoordinates(scanner, field, shipType);
            } catch (WrongLengthException e) {
                System.out.println("Error! Wrong length of the " + shipType.name + "! Try again:");
            } catch (WrongLocationException e) {
                System.out.println("Error! Wrong ship location! Try again:");
            } catch (AdjacentLocationException e) {
                System.out.println("Error! You placed it too close to another one. Try again:");
            } catch (Exception e) {
                System.out.println("Error!");
            }
            if (coordinates != null) {
                placeShip(field, coordinates);
                printField(field);
                break;
            }
        } while (true);
    }

    /**
     * Method prompting for coordinate input and validates it.
     *
     * @param scanner  {@link Scanner} object to be used for the input
     * @param field    2D array field to check for adjacent/existing ships.
     * @param shipType {@link ShipType} object to be used for ship type information
     * @return {@link  ShipCoordinates} object with the coordinates from the input
     * @throws Exception when the coordinates are incorrect
     */
    static ShipCoordinates enterCoordinates(Scanner scanner, String[][] field, ShipType shipType) throws Exception {
        String coordinates = scanner.nextLine();

        String[] parts = coordinates.split(" ");

        String beginning = parts[0];
        String ending = parts[1];
        int columnOne = Integer.parseInt(beginning.substring(1)) - 1; // Extract full number
        int rowOne = beginning.charAt(0) - 'A';
        int columnTwo = Integer.parseInt(ending.substring(1)) - 1; // Extract full number
        int rowTwo = ending.charAt(0) - 'A';

        int length = Math.max(Math.abs(columnOne - columnTwo), Math.abs(rowOne - rowTwo)) + 1;

        if (length != shipType.length) {
            throw new WrongLengthException("Error! Wrong length");
        } else if (columnOne != columnTwo && rowOne != rowTwo) {
            throw new WrongLengthException("Error! Wrong ship location - needs to be in a straight line.");
        } else if (columnOne >= 10 || rowOne >= 10 || columnTwo >= 10 || rowTwo >= 10) { //out of bounds
            throw new WrongLengthException("Error! Wrong ship location - out of bounds.");
        } else if (columnOne < 0 || columnTwo < 0) {
            throw new WrongLengthException("Error! Wrong ship location - out of bounds.");
        }

        // Check for Adjacent ships
        if (columnOne == columnTwo) { // Vertical placement check
            for (int row = Math.min(rowOne, rowTwo); row <= Math.max(rowOne, rowTwo); row++) {
                checkNeighbors(field, row, columnOne);
            }
        } else { // Horizontal placement check
            for (int col = Math.min(columnOne, columnTwo); col <= Math.max(columnOne, columnTwo); col++) {
                checkNeighbors(field, rowOne, col);
            }
        }
        return new ShipCoordinates(columnOne, rowOne, columnTwo, rowTwo);
    }

    /**
     * Checks the adjacent positions in the field
     *
     * @param field 2d Array field to use
     * @param row   current row position to check adjacent of
     * @param col   current col position to check adjacent of
     * @throws AdjacentLocationException thrown if a ship is found at adjacent location
     */
    static void checkNeighbors(String[][] field, int row, int col) throws AdjacentLocationException {
        int rows = field.length;
        int cols = field[0].length;

        int[][] directions = {
                {-1, 0},  // Up
                {1, 0},   // Down
                {0, -1},  // Left
                {0, 1}    // Right
        };

        for (int[] dir : directions) {
            int newRow = row + dir[0]; // Adjust row
            int newCol = col + dir[1]; // Adjust column

            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                if (field[newRow][newCol].equals("O")) {
                    throw new AdjacentLocationException("Ship at adjacent location present");
                }
            }
        }
    }

    /**
     * Places a ship on the field given  the field and the coordinates.
     *
     * @param field           2D String array to place the ship on
     * @param shipCoordinates {@link ShipCoordinates} object with coordinates
     * @return {@link ShipInfo} object with information
     */
    static ShipInfo placeShip(String[][] field, ShipCoordinates shipCoordinates) {
        int columnOne = shipCoordinates.getColumnOne(), rowOne = shipCoordinates.getRowOne();
        int columnTwo = shipCoordinates.getColumnTwo(), rowTwo = shipCoordinates.getRowTwo();

        int length = Math.max(Math.abs(columnOne - columnTwo), Math.abs(rowOne - rowTwo)) + 1;

        StringBuilder shipParts = new StringBuilder();
        if (columnOne == columnTwo) { // Vertical placement
            for (int row = Math.min(rowOne, rowTwo); row <= Math.max(rowOne, rowTwo); row++) {
                field[row][columnOne] = "O";
                shipParts.append((char) (row + 'A')).append(columnOne + 1).append(" ");
            }
        } else if (rowOne == rowTwo) { // Horizontal placement
            for (int col = Math.min(columnOne, columnTwo); col <= Math.max(columnOne, columnTwo); col++) {
                field[rowOne][col] = "O";
                shipParts.append((char) (rowOne + 'A')).append(col + 1).append(" ");
            }
        }
        return new ShipInfo(length, shipParts.toString());
    }


    /**
     * Creates a field with the given rows and columns.
     *
     * @param rows    int size
     * @param columns int size
     * @return 2D String array
     */
    public static String[][] createField(int rows, int columns) {
        String[][] field = new String[rows][columns];
        for (String[] strings : field) {
            Arrays.fill(strings, "~");
        }
        return field;
    }

    /**
     * Prints the given field with the appropriate numeration/coordinates
     *
     * @param field 2D String Array field to be printed
     */
    public static void printField(String[][] field) {
        System.out.println();
        System.out.print(" ");
        for (int column = 1; column <= field[0].length; column++) {
            System.out.print(" " + column);
        }
        System.out.println();
        for (int row = 0; row < field.length; row++) {
            System.out.print((char) (row + 65));
            for (int column = 0; column < field[row].length; column++) {
                System.out.print(" " + field[row][column]);
            }
            System.out.println();
        }
        System.out.println();
    }
}

/**
 * Helper class that contains ship information
 */
class ShipInfo {
    private final int length;
    private final String parts;

    public ShipInfo(int length, String parts) {
        this.length = length;
        this.parts = parts;
    }

    public int getLength() {
        return length;
    }

    public String getCoordinates() {
        return parts;
    }

    @Override
    public String toString() {
        return "Ship Length: " + length + "\nParts: " + parts;
    }
}

/**
 * Helper class to contain coordinates.
 */
class ShipCoordinates {
    private int columnOne, rowOne, columnTwo, rowTwo;

    public ShipCoordinates(int columnOne, int rowOne, int columnTwo, int rowTwo) {
        this.columnOne = columnOne;
        this.rowOne = rowOne;
        this.columnTwo = columnTwo;
        this.rowTwo = rowTwo;
    }

    public int getColumnOne() {
        return columnOne;
    }

    public int getRowOne() {
        return rowOne;
    }

    public int getColumnTwo() {
        return columnTwo;
    }

    public int getRowTwo() {
        return rowTwo;
    }
}

