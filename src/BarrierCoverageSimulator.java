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

        int[][] ranCoFinal = getRandomCoordinatesV3();
        printSequenceOfGraphs(ranCoFinal);

//        int[][] coordinates = getAndPlaceGreedy(planarRegion);
//        printSequenceOfGraphs(coordinates);

//        int[][] greedy2 = getAndPlaceGreedyV2(planarRegion);
//        printSequenceOfGraphs(greedy2);

//        int[][] coordinates = getAndPlaceHeuristic(planarRegion);
    }


    /**
     * Print random (x, y) coordinates.
     */
    public void printCoordinates(int[][] coordinates)
    {
//        coordinates = convertCoordinates(coordinates);

        for(int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];
            int d = coordinates[i][2];
            System.out.println("Location: (" + x + ", " + y + ")" +
                    "\n\rDirection: " + d);
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
     * Get 10 random (x, y) coordinates within the 7 x 15 planar
     * region. The first element in the 2nd dimension of the array
     * is always an x coordinate, while the second element is the y
     * coordinate.
     * @param direction The camera's facing direction. 1 = right.
     *                  2 = up. 3 = left. 4 = down.
     * @return a 2D array of 10 (x, y) coordinates along with the
     *         given direction.
     */
    public int[][] getRandomCoordinatesV2(int direction)
    {
        int[][] coordinates = new int[10][3];
        gen = new Random();

        for (int i = 0; i < 10; i++) {
            int x = gen.nextInt(7);
            int y = gen.nextInt(15);

            coordinates[i][0] = x;
            coordinates[i][1] = y;
            coordinates[i][2] = direction;

            // Make sure the random coordinate is not on a barrier line
            char[][] planarRegion = getPlanarRegion();
            int numBarrierPoints = getNumUncoveredPoints(planarRegion);

            updateCovered(planarRegion, y, x, direction, 5, 1);
            if (numBarrierPoints == getNumUncoveredPoints(planarRegion)) {
                i--;
            }
        }

        return coordinates;
    }

    /**
     * Get 10 random (x, y) coordinates within the 7 x 15 planar
     * region. The first element in the 2nd dimension of the array
     * is always an x coordinate, while the second element is the y
     * coordinate. This method relies on getRandomCoordinatesV2.
     * @return a 2D array of 40 (x, y) coordinates, 10 coordinates for
     * each of the 4 directions.
     */
    public int[][] getRandomCoordinatesV3()
    {
        int[][] ranCoFinal = new int[40][3]; // random coordinates
        int[][] ranCo1 = getRandomCoordinatesV2(1);
        int[][] ranCo2 = getRandomCoordinatesV2(2);
        int[][] ranCo3 = getRandomCoordinatesV2(3);
        int[][] ranCo4 = getRandomCoordinatesV2(4);

        for (int i = 0; i < 40; i++) {
            if (i < 10) { // direction 1
                ranCoFinal[i][0] = ranCo1[i][0];
                ranCoFinal[i][1] = ranCo1[i][1];
                ranCoFinal[i][2] = ranCo1[i][2];
            }
            else if (i < 20) { // direction 2
                int j = i - 10;
                ranCoFinal[i][0] = ranCo2[j][0];
                ranCoFinal[i][1] = ranCo2[j][1];
                ranCoFinal[i][2] = ranCo2[j][2];
            }
            else if (i < 30) { // direction 3
                int j = i - 20;
                ranCoFinal[i][0] = ranCo3[j][0];
                ranCoFinal[i][1] = ranCo3[j][1];
                ranCoFinal[i][2] = ranCo3[j][2];
            }
            else if (i < 40) { // direction 4
                int j = i - 30;
                ranCoFinal[i][0] = ranCo4[j][0];
                ranCoFinal[i][1] = ranCo4[j][1];
                ranCoFinal[i][2] = ranCo4[j][2];
            }
        }

        return ranCoFinal;
    }


    /**
     * Get coordinates from greedy algorithm, place the camera sensors on
     * the plane, and update the plane to reflect which points are covered.
     * @param planarRegion The planar region on which the sensors should be
     *                     placed according to the greedy algorithm. It should
     *                     have lines in it before it is passed to this method.
     * @return a 2D array of the coordinates at which the camera sensors
     * should be place and the direction at which they should face. The
     * second dimension has 3 elements: x, y, and direction.
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

        int index = 0;
        for (int row = 0; row < planarRegion1.length; row++) {
            for (int col = 0; col < planarRegion1[row].length; col++) {
                // Check coverage at this point when sensor faces right
                updateCovered(planarRegion1, row, col, 1, 5, 1);


//                    // DELETE ME
//                    System.out.println("Position: (" + locations[index][0] + ", " +
//                            locations[index][1] + ")");
//                    System.out.println("Direction: " + locations[index][2]);
//                    print2DCharArray(planarRegion1);


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


//
//                    // DELETE ME
//                    System.out.println("Position: (" + locations[index][0] + ", " +
//                            locations[index][1] + ")");
//                    System.out.println("Direction: " + locations[index][2]);
//                    print2DCharArray(planarRegion1);



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


//                    // DELETE ME
//                    System.out.println("Position: (" + locations[index][0] + ", " +
//                            locations[index][1] + ")");
//                    System.out.println("Direction: " + locations[index][2]);
//                    print2DCharArray(planarRegion1);


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


//                    // DELETE ME
//                    System.out.println("Position: (" + locations[index][0] + ", " +
//                            locations[index][1] + ")");
//                    System.out.println("Direction: " + locations[index][2]);
//                    print2DCharArray(planarRegion1);


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

        coordinates = findBestLocations(locations);

        return coordinates;
    }

    /**
     * Get coordinates from greedy algorithm, place the camera sensors on
     * the plane, and update the plane to reflect which points are covered.
     * @param planarRegion The planar region on which the sensors should be
     *                     placed according to the greedy algorithm. It should
     *                     have lines in it before it is passed to this method.
     * @return a 2D array of the coordinates at which the camera sensors
     * should be place and the direction at which they should face. The
     * second dimension has 3 elements: x, y, and direction.
     */
    public int[][] getAndPlaceGreedyV2(char[][] planarRegion)
    {
        int[][] coordinatesFinal = new int[40][3];


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

        for (int d = 1; d < 5; d++) {
            // An array with each location, direction, and the number of uncovered barrier
            // points there are at that location and direction.
            // Indices: 0: x. 1: y. 2: direction. 3: number of uncovered barrier points.
            int[][] locations = new int[planarRegion.length * planarRegion[0].length][4];
            int index = 0;

            for (int row = 0; row < planarRegion1.length; row++) {
                for (int col = 0; col < planarRegion1[row].length; col++) {
                    updateCovered(planarRegion1, row, col, d, 5, 1);

                    // Enter this location, direction, and the number of barrier points covered.
                    locations[index][0] = col; // x
                    locations[index][1] = row; // y
                    locations[index][2] = d; // direction
                    locations[index][3] = getNumUncoveredPoints(planarRegion1);
                    index++;

                    // reset planar region to what it was at the beginning of
                    // this while-loop iteration
                    planarRegion1 = copy(planarRegion1, planarRegion2);
                }
            }

            if (d == 1) { // direction 1
                int[][] temp = findBestLocations(locations);

                for (int i = 0; i < 10; i++) {
                    coordinatesFinal[i][0] = temp[i][0];
                    coordinatesFinal[i][1] = temp[i][1];
                    coordinatesFinal[i][2] = temp[i][2];
                }
            }
            if (d == 2) { // direction 2
                int[][] temp = findBestLocations(locations);

                for (int i = 0; i < 10; i++) {
                    coordinatesFinal[i + 10][0] = temp[i][0];
                    coordinatesFinal[i + 10][1] = temp[i][1];
                    coordinatesFinal[i + 10][2] = temp[i][2];
                }
            }
            if (d == 3) { // direction 3
                int[][] temp = findBestLocations(locations);

                for (int i = 0; i < 10; i++) {
                    coordinatesFinal[i + 20][0] = temp[i][0];
                    coordinatesFinal[i + 20][1] = temp[i][1];
                    coordinatesFinal[i + 20][2] = temp[i][2];
                }
            }
            if (d == 4) { // direction 4
                int[][] temp = findBestLocations(locations);

                for (int i = 0; i < 10; i++) {
                    coordinatesFinal[i + 30][0] = temp[i][0];
                    coordinatesFinal[i + 30][1] = temp[i][1];
                    coordinatesFinal[i + 30][2] = temp[i][2];
                }
            }

        }

        return coordinatesFinal;
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
            int sensingLimit = (planarRegion[row].length < (col + maxSenseRange + 1)) ?
                    planarRegion[row].length : (col + maxSenseRange + 1);

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
            int sensingLimit = (planarRegion.length < (row + maxSenseRange + 1)) ?
                    planarRegion.length : (row + maxSenseRange + 1);

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
     *                  int[][] locations = new int[_][4].) Indices: 0: x.
     *                  1: y. 2: direction. 3: number of uncovered barrier points.
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

//        for (int i = 0; i < locations.length; i++) {
//            System.out.println(locations[i][3]);
//        }

        for (int i = 0; i < 10; i++) {
            bestLocations[i][0] = locations[i][0]; // x
            bestLocations[i][1] = locations[i][1]; // y
            bestLocations[i][2] = locations[i][2]; // direction
        }

        return bestLocations;
    }


    public int[][] getAndPlaceHeuristic(char[][] planarRegion)
    {
        int[][] coordinates = new int[8][3]; // heuristic coordinates
        int[][] greedyCo = getAndPlaceGreedyV2(planarRegion); // greedy coordinates

        char[][] planarRegion1 = planarRegion; // Temporary
        char[][] planarRegion2 = new char[planarRegion.length][planarRegion[0].length];
        planarRegion2 = copy(planarRegion2, planarRegion1); // Used to reset temporary

        int coIndex = 0;

        for (int i = 0; i < 4; i++) {
            // The minimum number of uncovered barrier-line points
            int min = 18;

            for (int j = 0; j < 10; j++) {
                for (int k = j + 1; k < 10; k++) {

                    int iAdd = i * 10; // index addition

                    // Place the first sensor
                    int col1 = greedyCo[j + iAdd][0]; // x
                    int row1 = greedyCo[j + iAdd][1]; // y
                    int d1 = greedyCo[j + iAdd][2]; // direction
                    updateCovered(planarRegion1, row1, col1, d1, 5, 1);

                    // Place second sensor
                    int col2 = greedyCo[k + iAdd][0]; // x
                    int row2 = greedyCo[k + iAdd][1]; // y
                    int d2 = greedyCo[k + iAdd][2]; // direction
                    updateCovered(planarRegion1, row2, col2, d2, 5, 1);

                    int uncovered = getNumUncoveredPoints(planarRegion1);

                    if (uncovered < min) {
                        coordinates[coIndex][0] = col1;
                        coordinates[coIndex][1] = row1;
                        coordinates[coIndex][2] = d1;

                        coordinates[coIndex + 1][0] = col2;
                        coordinates[coIndex + 1][1] = row2;
                        coordinates[coIndex + 1][2] = d2;

                        min = uncovered;
                    }

                    // reset
                    planarRegion1 = copy(planarRegion1, planarRegion2);
                }
            }

            // This prints a graph containing the two sensors that have just
            // been found to be the best positions for a certain direction.
            updateCovered(planarRegion1, coordinates[coIndex][1], coordinates[coIndex][0],
                    coordinates[coIndex][2], 5, 1);
            updateCovered(planarRegion1, coordinates[coIndex + 1][1], coordinates[coIndex + 1][0],
                    coordinates[coIndex + 1][2], 5, 1);
            System.out.println("\n\rDirection: " + coordinates[coIndex][2]);
            System.out.println("Locations: (" + coordinates[coIndex][0] +", " + coordinates[coIndex][1] +
                    "), (" + coordinates[coIndex + 1][0] + ", " + coordinates[coIndex + 1][1] + ")");
            print2DCharArray(planarRegion1);
            planarRegion1 = copy(planarRegion1, planarRegion2);

            coIndex += 2;
        }

        // Place sensors
        for (int i = 0; i < coordinates.length; i++) {
            updateCovered(planarRegion2, coordinates[i][1], coordinates[i][0],
                    coordinates[i][2], 5, 1);
        }

        System.out.println("\n\rFinal:");
        print2DCharArray(planarRegion2);

        return coordinates;
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

        placeLines(planarRegion);

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
     * Print a 2D char array for each location-direction set given.
     * @param coordinates a 2D array whose second dimension must be
     *                    of length 3.
     */
    public void printSequenceOfGraphs(int[][] coordinates)
    {
        for (int i = 0; i < coordinates.length; i++) {
            char[][] p = getPlanarRegion();
            int numBarrierPoints = getNumUncoveredPoints(p);

            int x = coordinates[i][0];
            int y = coordinates[i][1];
            int direction = coordinates[i][2];

            System.out.println("\n\rLocation: (" + x + ", " +
                    y + ")\n\rDirection: " + direction);

            updateCovered(p, y, x, direction, 5, 1);
            int numCovered = numBarrierPoints - getNumUncoveredPoints(p);

            System.out.println("Number of covered points: " + numCovered);

            print2DCharArray(p);
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
