import java.util.Random;

/**
 * BarrierCoverageSimulator simulates the 2D planar region
 * with the barrier lines that need to be covered as well as
 * camera placement. Camera placement is determined by a
 * random baseline algorithm, greedy algorithm, or heuristic
 * algorithm.
 *
 * @author Caleb Tuttle
 * @version 6-25-2019
 */
public class BarrierCoverageSimulator {

    private char[][] planarRegion;
    Random gen;

    public static void main(String[] args)
    {
        BarrierCoverageSimulator sim = new BarrierCoverageSimulator();
    }


    /**
     * Constructor
     */
    public BarrierCoverageSimulator()
    {
        planarRegion = getPlanarRegion();
        placeLines();
        getAndPlaceGreedy(planarRegion);
        print2DCharArray(planarRegion);
//        printCoordinates();
    }

    public void printCoordinates()
    {
        int[][] coordinates = getRandomCoordinates();
        coordinates = convertCoordinates(coordinates);

        for(int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];
            System.out.println("(" + x + ", " + y + ")");
        }
    }


    /**
     * Get 10 random (x, y) coordinates within the 7 x 15 planar
     * region. The first element in the 2nd dimension of the array
     * is always an x coordinate, while the second element is the y
     * coordinate.
     * @return a 2D array of 10 (x, y) coordinates.
     */
    public int[][] getRandomCoordinates()
    {
        int[][] coordinates = new int[10][2];
        gen = new Random();

        for (int i = 0; i < 10; i++) {
            int x = gen.nextInt(7);
            int y = gen.nextInt(15);

            coordinates[i][0] = x;
            coordinates[i][1] = y;
        }

        return coordinates;
    }

    /**
     * Place camera sensors (denoted by the char 'S') in the
     * planar region according to the random algorithm by
     * modifying planarRegion to have a 'S' wherever a camera
     * is placed.
     */
    public void placeRandomly()
    {
        int[][] coordinates = getRandomCoordinates();

        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];

            planarRegion[y][x] = 'S';
        }
    }


    /**
     * Get coordinates from greedy algorithm, place the camera sensors on
     * the plane, and update the plane to reflect which points are covered.
     * @return a 2D array of the coordinates at which the camera sensors
     * should be place.
     */
    public int[][] getAndPlaceGreedy(char[][] planarRegion)
    {
        int[][] coordinates = new int[10][2];
        int numBarrierPoints = getNumBarrierPoints(planarRegion, 5, 5);

        int i = 0; // coordinates index
        while (numBarrierPoints > 0) {
            for (int row = 0; row < planarRegion.length; row++) {
                for (int col = 0; col < planarRegion[row].length; col++) {
                    if (planarRegion[row][col] == 'B') {
                        if (col > 0) {
                            planarRegion[row][col - 1] = 'S';

                            coordinates[i][0] = col; // x
                            coordinates[i][1] = row; // y

                            updateCovered(planarRegion, row, col);
                        }
                        else {
                            planarRegion[row][col] = 'S';

                            coordinates[i][0] = col; // x
                            coordinates[i][1] = row; // y

                            updateCovered(planarRegion, row, col);
                        }
                    }
                    numBarrierPoints--;
                }
            }
        }

        return coordinates;
    }

    /**
     * Update the planar region to indicate which points on the plane
     * are covered. 'C' designates a covered point on a barrier line.
     * 'i' designates a covered point not on a barrier line, an
     * irrelevant point. This method updates the plane on the
     * assumptions that the camera sensor has a 45 degree viewing
     * angle and that it can view the entire width of the plane.
     * @param planarRegion
     * @param row The row on which the sensor is placed.
     * @param col The column to the right of where the sensor is placed.
     */
    public void updateCovered(char[][] planarRegion, int row, int col)
    {
        if (row < planarRegion.length && row > 0) {
            if (col < planarRegion[row].length && col > 0) {
                for (int x = col; x < planarRegion[row].length; x++) {
                    if (planarRegion[row][x] == 'B') {
                        planarRegion[row][x] = 'C';
                    }
                    else if (planarRegion[row][x] == '.') {
                        planarRegion[row][x] = 'i';
                    }
                }
                updateCovered(planarRegion, row + 1, col + 2);
                updateCovered(planarRegion, row - 1, col + 2);
            }
        }
    }


    /**
     * This method places camera sensors according to the specified coordinates.
     * @param coordinates The coordinates at which to place the
     *                    cameras. Coordinates must be bound by
     *                    the height and width of the planar region.
     */
    public void placeCoordinates(int[][] coordinates)
    {
        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];

            planarRegion[y][x] = 'S';
        }
    }


    /**
     * Get the planar region to be covered. Dimensions of region:
     * 15 high by 10 wide.
     * @return a 2D array of chars representing the coverable planar region.
     */
    public char[][] getPlanarRegion()
    {
        char[][] planarRegion = new char[15][7];

        for (int y = 0; y < planarRegion.length; y++) {
            for (int x = 0; x < planarRegion[y].length; x++) {
                planarRegion[y][x] = '.';
            }
        }

        return planarRegion;
    }

    /**
     * Place the barrier lines in the planar region.
     * TODO: Determine where the lines should actually be
     */
    public void placeLines()
    {
        for (int y = 0; y < planarRegion.length; y++) {
            for (int x = 0; x < planarRegion[y].length; x++) {
                // Place the top line
                if (x > 1 && x < 7 && y == 2) {
                    planarRegion[y][x] = 'B';
                }

                // Place the bottom line
                if (x > 1 && x < 7 && y == 6) {
                    planarRegion[y][x] = 'B';
                }

                // Place the left line
                if (x == 2 && y > 1 && y < 6) {
                    planarRegion[y][x] = 'B';
                }

                // Place the right line
                if (x == 6 && y > 1 && y < 6) {
                    planarRegion[y][x] = 'B';
                }
            }
        }
    }


    /**
     * Get the number of barrier points on the plane.
     * @param planarRegion The region on which the barrier lines lay.
     * @param height The height of the rectangle composed of barrier lines.
     * @param width The width of the rectangle composed of barrier lines.
     * @return the number of barrier points on the plane.
     */
    public int getNumBarrierPoints(char[][] planarRegion, int height, int width)
    {
        int numBarrierPoints = (height * 2) + (width * 2) - 4;
        return numBarrierPoints;
    }

    /**
     * Print the given 2D array.
     * @param array the 2D array to be printed.
     */
    public void print2DCharArray(char[][] array)
    {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[y].length; x++) {
                System.out.print(array[y][x] + " ");
            }
            System.out.println();
        }
    }

    /**
     * The origin of the coordinates generated by the algorithms is at
     * the top left of the graph. This converter takes those coordinates
     * and returns the corresponding coordinates for the graph whose
     * origin is at the bottom left of the graph. For example, given
     * (5, 7) the converter will return (5, 8).
     * @param array
     * @return
     */
    public int[][] convertCoordinates(int[][] array)
    {
        int[][] newArray = array;

        for (int i = 0; i < array.length; i++) {
            newArray[i][1] = Math.abs(array[i][1] - 14);
        }

        return newArray;
    }
}
