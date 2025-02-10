package battleship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/* Hyperskill Battleship with Java study project Stage 6/6 WIP - https://hyperskill.org/projects/383/stages/2286/implement

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
        String[][] visibleFieldOne = createField(10, 10);
        String[][] fogOfWarFieldOne = createField(10, 10);
        String[][] visibleFieldTwo = createField(10, 10);
        String[][] fogOfWarFieldTwo = createField(10, 10);

        playGame(scanner, visibleFieldOne, fogOfWarFieldOne, visibleFieldTwo, fogOfWarFieldTwo);
        scanner.close();
    }

    /**
     * Starts a fully fledged game
     *
     * @param scanner          {@link  Scanner} to be used for input
     * @param visibleFieldOne  2D String array representing the visible field
     * @param fogOfWarFieldOne 2D String array representing the fog of war field
     */
    static void playGame(Scanner scanner, String[][] visibleFieldOne, String[][] fogOfWarFieldOne, String[][] visibleFieldTwo, String[][] fogOfWarFieldTwo) {
        List<Ship> shipList = placeAllShips(scanner, visibleFieldOne, visibleFieldTwo);
        List<Ship> playerOneShipList = shipList.subList(0, 4);
        List<Ship> playerTwoShipList = shipList.subList(4, 9);
        System.out.println("The Game starts!");
        printField(fogOfWarFieldOne);
        System.out.println("Take a shot!");
        // TODO: need to alternate between the two players here
        // TODO: need to alter the winning logic here to be to the sublist (all ships of one players sunk)
        do {
            shootShip(scanner, visibleFieldOne, fogOfWarFieldOne, shipList);
        } while (!shipList.stream().allMatch(Ship::isSunk)); // check if all ships have been sunk, if not, continue with the loop
        System.out.println("You sank the last ship. You won. Congratulations!");
    }

    /**
     * /**
     * Asks for input coordinates, validates them and check if a ship was shot or missed.
     * Updates both fields with the missed coordinate "M" or hit ship with "X".
     * Checks which ship was hit and whether it was sunk
     *
     * @param scanner       {@link  Scanner} to be used for input
     * @param visibleField  2D String array representing the visible field
     * @param fogOfWarField 2D String array representing the fog of war field
     * @param shipList      {@link List} of the placed ships
     */
    static void shootShip(Scanner scanner, String[][] visibleField, String[][] fogOfWarField, List<Ship> shipList) {
        String coordinate;
        do {
            coordinate = scanner.nextLine();
            int column = Integer.parseInt(coordinate.substring(1)) - 1; // Extract full number
            int row = coordinate.charAt(0) - 'A';
            if (column >= 10 || row >= 10 || column < 0 || row < 0) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
            } else {
                if (visibleField[row][column].equals("O") || visibleField[row][column].equals("X")) {
                    visibleField[row][column] = "X";
                    fogOfWarField[row][column] = "X";
                    printField(fogOfWarField); // TODO: need to print both fields (for player one and player two)
                    for (Ship ship : shipList) {
                        ShipCoordinates shipCoordinates = ship.getShipCoordinates();
                        if (row >= Math.min(shipCoordinates.getRowOne(), shipCoordinates.getRowTwo()) && row <= Math.max(shipCoordinates.getRowOne(), shipCoordinates.getRowTwo()) && column >= Math.min(shipCoordinates.getColumnOne(), shipCoordinates.getColumnTwo()) && column <= Math.max(shipCoordinates.getColumnOne(), shipCoordinates.getColumnTwo())) {
                            ship.registerHit();
                            if (ship.isSunk()) {
                                System.out.println("You sank a ship! Specify a new target:");
                            } else {
                                System.out.println("You hit a ship! Try again:");
                            }
                        }
                    }
                    printField(visibleField);
                } else {
                    visibleField[row][column] = "M";
                    fogOfWarField[row][column] = "M";
                    printField(fogOfWarField);
                    System.out.println("You missed. Try again:");
                    printField(visibleField);
                }
                break;
            }
        } while (true);
    }

    /**
     * Asks the user to input coordinates for all ships necessary
     *
     * @param scanner         {@link Scanner} to be used for the input
     * @param visibleFieldOne 2D Array visibleFieldOne to add the ship to
     * @return {@link List} with all the Ship objects.
     */
    static List<Ship> placeAllShips(Scanner scanner, String[][] visibleFieldOne, String[][] visibleFieldTwo) {
        List<Ship> shipsList = new ArrayList<>();
        System.out.println("Player 1, place your ships on the game field");
        printField(visibleFieldOne);
        shipsList.add(inputShip(scanner, visibleFieldOne, ShipType.AIRCRAFT_CARRIER));
        shipsList.add(inputShip(scanner, visibleFieldOne, ShipType.BATTLESHIP));
        shipsList.add(inputShip(scanner, visibleFieldOne, ShipType.SUBMARINE));
        shipsList.add(inputShip(scanner, visibleFieldOne, ShipType.CRUISER));
        shipsList.add(inputShip(scanner, visibleFieldOne, ShipType.DESTROYER));

        System.out.println("Press Enter and pass the move to another player");
        String string = scanner.nextLine();

        shipsList.add(inputShip(scanner, visibleFieldTwo, ShipType.AIRCRAFT_CARRIER));
        shipsList.add(inputShip(scanner, visibleFieldTwo, ShipType.BATTLESHIP));
        shipsList.add(inputShip(scanner, visibleFieldTwo, ShipType.SUBMARINE));
        shipsList.add(inputShip(scanner, visibleFieldTwo, ShipType.CRUISER));
        shipsList.add(inputShip(scanner, visibleFieldTwo, ShipType.DESTROYER));
        return shipsList;
    }

    /**
     * Asks the user to input coordinates for a single ship
     *
     * @param scanner  {@link Scanner} to be used for input
     * @param field    2D Array field to place the ship on
     * @param shipType {@link ShipType} obj of the ship the user needs to place
     */
    static Ship inputShip(Scanner scanner, String[][] field, ShipType shipType) {
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
                Ship ship = placeShip(field, coordinates);
                printField(field);
                return ship;
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

        int[][] directions = {{-1, 0},  // Up
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
     * @return {@link Ship} object with information
     */
    static Ship placeShip(String[][] field, ShipCoordinates shipCoordinates) {
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
        return new Ship(length, shipParts.toString(), shipCoordinates);
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
class Ship {
    private final int length;
    private final String parts;
    private ShipCoordinates shipCoordinates;
    private int hits;

    public Ship(int length, String parts, ShipCoordinates shipCoordinates) {
        this.length = length;
        this.parts = parts;
        this.shipCoordinates = shipCoordinates;
        this.hits = 0;
    }

    public void registerHit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= length;
    }

    public ShipCoordinates getShipCoordinates() {
        return shipCoordinates;
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

