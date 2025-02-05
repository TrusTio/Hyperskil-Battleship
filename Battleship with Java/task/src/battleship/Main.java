package battleship;

import java.util.Arrays;
import java.util.Scanner;

// Hyperskill Battleship with Java study project Stage 1/6 completed - https://hyperskill.org/projects/383/stages/2281/implement
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
        scanner.close();
        //printField(field);
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
     * @param scanner {@link Scanner} to be used for input
     * @param field 2D Array field to place the ship on
     * @param shipType {@link ShipType} obj of the ship the user needs to place
     */
    static void inputShip(Scanner scanner, String[][] field, ShipType shipType) {
        do {
            System.out.println("Enter the coordinates of the " + shipType.name + "(" + shipType.length + " cells):");
            ShipCoordinates coordinates = enterCoordinates(scanner, shipType);
            if (coordinates == null) {
                System.out.println("Error!");
            } else {
                placeShip(field, coordinates);
                printField(field);
                break;
            }
        } while (true);
    }

    /**
     * Method prompting for coordinate input and validates it.
     *
     * @param scanner {@link Scanner} object to be used for the input
     * @return {@link  ShipCoordinates} object with the coordinates from the input
     */
    static ShipCoordinates enterCoordinates(Scanner scanner, ShipType shipType) {
        String coordinates = scanner.nextLine();

        String[] parts = coordinates.split(" ");

        String beginning = parts[0];
        String ending = parts[1];
        int columnOne = Integer.parseInt(beginning.substring(1)) - 1; // Extract full number
        int rowOne = beginning.charAt(0) - 'A';
        int columnTwo = Integer.parseInt(ending.substring(1)) - 1; // Extract full number
        int rowTwo = ending.charAt(0) - 'A';

        int length = Math.max(Math.abs(columnOne - columnTwo), Math.abs(rowOne - rowTwo)) + 1;

        //TODO: need to add proper error responses
        //TODO: need to check whether it's adjacent to other ships as it's part of the rules
        //TODO: need to check if the spot is already not taken by another ship
        if (length != shipType.length) {
            return null;
        } else if (columnOne != columnTwo && rowOne != rowTwo) {
            return null;
        } else if (columnOne >= 10 || rowOne > 10 || columnTwo >= 10 || rowTwo > 10) { //out of bounds
            return null;
        } else if (columnOne < 0 || columnTwo < 0) {
            return null;
        }

        return new ShipCoordinates(columnOne, rowOne, columnTwo, rowTwo);
    }

    /**
     * Places a ship on the field given  the field and the coordinates.
     *
     * @param field           2D String array to place the ship on
     * @param shipCoordinates {@link ShipCoordinates} object with coordinates
     * @return {@link ShipInfo} object with information
     */
    static ShipInfo placeShip(String[][] field, ShipCoordinates shipCoordinates) {
        int columnOne = shipCoordinates.columnOne, rowOne = shipCoordinates.rowOne;
        int columnTwo = shipCoordinates.columnTwo, rowTwo = shipCoordinates.rowTwo;

        int length = Math.max(Math.abs(columnOne - columnTwo), Math.abs(rowOne - rowTwo)) + 1;

        StringBuilder shipParts = new StringBuilder();
        if (columnOne == columnTwo) { // Horizontal placement
            for (int row = Math.min(rowOne, rowTwo); row <= Math.max(rowOne, rowTwo); row++) {
                field[row][columnOne] = "O";
                shipParts.append((char) (rowOne + 'A')).append(row + 1).append(" ");
            }
        } else if (rowOne == rowTwo) { // Vertical placement
            for (int col = Math.min(columnOne, columnTwo); col <= Math.max(columnOne, columnTwo); col++) {
                field[rowOne][col] = "O";
                shipParts.append((char) (col + 'A')).append(columnOne + 1).append(" ");
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
    int columnOne, rowOne, columnTwo, rowTwo;

    //        return new ShipCoordinates(columnOne, rowOne, columnTwo, rowTwo);
    public ShipCoordinates(int columnOne, int rowOne, int columnTwo, int rowTwo) {
        this.columnOne = columnOne;
        this.rowOne = rowOne;
        this.columnTwo = columnTwo;
        this.rowTwo = rowTwo;
    }
}

