
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Implementation of an arbitrary-dimension RTree. Based on R-Trees: A Dynamic
 * Index Structure for Spatial Searching (Antonn Guttmann, 1984)
 *
 * This class is not thread-safe.
 *
 * Copyright 2010 Russ Weeks rweeks@newbrightidea.com Licensed under the GNU
 * LGPL License details here: http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * @param <T>
 *          the type of entry to store in this RTree.
 */
public class RTree<T>
{
    public enum SeedPicker { LINEAR, QUADRATIC }

    private final int maxEntries;
    private final int minEntries;
    private final int numDims;

    public List<Node> list = new ArrayList<>();
    public List<List<Node>> lists = new ArrayList<>();
    public int robi = 0;

    private final double[] pointDims;

    private final SeedPicker seedPicker;

    private Node root;

    private volatile int size;

    /**
     * Creates a new RTree.
     *
     * @param maxEntries
     *          maximum number of entries per node
     * @param minEntries
     *          minimum number of entries per node (except for the root node)
     * @param numDims
     *          the number of dimensions of the RTree.
     */
    public RTree(int maxEntries, int minEntries, int numDims, SeedPicker seedPicker)
    {
        assert (minEntries <= (maxEntries / 2));
        this.numDims = numDims;
        this.maxEntries = maxEntries;
        this.minEntries = minEntries;
        this.seedPicker = seedPicker;
        pointDims = new double[numDims];
        root = buildRoot(true);
    }

    public RTree(int maxEntries, int minEntries, int numDims)
    {
        this(maxEntries, minEntries, numDims, SeedPicker.LINEAR);
    }

    private Node buildRoot(boolean asLeaf)
    {
        double[] initCoords = new double[numDims];
        double[] initDimensions = new double[numDims];
        for (int i = 0; i < this.numDims; i++)
        {
            initCoords[i] = (double) Math.sqrt(Double.MAX_VALUE);
            initDimensions[i] = -2.0f * (double) Math.sqrt(Double.MAX_VALUE);
        }
        return new Node(initCoords, initDimensions, asLeaf);
    }

    /**
     * Builds a new RTree using default parameters: maximum 50 entries per node
     * minimum 2 entries per node 2 dimensions
     */
    public RTree()
    {
        this(1, 1
                , 2, SeedPicker.LINEAR);
    }

    /**
     * @return the maximum number of entries per node
     */
    public int getMaxEntries()
    {
        return maxEntries;
    }

    /**
     * @return the minimum number of entries per node for all nodes except the
     *         root.
     */
    public int getMinEntries()
    {
        return minEntries;
    }

    /**
     * @return the number of dimensions of the tree
     */
    public int getNumDims()
    {
        return numDims;
    }

    /**
     * @return the number of items in this tree.
     */
    public int size()
    {
        return size;
    }

    /**
     * Searches the RTree for objects overlapping with the given rectangle.
     *
     * @param coords
     *          the corner of the rectangle that is the lower bound of every
     *          dimension (eg. the top-left corner)
     * @param dimensions
     *          the dimensions of the rectangle.
     * @return a list of objects whose rectangles overlap with the given
     *         rectangle.
     */
    public List<T> search(double[] coords, double[] dimensions)
    {
        assert (coords.length == numDims);
        assert (dimensions.length == numDims);
        LinkedList<T> results = new LinkedList<T>();
        search(coords, dimensions, root, results);
        return results;
    }

    private void search(double[] coords, double[] dimensions, Node n,
                        LinkedList<T> results)
    {
        if (n.leaf)
        {
            for (Node e : n.children)
            {
                if (isOverlap(coords, dimensions, e.coords, e.dimensions))
                {
                    results.add(((Entry) e).entry);
                }
            }
        }
        else
        {
            for (Node c : n.children)
            {
                if (isOverlap(coords, dimensions, c.coords, c.dimensions))
                {
                    search(coords, dimensions, c, results);
                }
            }
        }
    }

    /**
     * Deletes the entry associated with the given rectangle from the RTree
     *
     * @param coords
     *          the corner of the rectangle that is the lower bound in every
     *          dimension
     * @param dimensions
     *          the dimensions of the rectangle
     * @param entry
     *          the entry to delete
     * @return true iff the entry was deletedfl from the RTree.
     */

