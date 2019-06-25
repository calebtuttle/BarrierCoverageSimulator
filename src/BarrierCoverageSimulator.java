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
            int x = gen.nextInt(11);
            int y = gen.nextInt(16);

            coordinates[i][0] = x;
            coordinates[i][1] = y;
        }

        return coordinates;
    }


    /**
     * Place cameras in the planar region according
     * to the random algorithm.
     */
    public void placeRandomly()
    {
//        for (int)
    }


    /**
     * Get the planar region to be covered. Dimensions of region:
     * 15 high by 10 wide.
     * @return a 2D array representing the coverable planar region.
     */
    public char[][] getPlanarRegion()
    {
        char[][] planarRegion = new char[15][10];
        return planarRegion;
    }

    public void print2DArray(char[][] array)
    {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                System.out.print(array[i][j] + " ");
            }
            System.out.println();
        }
    }
}
