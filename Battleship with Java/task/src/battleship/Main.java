package battleship;

import java.util.Arrays;

// Hyperskill Battleship with Java study project - https://hyperskill.org/projects/383/stages/2281/implement
public class Main {

    public static void main(String[] args) {
        String[][] field = createField(10, 10);
        printField(field);
        placeShip(field, "A4", "A1");
        printField(field);

    }

    /**
     * Places a ship on the given coordinates. X = horizontal, Y = vertical.
     *
     * @param beginning
     * @param ending
     */
    public static void placeShip(String[][] field, String beginning, String ending) {
        int beginningX = (int) beginning.charAt(1) - '0';
        int beginningY = (int) (ending.charAt(0) - 65); // subtracting the ASCII value of 'A' to get the correct index
        int endingX = ending.charAt(1) - '0';
        int endingY = (int) (ending.charAt(0) - 65);

        //TODO: Need to figure out a way to place the ship when the coordinates are reversed, e.g. A4 - A1(currently only works with A1-A4)
        for (int row = beginningX - 1; row <= endingX - 1; row++) {
            field[beginningY][row] = "O";
        }
    }

    /**
     * Creates a field with the given rows and columns.
     *
     * @param rows    size
     * @param columns size
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
     * @param field to be printed
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
            for (int seat = 0; seat < field[row].length; seat++) {
                System.out.print(" " + field[row][seat]);
            }
            System.out.println();
        }
        System.out.println();
    }
}