    public boolean delete(double[] coords, double[] dimensions, T entry)
    {
        assert (coords.length == numDims);
        assert (dimensions.length == numDims);
        Node l = findLeaf(root, coords, dimensions, entry);
        if ( l == null ) {
            System.out.println("WTF?");
            findLeaf(root, coords, dimensions, entry);
        }
        assert (l != null) : "Could not find leaf for entry to delete";
        assert (l.leaf) : "Entry is not found at leaf?!?";
        ListIterator<Node> li = l.children.listIterator();
        T removed = null;
        while (li.hasNext())
        {
            @SuppressWarnings("unchecked")
            Entry e = (Entry) li.next();
            if (e.entry.equals(entry))
            {
                removed = e.entry;
                li.remove();
                break;
            }
        }
        if (removed != null)
        {
            condenseTree(l);
            size--;
        }
        if ( size == 0 )
        {
            root = buildRoot(true);
        }
        return (removed != null);
    }

    public boolean delete(double[] coords, T entry)
    {
        return delete(coords, pointDims, entry);
    }

    private Node findLeaf(Node n, double[] coords, double[] dimensions, T entry)
    {
        if (n.leaf)
        {
            for (Node c : n.children)
            {
                if (((Entry) c).entry.equals(entry))
                {
                    return n;
                }
            }
            return null;
        }
        else
        {
            for (Node c : n.children)
            {
                if (isOverlap(c.coords, c.dimensions, coords, dimensions))
                {
                    Node result = findLeaf(c, coords, dimensions, entry);
                    if (result != null)
                    {
                        return result;
                    }
                }
            }
            return null;
        }
    }

    private void condenseTree(Node n)
    {
        Set<Node> q = new HashSet<Node>();
        while (n != root)
        {
            if (n.leaf && (n.children.size() < minEntries))
            {
                q.addAll(n.children);
                n.parent.children.remove(n);
            }
            else if (!n.leaf && (n.children.size() < minEntries))
            {
                // probably a more efficient way to do this...
                LinkedList<Node> toVisit = new LinkedList<Node>(n.children);
                while (!toVisit.isEmpty())
                {
                    Node c = toVisit.pop();
                    if (c.leaf)
                    {
                        q.addAll(c.children);
                    }
                    else
                    {
                        toVisit.addAll(c.children);
                    }
                }
                n.parent.children.remove(n);
            }
            else
            {
                tighten(n);
            }
            n = n.parent;
        }
        if ( root.children.size() == 0 )
        {
            root = buildRoot(true);
        }
        else if ( (root.children.size() == 1) && (!root.leaf) )
        {
            root = root.children.get(0);
            root.parent = null;
        }
        else
        {
            tighten(root);
        }
        for (Node ne : q)
        {
            @SuppressWarnings("unchecked")
            Entry e = (Entry) ne;
            insert(e.coords, e.dimensions, e.entry);
        }
        size -= q.size();
    }

    /**
     * Empties the RTree
     */

    public void clear()
    {
        root = buildRoot(true);
        // let the GC take care of the rest.
    }

    /**
     * Inserts the given entry into the RTree, associated with the given
     * rectangle.
     *
     * @param coords
     *          the corner of the rectangle that is the lower bound in every
     *          dimension
     * @param dimensions
     *          the dimensions of the rectangle
     * @param entry
     *          the entry to insert
     */
    public void insert(double[] coords, double[] dimensions, T entry)
    {
        assert (coords.length == numDims);
        assert (dimensions.length == numDims);
        Entry e = new Entry(coords, dimensions, entry);
        Node l = chooseLeaf(root, e);
        l.children.add(e);
        size++;
        e.parent = l;
        if (l.children.size() > maxEntries)
        {
            Node[] splits = splitNode(l);
            adjustTree(splits[0], splits[1]);
        }
        else
        {
            adjustTree(l, null);
        }
    }

    /**
     * Convenience method for inserting a point
     * @param coords
     * @param entry
     */


    public void insert(double[] coords, T entry)
    {
        insert(coords, pointDims, entry);
    }

    private void adjustTree(Node n, Node nn)
    {
        if (n == root)
        {
            if (nn != null)
            {
                // build new root and add children.
                root = buildRoot(false);
                root.children.add(n);
                n.parent = root;
                root.children.add(nn);
                nn.parent = root;
            }
            tighten(root);
            return;
        }
        tighten(n);
        if (nn != null)
        {
            tighten(nn);
            if (n.parent.children.size() > maxEntries)
            {
                Node[] splits = splitNode(n.parent);
                adjustTree(splits[0], splits[1]);

            }
        }
        if (n.parent != null)
        {
            adjustTree(n.parent, null);
        }
    }

