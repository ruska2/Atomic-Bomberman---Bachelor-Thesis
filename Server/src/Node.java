import java.util.LinkedList;

class Node
{
    final double[] coords;
    final double[] dimensions;
    final LinkedList<Node> children;
    final boolean leaf;

    Node parent;

    public Node(double[] coords, double[] dimensions, boolean leaf)
    {
        this.coords = new double[coords.length];
        this.dimensions = new double[dimensions.length];
        System.arraycopy(coords, 0, this.coords, 0, coords.length);
        System.arraycopy(dimensions, 0, this.dimensions, 0, dimensions.length);
        this.leaf = leaf;
        children = new LinkedList<Node>();
    }

}