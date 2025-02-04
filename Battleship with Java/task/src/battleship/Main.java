package battleship;

import java.util.Arrays;
import java.util.Scanner;

// Hyperskill Battleship with Java study project Stage 1/6 completed - https://hyperskill.org/projects/383/stages/2281/implement
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String[][] field = createField(10, 10);
        printField(field);
        ShipCoordinates coordinates = enterCoordinates(scanner);
        if (coordinates == null) {
            System.out.println("Error!");
        } else {
            System.out.println(placeShip(field, coordinates));
        }
        scanner.close();
        //printField(field);
    }

    /**
     * Method prompting for coordinate input and validates it.
     *
     * @param scanner {@link Scanner} object to be used for the input
     * @return {@link  ShipCoordinates} object with the coordinates from the input
     */
    static ShipCoordinates enterCoordinates(Scanner scanner) {
        System.out.println("Enter the coordinates of the ship:");
        String coordinates = scanner.nextLine();

        String[] parts = coordinates.split(" ");

        String beginning = parts[0];
        String ending = parts[1];
        int startX = Integer.parseInt(beginning.substring(1)) - 1; // Extract full number
        int startY = beginning.charAt(0) - 'A';
        int endX = Integer.parseInt(ending.substring(1)) - 1; // Extract full number
        int endY = ending.charAt(0) - 'A';

        if (startX != endX && startY != endY) {
            return null;
        } else if (startX >= 10 || startY > 10 || endX >= 10 || endY > 10) { //out of bounds
            return null;
        } else if (startX < 0 || endX < 0) {
            return null;
        }

        return new ShipCoordinates(startX, startY, endX, endY);
    }

    /**
     * Places a ship on the field given  the field and the coordinates.
     *
     * @param field           2D String array to place the ship on
     * @param shipCoordinates {@link ShipCoordinates} object with coordinates
     * @return {@link ShipInfo} object with information
     */
    static ShipInfo placeShip(String[][] field, ShipCoordinates shipCoordinates) {
        int startX = shipCoordinates.startX, startY = shipCoordinates.startY;
        int endX = shipCoordinates.endX, endY = shipCoordinates.endY;

        int length = Math.max(Math.abs(startX - endX), Math.abs(startY - endY)) + 1;

        StringBuilder shipParts = new StringBuilder();
        if (startX == endX) { // Horizontal placement
            for (int col = Math.min(startY, endY); col <= Math.max(startY, endY); col++) {
                field[startX][col] = "O";
                shipParts.append((char) (col + 'A')).append(startX + 1).append(" ");
            }
        } else if (startY == endY) {
            for (int row = Math.min(startX, endX); row <= Math.max(startX, endX); row++) {
                field[startY][row] = "O";
                shipParts.append((char) (startY + 'A')).append(row + 1).append(" ");
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
    int startX, startY, endX, endY;

    public ShipCoordinates(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
}
