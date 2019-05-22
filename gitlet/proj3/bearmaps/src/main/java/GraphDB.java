import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.*;

/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Kevin Lowe, Antares Chen, Kevin Lin
 */
public class GraphDB {
    final Map<String, Node> nodes = new HashMap<>();
    double x = 0; /** the x value of the position for closest() */
    double y = 0; /** the y value of the position for closest() */
    /**
     * This constructor creates and starts an XML parser, cleans the nodes, and prepares the
     * data structures for processing. Modify this constructor to initialize your data structures.
     * @param dbPath Path to the XML file to be parsed.
     */
    public GraphDB(String dbPath) {
        File inputFile = new File(dbPath);
        try (FileInputStream inputStream = new FileInputStream(inputFile)) {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(inputStream, new GraphBuildingHandler(this));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     * Remove nodes with no connections from the graph.
     * While this does not guarantee that any two nodes in the remaining graph are connected,
     * we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        nodes.values().removeIf((Node o) -> (o.to.size() == 0));
        //nodes.keySet().removeIf();
        /*for (Iterator<String> i = nodes.keySet().iterator(); i.hasNext();) {
            Integer element = i.next();
            if (element % 2 == 0) {
                i.remove();
            }
        }*/
    }

    /**
     * Returns the longitude of vertex <code>v</code>.
     * @param v The ID of a vertex in the graph.
     * @return The longitude of that vertex, or 0.0 if the vertex is not in the graph.
     */
    double lon(long v) {
        return nodes.get(String.format("%d", v)).lon;
    }

    /**
     * Returns the latitude of vertex <code>v</code>.
     * @param v The ID of a vertex in the graph.
     * @return The latitude of that vertex, or 0.0 if the vertex is not in the graph.
     */
    double lat(long v) {
        return nodes.get(String.format("%d", v)).lat;
    }

    /**
     * Returns an iterable of all vertex IDs in the graph.
     * @return An iterable of all vertex IDs in the graph.
     */
    Iterable<Long> vertices() {
        List<Long> vertices = new ArrayList<>();
        for (String s: nodes.keySet()) {
            vertices.add(Long.parseLong(s));
        }
        return vertices;
    }

    /**
     * Returns an iterable over the IDs of all vertices adjacent to <code>v</code>.
     * @param v The ID for any vertex in the graph.
     * @return An iterable over the IDs of all vertices adjacent to <code>v</code>, or an empty
     * iterable if the vertex is not in the graph.
     */
    Iterable<Long> adjacent(long v) {
        List<Long> adjacent = new ArrayList<>();
        for (String s: nodes.get(String.format("%d", v)).to) {
            adjacent.add(Long.parseLong(s));
        }
        return adjacent;
    }

    /**
     * Returns the great-circle distance between two vertices, v and w, in miles.
     * Assumes the lon/lat methods are implemented properly.
     * @param v The ID for the first vertex.
     * @param w The ID for the second vertex.
     * @return The great-circle distance between vertices and w.
     * @source https://www.movable-type.co.uk/scripts/latlong.html
     */
    public double distance(long v, long w) {
        double phi1 = Math.toRadians(lat(v));
        double phi2 = Math.toRadians(lat(w));
        double dphi = Math.toRadians(lat(w) - lat(v));
        double dlambda = Math.toRadians(lon(w) - lon(v));

        double a = Math.sin(dphi / 2.0) * Math.sin(dphi / 2.0);
        a += Math.cos(phi1) * Math.cos(phi2) * Math.sin(dlambda / 2.0) * Math.sin(dlambda / 2.0);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Returns the ID of the vertex closest to the given longitude and latitude.
     * @param lon The given longitude.
     * @param lat The given latitude.
     * @return The ID for the vertex closest to the <code>lon</code> and <code>lat</code>.
     */
    public long closest(double lon, double lat) {
        KDTree.TreeNode route = new KDTree(this).root;
        String champion = route.id;
        champion = find_closest(route, champion, true);
        x = projectToX(lon, lat);
        y = projectToY(lon, lat);
        return Long.parseLong(champion);
    }

/*    public String getClosest(KDTree.TreeNode node, String champ, boolean horizontal) {
        Node top = nodes.get(node.id);
        Node champion = nodes.get(champ);
        double closestSoFar =  euclidean(projectToX(champion.lon, champion.lat), x, projectToY(champion.lon, champion.lat), y); //the smallest distance recorded so far
        double potentialClosest = euclidean(projectToX(top.lon, top.lat), x, projectToY(top.lon, top.lat), y); //the distance of current top node to our start point
        if (potentialClosest < closestSoFar) {
            champ = top.id;
        }
    }*/

    /** the helper method for closest
     * champ is the current closest Node to (lon, lat)
     * horizontal marks whether this time we should check on horizontal value or vertical
     * we first compare if the current KDTreeNode is closer to (lon, lat), if so we update the champ
     * then we check if (lon, lat) is to the left/bottom of champ, if so, we go left/down first and get the new champ
     * then we compare the (lon, lat)'s distance to champ and (lon, lat)'s distance to the x/y axis that KDTree.Node is on
     * if the distance to current Node axis is greater, we don't have to go to the other branch of node*/

    private String find_closest(KDTree.TreeNode node, String champ, boolean horizontal) {
            Node top = nodes.get(node.id);
            Node champion = nodes.get(champ);
            double oldDistance =  euclidean(projectToX(champion.lon, champion.lat), x, projectToY(champion.lon, champion.lat), y);
            double newDistance = euclidean(projectToX(top.lon, top.lat), x, projectToY(top.lon, top.lat), y);
            if (newDistance < oldDistance) {
                champ = top.id;
            }
            if (horizontal) {
                if (x <= projectToX(top.lon, top.lat)) {
                    if (node.left != null) {
                        champ = find_closest(node.left, champ, !horizontal);
                    }
                    Node n = nodes.get(champ);
                    double checkDistance = euclidean(projectToX(n.lon, n.lat), x, projectToY(n.lon, n.lat), y);
                    double horizontalDis = projectToX(top.lon, top.lat) - x;
                    if (checkDistance < horizontalDis) {
                        return champ;
                    }
                    if (node.right != null) {
                        champ = find_closest(node.right, champ, !horizontal);
                    }
                } else {
                    if (node.right != null) {
                        champ = find_closest(node.right, champ, !horizontal);
                    }
                    Node n = nodes.get(champ);
                    double checkDistance = euclidean(projectToX(n.lon, n.lat), x, projectToY(n.lon, n.lat), y);
                    double horizontalDis = projectToX(top.lon, top.lat) - x;
                    if (checkDistance < horizontalDis) {
                        return champ;
                    }
                    if (node.left != null) {
                        champ = find_closest(node.left, champ, !horizontal);
                    }
                }
            } else {
                if (y <= projectToY(top.lon, top.lat)) {
                    if (node.left != null) {
                        champ = find_closest(node.left, champ, !horizontal);
                    }
                    Node n = nodes.get(champ);
                    double checkDistance = euclidean(projectToX(n.lon, n.lat), x, projectToY(n.lon, n.lat), y);
                    double verticalDis = projectToX(top.lon, top.lat) - x;
                    if (checkDistance < verticalDis) {
                        return champ;
                    }
                    if (node.right != null) {
                        champ = find_closest(node.right, champ, !horizontal);
                    }
                } else {
                    if (node.left != null) {
                        champ = find_closest(node.right, champ, !horizontal);
                    }
                    Node n = nodes.get(champ);
                    double checkDistance = euclidean(projectToX(n.lon, n.lat), x, projectToY(n.lon, n.lat), y);
                    double verticalDis = projectToX(top.lon, top.lat) - x;
                    if (checkDistance < verticalDis) {
                        return champ;
                    }
                    if (node.left != null) {
                        champ = find_closest(node.left, champ, !horizontal);
                    }
                }
            }

        return champ;
    }


    static double euclidean(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }
    /**
     * Return the Euclidean x-value for some point, p, in Berkeley. Found by computing the
     * Transverse Mercator projection centered at Berkeley.
     * @param lon The longitude for p.
     * @param lat The latitude for p.
     * @return The flattened, Euclidean x-value for p.
     * @source https://en.wikipedia.org/wiki/Transverse_Mercator_projection
     */
    static double projectToX(double lon, double lat) {
        double dlon = Math.toRadians(lon - ROOT_LON);
        double phi = Math.toRadians(lat);
        double b = Math.sin(dlon) * Math.cos(phi);
        return (K0 / 2) * Math.log((1 + b) / (1 - b));
    }

    /**
     * Return the Euclidean y-value for some point, p, in Berkeley. Found by computing the
     * Transverse Mercator projection centered at Berkeley.
     * @param lon The longitude for p.
     * @param lat The latitude for p.
     * @return The flattened, Euclidean y-value for p.
     * @source https://en.wikipedia.org/wiki/Transverse_Mercator_projection
     */
    static double projectToY(double lon, double lat) {
        double dlon = Math.toRadians(lon - ROOT_LON);
        double phi = Math.toRadians(lat);
        double con = Math.atan(Math.tan(phi) / Math.cos(dlon));
        return K0 * (con - Math.toRadians(ROOT_LAT));
    }

    /**
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        return Collections.emptyList();
    }

    /**
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A <code>List</code> of <code>LocationParams</code> whose cleaned name matches the
     * cleaned <code>locationName</code>
     */
    public List<LocationParams> getLocations(String locationName) {
        return Collections.emptyList();
    }

    /**
     * Returns the initial bearing between vertices <code>v</code> and <code>w</code> in degrees.
     * The initial bearing is the angle that, if followed in a straight line along a great-circle
     * arc from the starting point, would take you to the end point.
     * Assumes the lon/lat methods are implemented properly.
     * @param v The ID for the first vertex.
     * @param w The ID for the second vertex.
     * @return The bearing between <code>v</code> and <code>w</code> in degrees.
     * @source https://www.movable-type.co.uk/scripts/latlong.html
     */
    double bearing(long v, long w) {
        double phi1 = Math.toRadians(lat(v));
        double phi2 = Math.toRadians(lat(w));
        double lambda1 = Math.toRadians(lon(v));
        double lambda2 = Math.toRadians(lon(w));

        double y = Math.sin(lambda2 - lambda1) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2);
        x -= Math.sin(phi1) * Math.cos(phi2) * Math.cos(lambda2 - lambda1);
        return Math.toDegrees(Math.atan2(y, x));
    }

    static class Node {
        String id;
        double lon;
        double lat;
        String name;
        HashSet<String> to;

        public Node (String id, double lon, double lat) {
            this.id = id;
            this.lon = lon;
            this.lat = lat;
            this.to = new HashSet<>();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return Objects.equals(id, node.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }



    private class KDTree {
        TreeNode root;

        KDTree (GraphDB g) {
            List<GraphDB.Node> queue = new ArrayList<>(g.nodes.values());
            queue.sort((o1, o2) -> Double.compare(projectToX(o1.lon, o1.lat), projectToX(o2.lon, o2.lat)));
            int median = queue.size() / 2;
            root = new TreeNode(queue.get(median).id, Helper(queue.subList(0, median), false), Helper(queue.subList(median + 1, queue.size()), false));
        }

        private TreeNode Helper(List<GraphDB.Node> lst, boolean horizontal) {
            if (lst.size() == 0) {
                return null;
            } else if (lst.size() == 1) {
                return new TreeNode(lst.get(0).id, null, null);
            }
            if (horizontal) {
                lst.sort(((o1, o2) -> Double.compare(projectToX(o1.lon, o1.lat), projectToX(o2.lon, o2.lat))));
                horizontal = false;
            } else {
                lst.sort(((o1, o2) -> Double.compare(projectToY(o1.lon, o1.lat), projectToY(o2.lon, o2.lat))));
                horizontal = true;
            }

            int median = lst.size() / 2;
            TreeNode toReturn = new TreeNode(lst.get(median).id, Helper(lst.subList(0, median), horizontal),
                    Helper(lst.subList(median + 1, lst.size()), horizontal));
            return toReturn;
        }


        class TreeNode {
            String id;
            TreeNode left;
            TreeNode right;

            TreeNode (String s, TreeNode left, TreeNode right) {
                this.id = s;
                this.left = left;
                this.right = right;
            }


            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                TreeNode node = (TreeNode) o;
                return Objects.equals(id, node.id);
            }

            @Override
            public int hashCode() {
                return Objects.hash(id);
            }
        }
    }

    /** Radius of the Earth in miles. */
    private static final int R = 3963;
    /** Latitude centered on Berkeley. */
    private static final double ROOT_LAT = (MapServer.ROOT_ULLAT + MapServer.ROOT_LRLAT) / 2;
    /** Longitude centered on Berkeley. */
    private static final double ROOT_LON = (MapServer.ROOT_ULLON + MapServer.ROOT_LRLON) / 2;
    /**
     * Scale factor at the natural origin, Berkeley. Prefer to use 1 instead of 0.9996 as in UTM.
     * @source https://gis.stackexchange.com/a/7298
     */
    private static final double K0 = 1.0;
}
