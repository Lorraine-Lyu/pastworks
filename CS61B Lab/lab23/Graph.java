
import java.util.LinkedList;
import java.util.HashSet;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class Graph implements Iterable<Integer> {

    private LinkedList<Edge>[] adjLists;
    private int vertexCount;

    /* Initializes a graph with NUMVERTICES vertices and no Edges. */
    public Graph(int numVertices) {
        adjLists = (LinkedList<Edge>[]) new LinkedList[numVertices];
        for (int k = 0; k < numVertices; k++) {
            adjLists[k] = new LinkedList<Edge>();
        }
        vertexCount = numVertices;
    }

    /* Adds a directed Edge (V1, V2) to the graph. */
    public void addEdge(int v1, int v2) {
        addEdge(v1, v2, 0);
    }

    /* Adds an undirected Edge (V1, V2) to the graph. */
    public void addUndirectedEdge(int v1, int v2) {
        addUndirectedEdge(v1, v2, 0);
    }

    /* Adds a directed Edge (V1, V2) to the graph with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addEdge(int v1, int v2, int weight) {
        if (v1 >= vertexCount || v2 >= vertexCount) {
            return;
        }
        Edge e = new Edge(v1, v2, weight);
        if (adjLists[v1].size() == 0) {
            adjLists[v1].add(e);
        } else {
            for (int i = 0; i < adjLists[v1].size(); i++) {
                if (adjLists[v1].get(i).to == v2) {
                    adjLists[v1].set(i, e);
                    return;
                }
            }
            adjLists[v1].addLast(e);
        }
    }

    /* Adds an undirected Edge (V1, V2) to the graph with weight WEIGHT. If the
       Edge already exists, replaces the current Edge with a new Edge with
       weight WEIGHT. */
    public void addUndirectedEdge(int v1, int v2, int weight) {
        addEdge(v1, v2, weight);
        addEdge(v2, v1, weight);
    }
    /* Returns true if there exists an Edge from vertex FROM to vertex TO.
       Returns false otherwise. */
    public boolean isAdjacent(int from, int to) {
        if (from >= vertexCount || to >= vertexCount) {
            throw new IllegalArgumentException();
        }
        if (adjLists[from].size() == 0) {
            return false;
        } else {
            for (Edge e : adjLists[from]) {
                if (e.to == to) {
                    return true;
                }
            }
        }
        return false;
    }

    /* Returns a list of all the vertices u such that the Edge (V, u)
       exists in the graph. */
    public List<Integer> neighbors(int v) {
        if (v >= vertexCount) {
            throw new IllegalArgumentException();
        }
        List<Integer> lst = new ArrayList<>();
        if (adjLists[v].size() == 0) {
            return lst;
        } else {
            for (Edge e: adjLists[v]) {
                lst.add(e.to);
            }
        }
        return lst;
    }
    /* Returns the number of incoming Edges for vertex V. */
    public int inDegree(int v) {
        if (v >= vertexCount) {
            throw new IllegalArgumentException();
        }
        int count = 0;
        for (LinkedList<Edge> lst : adjLists) {
            if (lst.size() == 0) {
                continue;
            }
            for (Edge e : lst) {
                if (e.to == v) {
                    count++;
                }
            }
        }
        return count;
    }

    /* Returns an Iterator that outputs the vertices of the graph in topological
       sorted order. */
    public Iterator<Integer> iterator() {
        return new TopologicalIterator();
    }

    /* A class that iterates through the vertices of this graph, starting with
       vertex START. If the iteration from START has no path from START to some
       vertex v, then the iteration will not include v. */
    private class DFSIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;
        private HashSet<Integer> visited;

        DFSIterator(int start) {
            fringe = new Stack<>();
            visited = new HashSet<>();
            fringe.push(start);
        }

        public boolean hasNext() {
            return !fringe.isEmpty();
        }

        public Integer next() {
            Integer toReturn = fringe.pop();
            visited.add(toReturn);
            for (int i : neighbors(toReturn)) {
                if (!visited.contains(i) && !fringe.contains(i)) {
                    fringe.push(i);
                }
            }
            return toReturn;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    /* Returns the collected result of performing a depth-first search on this
       graph's vertices starting from V. */
    public List<Integer> dfs(int v) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new DFSIterator(v);

        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    /* Returns true iff there exists a path from START to STOP. Assumes both
       START and STOP are in this graph. If START == STOP, returns true. */
    public boolean pathExists(int start, int stop) {
        List<Integer> res = dfs(start);
        if (res.contains(stop)) {
            return true;
        }
        return false;
    }


    /* Returns the path from START to STOP. If no path exists, returns an empty
       List. If START == STOP, returns a List with START. */
    public List<Integer> path(int start, int stop) {
        ArrayList<Integer> result = new ArrayList<>();
        Iterator<Integer> iter = new DFSIterator(start);
        if (!pathExists(start, stop)) {
            return new ArrayList<>();
        }
        if (start == stop) {
            result.add(start);
            return result;
        }
        while (iter.hasNext()) {
            Integer next = iter.next();
            if (next == stop) {
                break;
            }
            result.add(next);
        }
        ArrayList<Integer> shortcut = new ArrayList<>();
        shortcut.add(stop);
        while (result.size() > 0) {
            for (int i = 0; i < result.size(); i++) {
                if (isAdjacent(result.get(i), shortcut.get(0))) {
                    shortcut.add(0, result.get(i));
                    result = new ArrayList<>(result.subList(0, i));
                    break;
                }
            }
        }
        return shortcut;
    }

    public List<Integer> topologicalSort() {
        ArrayList<Integer> result = new ArrayList<Integer>();
        Iterator<Integer> iter = new TopologicalIterator();
        while (iter.hasNext()) {
            result.add(iter.next());
        }
        return result;
    }

    private class TopologicalIterator implements Iterator<Integer> {

        private Stack<Integer> fringe;


        TopologicalIterator() {
            fringe = new Stack<Integer>();
        }

        public boolean hasNext() {
            return false;
        }

        public Integer next() {
            return 0;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private class Edge {

        private int from;
        private int to;
        private int weight;

        Edge(int from, int to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        public String toString() {
            return "(" + from + ", " + to + ", weight = " + weight + ")";
        }

    }

    private void generateG1() {
        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 4);
        addEdge(1, 2);
        addEdge(2, 0);
        addEdge(2, 3);
        addEdge(4, 3);
    }

    private void generateG2() {
        addEdge(0, 1);
        addEdge(0, 2);
        addEdge(0, 4);
        addEdge(1, 2);
        addEdge(2, 3);
        addEdge(4, 3);
    }

    private void generateG3() {
        addUndirectedEdge(0, 2);
        addUndirectedEdge(0, 3);
        addUndirectedEdge(1, 4);
        addUndirectedEdge(1, 5);
        addUndirectedEdge(2, 3);
        addUndirectedEdge(2, 6);
        addUndirectedEdge(4, 5);
    }

    private void generateG4() {
        addEdge(0, 1);
        addEdge(1, 2);
        addEdge(2, 0);
        addEdge(2, 3);
        addEdge(4, 2);
    }

    private void printDFS(int start) {
        System.out.println("DFS traversal starting at " + start);
        List<Integer> result = dfs(start);
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void printPath(int start, int end) {
        System.out.println("Path from " + start + " to " + end);
        List<Integer> result = path(start, end);
        if (result.size() == 0) {
            System.out.println("No path from " + start + " to " + end);
            return;
        }
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
        System.out.println();
        System.out.println();
    }

    private void printTopologicalSort() {
        System.out.println("Topological sort");
        List<Integer> result = topologicalSort();
        Iterator<Integer> iter = result.iterator();
        while (iter.hasNext()) {
            System.out.println(iter.next() + " ");
        }
    }

    public static void main(String[] args) {
        Graph g1 = new Graph(5);
        g1.generateG4();
        g1.printDFS(0);
        g1.printDFS(2);
        g1.printDFS(3);
        g1.printDFS(4);
//        g1.printDFS(5);
//        g1.printDFS(6);

        g1.printPath(0, 3);
        g1.printPath(0, 4);
        g1.printPath(1, 3);
        g1.printPath(1, 4);
        g1.printPath(4, 0);

        Graph g2 = new Graph(5);
        g2.generateG2();
        g2.printTopologicalSort();
    }
}
