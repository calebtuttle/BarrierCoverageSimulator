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
//        placeRandomly();
        placeGreedy();
        System.out.println();
        print2DArray(planarRegion);
    }


    /**
     * Get 10 random (x, y) coordinates within the 15 x 10 planar
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
            int x = gen.nextInt(10);
            int y = gen.nextInt(15);

            coordinates[i][0] = x;
            coordinates[i][1] = y;
        }

        return coordinates;
    }

    /**
     * Place cameras (denoted by the char 'C') in the
     * planar region according to the random algorithm by
     * modifying planarRegion to have a 'C' wherever a camera
     * is placed.
     */
    public void placeRandomly()
    {
        int[][] coordinates = getRandomCoordinates();

        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];

            planarRegion[y][x] = 'C';
        }
    }


    /**
     * Get coordinates from greedy algorithm.
     * TODO: How do we determine which direction the camera faces, and how
     *  do we indicate its facing direction? How do we determine how much of
     *  a line a camera covers?
     * @return
     */
    public int[][] getGreedyCoordinates()
    {
        int[][] coordinates = new int[10][2];
        gen = new Random();

        for (int i = 0; i < 10; i++) {
            int x = gen.nextInt(6) + 2; // Restrict bounds
            int y = gen.nextInt(6) + 2;

            coordinates[i][0] = x;
            coordinates[i][1] = y;
        }


        return coordinates;
    }

    /**
     * Place cameras in the planar region according to
     * the greedy algorithm by modifying planarRegion
     * to have a 'C' wherever a camera is placed.
     */
    public void placeGreedy()
    {
        int[][] coordinates = getGreedyCoordinates();

        for (int i = 0; i < coordinates.length; i++) {
            int x = coordinates[i][0];
            int y = coordinates[i][1];

            planarRegion[y][x] = 'C';
        }
    }


    /**
     * Get the planar region to be covered. Dimensions of region:
     * 15 high by 10 wide.
     * @return a 2D array of chars representing the coverable planar region.
     */
    public char[][] getPlanarRegion()
    {
        char[][] planarRegion = new char[15][10];

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
                if (x > 1 && x < 8 && y == 2) {
                    planarRegion[y][x] = '-';
                }

                // Place the bottom line
                if (x > 1 && x < 8 && y == 7) {
                    planarRegion[y][x] = '-';
                }

                // Place the left line
                if (x == 2 && y > 1 && y < 8) {
                    planarRegion[y][x] = '|';
                }

                // Place the right line
                if (x == 7 && y > 1 && y < 8) {
                    planarRegion[y][x] = '|';
                }
            }
        }
    }

    /**
     * Print the given 2D array.
     * @param array the 2D array to be printed.
     */
    public void print2DArray(char[][] array)
    {
        for (int y = 0; y < array.length; y++) {
            for (int x = 0; x < array[y].length; x++) {
                System.out.print(array[y][x] + " ");
            }
            System.out.println();
        }
    }
}