    private Node[] splitNode(Node n)
    {
        // TODO: this class probably calls "tighten" a little too often.
        // For instance the call at the end of the "while (!cc.isEmpty())" loop
        // could be modified and inlined because it's only adjusting for the addition
        // of a single node.  Left as-is for now for readability.
        @SuppressWarnings("unchecked")
        Node[] nn = new Node[]
                { n, new Node(n.coords, n.dimensions, n.leaf) };
        nn[1].parent = n.parent;
        if (nn[1].parent != null)
        {
            nn[1].parent.children.add(nn[1]);
        }
        LinkedList<Node> cc = new LinkedList<Node>(n.children);
        n.children.clear();
        Node[] ss = seedPicker == SeedPicker.LINEAR ? lPickSeeds(cc) : qPickSeeds(cc);
        nn[0].children.add(ss[0]);
        nn[1].children.add(ss[1]);
        tighten(nn);
        while (!cc.isEmpty())
        {
            if ((nn[0].children.size() >= minEntries)
                    && (nn[1].children.size() + cc.size() == minEntries))
            {
                nn[1].children.addAll(cc);
                cc.clear();
                tighten(nn); // Not sure this is required.
                return nn;
            }
            else if ((nn[1].children.size() >= minEntries)
                    && (nn[0].children.size() + cc.size() == minEntries))
            {
                nn[0].children.addAll(cc);
                cc.clear();
                tighten(nn); // Not sure this is required.
                return nn;
            }
            Node c = seedPicker == SeedPicker.LINEAR ? lPickNext(cc) : qPickNext(cc, nn);
            Node preferred;
            double e0 = getRequiredExpansion(nn[0].coords, nn[0].dimensions, c);
            double e1 = getRequiredExpansion(nn[1].coords, nn[1].dimensions, c);
            if (e0 < e1)
            {
                preferred = nn[0];
            }
            else if (e0 > e1)
            {
                preferred = nn[1];
            }
            else
            {
                double a0 = getArea(nn[0].dimensions);
                double a1 = getArea(nn[1].dimensions);
                if (a0 < a1)
                {
                    preferred = nn[0];
                }
                else if (e0 > a1)
                {
                    preferred = nn[1];
                }
                else
                {
                    if (nn[0].children.size() < nn[1].children.size())
                    {
                        preferred = nn[0];
                    }
                    else if (nn[0].children.size() > nn[1].children.size())
                    {
                        preferred = nn[1];
                    }
                    else
                    {
                        preferred = nn[(int) Math.round(Math.random())];
                    }
                }
            }
            preferred.children.add(c);
            tighten(preferred);
        }
        return nn;
    }

    // Implementation of Quadratic PickSeeds
    private Node[] qPickSeeds(LinkedList<Node> nn)
    {
        @SuppressWarnings("unchecked")
        Node[] bestPair = new Node[2];
        double maxWaste = -1.0f * Double.MAX_VALUE;
        for (Node n1: nn)
        {
            for (Node n2: nn)
            {
                if (n1 == n2) continue;
                double n1a = getArea(n1.dimensions);
                double n2a = getArea(n2.dimensions);
                double ja = 1.0f;
                for ( int i = 0; i < numDims; i++ )
                {
                    double jc0 = Math.min(n1.coords[i], n2.coords[i]);
                    double jc1 = Math.max(n1.coords[i] + n1.dimensions[i], n2.coords[i] + n2.dimensions[i]);
                    ja *= (jc1 - jc0);
                }
                double waste = ja - n1a - n2a;
                if ( waste > maxWaste )
                {
                    maxWaste = waste;
                    bestPair[0] = n1;
                    bestPair[1] = n2;
                }
            }
        }
        nn.remove(bestPair[0]);
        nn.remove(bestPair[1]);
        return bestPair;
    }

    /**
     * Implementation of QuadraticPickNext
     * @param cc the children to be divided between the new nodes, one item will be removed from this list.
     * @param nn the candidate nodes for the children to be added to.
     */
    private Node qPickNext(LinkedList<Node> cc, Node[] nn)
    {
        double maxDiff = -1.0f * Double.MAX_VALUE;
        Node nextC = null;
        for ( Node c: cc )
        {
            double n0Exp = getRequiredExpansion(nn[0].coords, nn[0].dimensions, c);
            double n1Exp = getRequiredExpansion(nn[1].coords, nn[1].dimensions, c);
            double diff = Math.abs(n1Exp - n0Exp);
            if (diff > maxDiff)
            {
                maxDiff = diff;
                nextC = c;
            }
        }
        assert (nextC != null) : "No node selected from qPickNext";
        cc.remove(nextC);
        return nextC;
    }

