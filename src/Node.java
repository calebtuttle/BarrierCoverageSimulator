/**
 * Node for binary search tree for heuristic algorithm.
 */
public class Node {
    int x;
    int y;
    Node left;
    Node right;

    public Node(int x, int y)
    {
        this.x = x;
        this.y = y;
        left = null;
        right = null;
    }
}
