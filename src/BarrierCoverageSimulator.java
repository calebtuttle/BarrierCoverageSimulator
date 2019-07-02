import java.util.Random;

/**
 * BarrierCoverageSimulator simulates the 2D planar region
 * with the barrier lines that need to be covered as well as
 * camera placement. Camera placement is determined by a
 * random baseline algorithm, greedy algorithm, or heuristic
 * algorithm.
 *
 * @author Caleb Tuttle
 * @version 6-30-2019
 */
public class BarrierCoverageSimulator {

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
        char[][] planarRegion = getPlanarRegion();
        placeLines(planarRegion);

        int[][] coordinates = getAndPlaceGreedy(planarRegion);

        for (int i = 0; i < coordinates.length; i++) {
            char[][] p = getPlanarRegion();
            placeLines(p);

            int x = coordinates[i][0];
            int y = coordinates[i][1];
            int direction = coordinates[i][2];

            System.out.println("\n\rLocation: (" + x + ", " +
                    y + ")\n\rDirection: " + direction);

            updateCovered(p, y, x, direction, 5, 1);
            print2DCharArray(p);
        }

        createTree();
    }


    /**
     * Print random (x, y) coordinates.
     */
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
     * Place camera sensors (denoted by the char 'S') in a
     * the given planar region according to the random algorithm.
     * @return The given planar region updated with randomly
     *          placed camera sensors.
     */
    public char[][] placeRandomly(char[][] planarRegion)
    {
        int[][] coordinates = getRandomCoordinates();

        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];

            planarRegion[y][x] = 'S';
        }

        return planarRegion;
    }


    /**
     * Get coordinates from greedy algorithm, place the camera sensors on
     * the plane, and update the plane to reflect which points are covered.
     * @param planarRegion The planar region on which the sensors should be
     *                     placed according to the greedy algorithm. It should
     *                     have lines in it before it is passed to this method.
     * @return a 2D array of the coordinates at which the camera sensors
     * should be place.
     */
    public int[][] getAndPlaceGreedy(char[][] planarRegion)
    {
        int[][] coordinates;

        // An array with each location, direction, and the number of uncovered barrier
        // points there are at that location and direction.
        // Indices: 0: x. 1: y. 2: direction. 3: number of uncovered barrier points.
        int[][] locations = new int[planarRegion.length * planarRegion[0].length * 4][4];

        // The planar region used to determine which point is best for sensor
        char[][] planarRegion1 = planarRegion;

        // The final planar region, the one that is modified at the
        // end of each iteration of the while loop.
        char[][] planarRegion2 = new char[planarRegion1.length][planarRegion1[0].length];
        planarRegion2 = copy(planarRegion2, planarRegion1);

        System.out.println("Initial:"); // DELETE THIS LINE
        print2DCharArray(planarRegion1); // DELETE THIS LINE




        // Indices: 0: number of uncovered points. 1: row. 2: col. 3: direction.
        int[] min = new int[4];
        min[0] = getNumUncoveredPoints(planarRegion1);

        // Goal: decrease number of uncovered barrier points to 0.
//        int i = 1;
//        while (getNumUncoveredPoints(planarRegion2) > 0) {
        int index = 0;
            for (int row = 0; row < planarRegion1.length; row++) {
                for (int col = 0; col < planarRegion1[row].length; col++) {
                    // Check coverage at this point when sensor faces right
                    updateCovered(planarRegion1, row, col, 1, 5, 1);

                    // Enter this location, direction, and the number of barrier points covered.
                    locations[index][0] = col; // x
                    locations[index][1] = row; // y
                    locations[index][2] = 1; // direction
                    locations[index][3] = getNumUncoveredPoints(planarRegion1);
                    index++;

                    if (getNumUncoveredPoints(planarRegion1) < min[0]) {
                        min[0] = getNumUncoveredPoints(planarRegion1);
                        min[1] = row;
                        min[2] = col;
                        min[3] = 1;
                    }

                    // reset planar region to what it was at the beginning of
                    // this while-loop iteration
                    planarRegion1 = copy(planarRegion1, planarRegion2);

                    // Check coverage at this point when sensor faces up
                    updateCovered(planarRegion1, row, col, 2, 5, 1);

                    locations[index][0] = col; // x
                    locations[index][1] = row; // y
                    locations[index][2] = 2; // direction
                    locations[index][3] = getNumUncoveredPoints(planarRegion1);
                    index++;

                    if (getNumUncoveredPoints(planarRegion1) < min[0]) {
                        min[0] = getNumUncoveredPoints(planarRegion1);
                        min[1] = row;
                        min[2] = col;
                        min[3] = 2;
                    }

                    // reset planar region to what it was at the beginning of
                    // this while-loop iteration
                    planarRegion1 = copy(planarRegion1, planarRegion2);

                    // Check coverage at this point when sensor faces left
                    updateCovered(planarRegion1, row, col, 3, 5, 1);

                    locations[index][0] = col; // x
                    locations[index][1] = row; // y
                    locations[index][2] = 3; // direction
                    locations[index][3] = getNumUncoveredPoints(planarRegion1);
                    index++;

                    if (getNumUncoveredPoints(planarRegion1) < min[0]) {
                        min[0] = getNumUncoveredPoints(planarRegion1);
                        min[1] = row;
                        min[2] = col;
                        min[3] = 3;
                    }


                    // reset planar region to what it was at the beginning of
                    // this while-loop iteration
                    planarRegion1 = copy(planarRegion1, planarRegion2);


                    // Check coverage at this point when sensor faces down
                    updateCovered(planarRegion1, row, col, 4, 5, 1);

                    locations[index][0] = col; // x
                    locations[index][1] = row; // y
                    locations[index][2] = 4; // direction
                    locations[index][3] = getNumUncoveredPoints(planarRegion1);
                    index++;

                    if (getNumUncoveredPoints(planarRegion1) < min[0]) {
                        min[0] = getNumUncoveredPoints(planarRegion1);
                        min[1] = row;
                        min[2] = col;
                        min[3] = 4;
                    }

                    // reset
                    planarRegion1 = copy(planarRegion1, planarRegion2);
                }
            }

            // Remove the following chunk of code. Put it somewhere else
//            updateCovered(planarRegion2, min[1], min[2], min[3], 5, 1);
//            System.out.println("Iteration: " + // i +
//                    "\n\rDirection: " + min[3]);
//            System.out.println("Location: (" + min[2] + ", " + min[1] + ")");
//            print2DCharArray(planarRegion2);

//            i++;
//        }

        coordinates = findBestLocations(locations);

        return coordinates;
    }

    /**
     * Update the planar region to indicate which points on the plane
     * are covered after a camera sensor has been placed at the specified
     * row and column. 'C' designates a covered point on a barrier line.
     * 'i' designates a covered point that is not on a barrier line, an
     * irrelevant point. This method updates the plane on the
     * assumptions that the camera sensor has a 45 degree viewing
     * angle and that it can view the entire width of the plane.
     * The sensor cannot be placed on a barrier line.
     * @param planarRegion
     * @param row The row at which the sensor is placed.
     * @param col The column at which the sensor is placed.
     * @param direction The direction the camera is facing (1 for right, 2
     *                  for up, 3 for left, 4 for down).
     * @param maxSenseRange The camera sensor's maximum sensing range in feet.
     * @param callNum The number of times updateCovered has been called. Used
     *                to determine how many times it has called itself.
     */
    public void updateCovered(char[][] planarRegion, int row, int col, int direction,
                              int maxSenseRange, int callNum)
    {
        // Base case
        if (row >= planarRegion.length || row < 0) {
            return;
        }
        if (col >= planarRegion[row].length || col < 0) {
            return;
        }

        if (callNum == 1) {
            // Do not let the sensor be placed on a barrier line
            if (planarRegion[row][col] == 'B' || planarRegion[row][col] == 'C') {
                return;
            }

            // Place sensor here
            planarRegion[row][col] = 'S';
        }


        // Camera facing right
        if (direction == 1) {
            // sensingLimit = the first point the sensor cannot see
            int sensingLimit = (planarRegion[row].length < (col + maxSenseRange)) ?
                    planarRegion[row].length : (col + maxSenseRange);

            for (int x = col; x < sensingLimit; x++) {
                if (planarRegion[row][x] == 'B') {
                    planarRegion[row][x] = 'C';
                }
                else if (planarRegion[row][x] == '.') {
                    planarRegion[row][x] = 'i';
                }
            }
            updateCovered(planarRegion, row + 1, col + 2, 1,
                    maxSenseRange - 2, callNum + 1);
            updateCovered(planarRegion, row - 1, col + 2, 1,
                    maxSenseRange - 2, callNum + 1);
        }

        // Camera facing up
        if (direction == 2) {
            int sensingLimit = (0 > (row - maxSenseRange)) ?
                    0 : (row - maxSenseRange);

            for (int y = row; y >= sensingLimit; y--) {
                if (planarRegion[y][col] == 'B') {
                    planarRegion[y][col] = 'C';
                }
                else if (planarRegion[y][col] == '.') {
                    planarRegion[y][col] = 'i';
                }
            }
            updateCovered(planarRegion, row - 2, col + 1, 2,maxSenseRange - 2,
                    callNum + 1);
            updateCovered(planarRegion, row - 2, col - 1, 2,
                    maxSenseRange - 2, callNum + 1);
        }

        // Camera facing left
        if (direction == 3) {
            int sensingLimit = (0 > (col - maxSenseRange)) ?
                    0 : (col - maxSenseRange);

            for (int x = col; x >= sensingLimit; x--) {
                if (planarRegion[row][x] == 'B') {
                    planarRegion[row][x] = 'C';
                }
                else if (planarRegion[row][x] == '.') {
                    planarRegion[row][x] = 'i';
                }
            }
            updateCovered(planarRegion, row + 1, col - 2, 3,
                    maxSenseRange - 2, callNum + 1);
            updateCovered(planarRegion, row - 1, col - 2, 3,
                    maxSenseRange - 2, callNum + 1);
        }

        // Camera facing down
        if (direction == 4) {
            int sensingLimit = (planarRegion.length < (row + maxSenseRange)) ?
                    planarRegion.length : (row + maxSenseRange);

            for (int y = row; y < sensingLimit; y++) {
                if (planarRegion[y][col] == 'B') {
                    planarRegion[y][col] = 'C';
                }
                else if (planarRegion[y][col] == '.') {
                    planarRegion[y][col] = 'i';
                }
            }
            updateCovered(planarRegion, row + 2, col + 1, 4,
                    maxSenseRange - 2, callNum + 1);
            updateCovered(planarRegion, row + 2, col - 1, 4,
                    maxSenseRange - 2, callNum + 1);
        }
    }

    /**
     * Used by getAndPlaceGreedy.
     * @param locations The second dimension array must have 4 elements. (That is,
     *                  int[][] locations = new int[_][4].)
     * @return
     */
    private int[][] findBestLocations(int[][] locations)
    {
        int[][] bestLocations = new int[10][3];

        // Sort locations according to the number of uncovered barrier points.
        // The location with the least number of uncovered barrier points will
        // be first in the array.
        for (int i = 0; i < locations.length - 1; i++) {
            for (int j = 0; j < locations.length - i - 1; j++) {
                if (locations[j][3] > locations[j + 1][3]) {
                    int[] tmp = new int[4];
                    for (int k = 0; k < 4; k++) {
                        tmp[k] = locations[j][k];
                        locations[j][k] = locations[j + 1][k];
                        locations[j + 1][k] = tmp[k];
                    }
                }
            }
        }

        for (int i = 0; i < 10; i++) {
            bestLocations[i][0] = locations[i][0]; // x
            bestLocations[i][1] = locations[i][1]; // y
            bestLocations[i][2] = locations[i][2]; // direction
        }

        return bestLocations;
    }


    /**
     * Create binary tree for heuristic algorithm.
     */
    public void createTree()
    {
        Node root = new Node(4, 7);

        char[][] planarRegion = getPlanarRegion();
        placeLines(planarRegion);
        int[][] coordinates = getAndPlaceGreedy(planarRegion);

        for (int i = 0; i < coordinates.length; i++) {
            addNode(root, coordinates[i][0], coordinates[i][1]);
        }

        traverseInOrder(root);
    }


    private Node addNode(Node current, int x, int y)
    {
        if (current == null) {
            return new Node(x, y);
        }

        // Perhaps the tree structure should be modified so that
        // any location with y < 7 is on the left, while any location
        //  with y >= 7 is on the right. Locations with identical y values
        // can then be sorted by x values.
        if (x < current.x && y < current.y) {
            current.left = addNode(current.left, x, y);
        }
        else {
            current.right = addNode(current.right, x, y);
        }

        return current;
    }

    /**
     * Depth first search traversal method.
     * @param node The node at which the traversal begins.
     */
    public void traverseInOrder(Node node)
    {
        if (node != null) {
            traverseInOrder(node.left);
            System.out.println("(" + node.x + ", " + node.y + ")");
            traverseInOrder(node.right);
        }
    }


    /**
     * Set each value in planarRegion1 to the corresponding value in
     * planarRegion2.
     * @param planarRegion1 The new copy of planarRegion2.
     * @param planarRegion2 The 2D array that is copied onto planarRegion1.
     * @return planarRegion1, the copy of planarRegion2.
     */
    public char[][] copy(char[][] planarRegion1, char[][] planarRegion2)
    {
        for (int row = 0; row < planarRegion2.length; row++) {
            for (int col = 0; col < planarRegion2[row].length; col++) {
                planarRegion1[row][col] = planarRegion2[row][col];
            }
        }
        return planarRegion1;
    }

    /**
     * This method places camera sensors according to the specified coordinates,
     * and returns the updated planar region.
     * @param planarRegion The planar region on which the camera sensors are
     *                     to be placed.
     * @param coordinates The coordinates at which to place the
     *                    cameras. Coordinates must be bound by
     *                    the height and width of the planar region.
     * @return The updated planar region.
     */
    public char[][] placeCoordinates(char[][] planarRegion, int[][] coordinates)
    {
        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];

            planarRegion[y][x] = 'S';
        }

        return planarRegion;
    }


    /**
     * Get the planar region to be covered. Dimensions of region:
     * 15 high by 7 wide.
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
     * Place the barrier lines in the given planar region, and
     * return the updated planar region.
     * @param planarRegion The planar region on which to place
     *                     the lines.
     * @return The updated planar region.
     */
    public char[][] placeLines(char[][] planarRegion)
    {
        for (int y = 0; y < planarRegion.length; y++) {
            for (int x = 0; x < planarRegion[y].length; x++) {
                // Place the top line
                if (x > 0 && x < 6 && y == 1) {
                    planarRegion[y][x] = 'B';
                }

                // Place the bottom line
                if (x > 0 && x < 6 && y == 6) {
                    planarRegion[y][x] = 'B';
                }

                // Place the left line
                if (x == 1 && y > 1 && y < 6) {
                    planarRegion[y][x] = 'B';
                }

                // Place the right line
                if (x == 5 && y > 1 && y < 6) {
                    planarRegion[y][x] = 'B';
                }
            }
        }

        return planarRegion;
    }


    /**
     * Get the number of uncovered barrier points on the plane.
     * @param planarRegion The region on which the barrier lines lay.
     * @return The number of uncovered barrier points on the plane.
     */
    public int getNumUncoveredPoints(char[][] planarRegion)
    {
        int numUncoveredPoints = 0;
        for (int row = 0; row < planarRegion.length; row++) {
            for (int col = 0; col < planarRegion[row].length; col++) {
                if (planarRegion[row][col] == 'B') {
                    numUncoveredPoints++;
                }
            }
        }
        return numUncoveredPoints;
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