    // Implementation of LinearPickSeeds
    private Node[] lPickSeeds(LinkedList<Node> nn)
    {
        @SuppressWarnings("unchecked")
        Node[] bestPair = new Node[2];
        boolean foundBestPair = false;
        double bestSep = 0.0f;
        for (int i = 0; i < numDims; i++)
        {
            double dimLb = Double.MAX_VALUE, dimMinUb = Double.MAX_VALUE;
            double dimUb = -1.0f * Double.MAX_VALUE, dimMaxLb = -1.0f * Double.MAX_VALUE;
            Node nMaxLb = null, nMinUb = null;
            for (Node n : nn)
            {
                if (n.coords[i] < dimLb)
                {
                    dimLb = n.coords[i];
                }
                if (n.dimensions[i] + n.coords[i] > dimUb)
                {
                    dimUb = n.dimensions[i] + n.coords[i];
                }
                if (n.coords[i] > dimMaxLb)
                {
                    dimMaxLb = n.coords[i];
                    nMaxLb = n;
                }
                if (n.dimensions[i] + n.coords[i] < dimMinUb)
                {
                    dimMinUb = n.dimensions[i] + n.coords[i];
                    nMinUb = n;
                }
            }
            double sep = (nMaxLb == nMinUb) ? -1.0f :
                    Math.abs((dimMinUb - dimMaxLb) / (dimUb - dimLb));
            if (sep >= bestSep)
            {
                bestPair[0] = nMaxLb;
                bestPair[1] = nMinUb;
                bestSep = sep;
                foundBestPair = true;
            }
        }
        // In the degenerate case where all points are the same, the above
        // algorithm does not find a best pair.  Just pick the first 2
        // children.
        if ( !foundBestPair )
        {
            bestPair = new Node[] { nn.get(0), nn.get(1) };
        }
        nn.remove(bestPair[0]);
        nn.remove(bestPair[1]);
        return bestPair;
    }

    /**
     * Implementation of LinearPickNext
     * @param cc the children to be divided between the new nodes, one item will be removed from this list.
     */
    private Node lPickNext(LinkedList<Node> cc)
    {
        return cc.pop();
    }

    private void tighten(Node... nodes)
    {
        assert(nodes.length >= 1): "Pass some nodes to tighten!";
        for (Node n: nodes) {

            //if(n.dimensions[0] == 0.0 && n != root) break;
            assert(n.children.size() > 0) : "tighten() called on empty node!";
            double[] minCoords = new double[numDims];
            double[] maxCoords = new double[numDims];
            for (int i = 0; i < numDims; i++)
            {
                minCoords[i] = Double.MAX_VALUE;
                maxCoords[i] = -1.0f * Double.MAX_VALUE;

                for (Node c : n.children)
                {
                    // we may have bulk-added a bunch of children to a node (eg. in
                    // splitNode)
                    // so here we just enforce the child->parent relationship.
                    c.parent = n;
                    if (c.coords[i] < minCoords[i])
                    {
                        minCoords[i] = c.coords[i];
                    }
                    if ((c.coords[i] + c.dimensions[i]) > maxCoords[i])
                    {
                        maxCoords[i] = (c.coords[i] + c.dimensions[i]);
                    }
                }
            }
            for (int i = 0; i < numDims; i++)
            {
                // Convert max coords to dimensions
                maxCoords[i] -= minCoords[i];
            }
            System.arraycopy(minCoords, 0, n.coords, 0, numDims);
            System.arraycopy(maxCoords, 0, n.dimensions, 0, numDims);
        }
    }

    private Node chooseLeaf(Node n, Entry e)
    {
        if (n.leaf)
        {
            return n;
        }
        double minInc = Double.MAX_VALUE;
        Node next = null;
        for (Node c : n.children)
        {
            double inc = getRequiredExpansion(c.coords, c.dimensions, e);
            if (inc < minInc)
            {
                minInc = inc;
                next = c;
            }
            else if (inc == minInc)
            {
                double curArea = 1.0f;
                double thisArea = 1.0f;
                for (int i = 0; i < c.dimensions.length; i++)
                {
                    curArea *= next.dimensions[i];
                    thisArea *= c.dimensions[i];
                }
                if (thisArea < curArea)
                {
                    next = c;
                }
            }
        }
        return chooseLeaf(next, e);
    }

    /**
     * Returns the increase in area necessary for the given rectangle to cover the
     * given entry.
     */
    private double getRequiredExpansion(double[] coords, double[] dimensions, Node e)
    {
        double area = getArea(dimensions);
        double[] deltas = new double[dimensions.length];
        for (int i = 0; i < deltas.length; i++)
        {
            if (coords[i] + dimensions[i] < e.coords[i] + e.dimensions[i])
            {
                deltas[i] = e.coords[i] + e.dimensions[i] - coords[i] - dimensions[i];
            }
            else if (coords[i] + dimensions[i] > e.coords[i] + e.dimensions[i])
            {
                deltas[i] = coords[i] - e.coords[i];
            }
        }
        double expanded = 1.0f;
        for (int i = 0; i < dimensions.length; i++)
        {
            expanded *= dimensions[i] + deltas[i];
        }
        return (expanded - area);
    }

    private double getArea(double[] dimensions)
    {
        double area = 1.0f;
        for (int i = 0; i < dimensions.length; i++)
        {
            area *= dimensions[i];
        }
        return area;
    }

    private boolean isOverlap(double[] scoords, double[] sdimensions,
                              double[] coords, double[] dimensions)
    {
        final double FUDGE_FACTOR=1.001f;
        for (int i = 0; i < scoords.length; i++)
        {
            boolean overlapInThisDimension = false;
            if (scoords[i] == coords[i])
            {
                overlapInThisDimension = true;
            }
            else if (scoords[i] < coords[i])
            {
                if (scoords[i] + FUDGE_FACTOR*sdimensions[i] >= coords[i])
                {
                    overlapInThisDimension = true;
                }
            }
            else if (scoords[i] > coords[i])
            {
                if (coords[i] + FUDGE_FACTOR*dimensions[i] >= scoords[i])
                {
                    overlapInThisDimension = true;
                }
            }
            if (!overlapInThisDimension)
            {
                return false;
            }
        }
        return true;
    }

    private class Entry extends Node
    {
        final T entry;

        public Entry(double[] coords, double[] dimensions, T entry)
        {
            // an entry isn't actually a leaf (its parent is a leaf)
            // but all the algorithms should stop at the first leaf they encounter,
            // so this little hack shouldn't be a problem.
            super(coords, dimensions, true);
            this.entry = entry;
        }

        public String toString()
        {
            return "Entry: " + entry;
        }
    }

    // The methods below this point can be used to create an HTML rendering
    // of the RTree.  Maybe useful for debugging?

    private static final int elemWidth = 150;
    private static final int elemHeight = 120;

    void visualize()
    {
        int ubDepth = (int)Math.ceil(Math.log(size)/Math.log(minEntries)) * elemHeight;
        int ubWidth = size * elemWidth;
        java.io.StringWriter sw = new java.io.StringWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter(sw);
        visualize(root, root.coords[0], root.coords[1], ubWidth, ubDepth);
        pw.flush();
    }


    private void visualize(Node n, double x0, double y0, int w, int h)
    {
        boolean q = false;

        if(!n.leaf){

            double[] a = n.dimensions;
            if(a[0] != 0.0 && a[1] != 0.0){

                double dy1 = y0 + a[1];
                double dx1 = x0 + a[0];

                double dis1 =  PointsDistance.distFrom(x0,y0,dx1,dy1);
                double dis2 = dis1/Math.sqrt(2);



                boolean area = dis2 < 100;

                if(area){
                    q = true;
                    counter(n);
                    if(list.size() > 2) {
                        lists.add(list);
                        list = new ArrayList<>();
                    }
                }
            }
        }
        if(!q) {
            int numChildren = (n.children == null) ? 0 : n.children.size();
            for (int i = 0; i < numChildren; i++) {
                visualize(n.children.get(i), n.children.get(i).coords[0],
                        n.children.get(i).coords[1], (int) (w / (double) numChildren), h - elemHeight);
            }
        }
    }

    public void counter(Node n){
        int numChildren = (n.children == null) ? 0 : n.children.size();
        for (int i = 0; i < numChildren; i++) {

            if(n.leaf){
                list.add(n);
            }else{
                counter(n.children.get(i));
            }
        }
    }

    public List<List<Node>> getLists(){
        return lists;
    }

    public void clearLists(){
        lists = new ArrayList<>();
    }


}