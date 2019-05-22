/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    /** The max image depth level. */
    public static final int MAX_DEPTH = 7;

    //Depth ddp is decreasing while depth increases
    public static final double[] DEPTH_LON_DPP = new double[] {
            0.00034332275390625, 0.000171661376953125,
            8.58306884765625e-05, 4.291534423828125e-05,
            2.1457672119140625e-05, 1.0728836059570312e-05,
            5.364418029785156e-06, 2.682209014892578e-06};
/*  How the DPP calculated
def main():
	worldUlLon = -122.2998046875
	worldLrLon = -122.2119140625

    for i in range(8):
        ulLon = worldUlLon
        lrLon = worldUlLon + (worldUlLon - worldLrLon) / 2**i
		#print("d = {:d}:\nlon: {:f}-{:f}.\nlonDPP = {:f}".format(i, ulLon, lrLon, (ulLon - lrLon)/256))

        print(repr((ulLon - lrLon)/256))*/


    /**
     * Takes a user query and finds the grid of images that best matches the query. These images
     * will be combined into one big image (rastered) by the front end. The grid of images must obey
     * the following properties, where image in the grid is referred to as a "tile".
     * <ul>
     *     <li>The tiles collected must cover the most longitudinal distance per pixel (LonDPP)
     *     possible, while still covering less than or equal to the amount of longitudinal distance
     *     per pixel in the query box for the user viewport size.</li>
     *     <li>Contains all tiles that intersect the query bounding box that fulfill the above
     *     condition.</li>
     *     <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     * @param params The RasterRequestParams containing coordinates of the query box and the browser
     *               viewport width and height.
     * @return A valid RasterResultParams containing the computed results.
     */
    public RasterResultParams getMapRaster(RasterRequestParams params) {
        /*System.out.println(
                "Since you haven't implemented getMapRaster, nothing is displayed in the browser.");*/
        System.out.println("params = [" + params + "]");

        /* TODO: Make sure you can explain every part of the task before you begin.
         * Hint: Define additional classes to make it easier to pass around multiple values, and
         * define additional methods to make it easier to test and reason about code. */

        //if out of the border of the map
/*
        if (params.ullon < MapServer.ROOT_ULLON || params.lrlon > MapServer.ROOT_LRLON
                || params.ullat > MapServer.ROOT_ULLAT || params.lrlat < MapServer.ROOT_LRLAT) {
            return RasterResultParams.queryFailed();
        }
*/
        //
        if (params.ullat < params.lrlat || params.ullon > params.lrlon) {
            return RasterResultParams.queryFailed();
        }



        int depth = calculateDepth(params.lrlon, params.ullon, params.w);
        //System.out.println(depth);
        int numGrid1D = (int) Math.pow(2, depth);

        double gridWDiff = MapServer.ROOT_LON_DELTA / numGrid1D;
        double gridHDiff = MapServer.ROOT_LAT_DELTA / numGrid1D;

        double startLon = findStartLon(params.ullon, gridWDiff);
        double startLat = findStartLat(params.ullat, gridHDiff);
        //System.out.println(startLon + "\n" + startLat);

        int startLonIndex = (int) Math.round((startLon - MapServer.ROOT_ULLON)/gridWDiff);
        int startLatIndex = (int) Math.round((MapServer.ROOT_ULLAT - startLat)/gridHDiff);

        double rasterLrLon = findEndLon(params.lrlon, gridWDiff);
        double rasterLrLat = findEndLat(params.lrlat, gridHDiff);

        int numXImages = (int) Math.round((rasterLrLon - startLon) / gridWDiff);
        int numYImages = (int) Math.round((startLat - rasterLrLat) / gridHDiff);

        String[][] renderGrid = new String[numYImages][numXImages];

        for (int x = 0; x < numXImages; x++) {
            for (int y = 0; y < numYImages; y++) {
                renderGrid[y][x] = String.format("d%d_x%d_y%d.png", depth,startLonIndex + x, startLatIndex + y);
            }// remove the a after .pnga to compile
        }
        return new RasterResultParams(renderGrid, startLon,startLat, rasterLrLon, rasterLrLat, depth, true);


        //return RasterResultParams.queryFailed();
    }

    /**
     * Calculates the lonDPP of an image or query box
     * @param lrlon Lower right longitudinal value of the image or query box
     * @param ullon Upper left longitudinal value of the image or query box
     * @param width Width of the query box or image
     * @return lonDPP
     */
    private double lonDPP(double lrlon, double ullon, double width) {
        return (lrlon - ullon) / width;
    }

    /** return the k of dk that will be used */
    private int calculateDepth(double lrlon, double ullon, double width) {
        double reqLonDDP = lonDPP(lrlon, ullon, width);
        for (int i = 0; i < MAX_DEPTH; i++) {
            if (reqLonDDP > DEPTH_LON_DPP[i]) {
                return i;
            }
        }
        return MAX_DEPTH;
    }

    private double findStartLon(double startingLon, double gridWDiff) {
        return (startingLon - (startingLon - MapServer.ROOT_ULLON) % gridWDiff);
    }

    private double findStartLat(double startingLat, double gridHDiff) {
        return (startingLat + (MapServer.ROOT_ULLAT - startingLat) % gridHDiff);
    }

    private double findEndLon(double endingLon, double gridDiff) {
        //TODO : IDK WHY THE FIRST VERSION DOESN'T WORK
        //return findStartLon(endingLon, gridDiff) + gridDiff;
        return endingLon + (MapServer.ROOT_LRLON - endingLon) % gridDiff;
    }

    private double findEndLat(double endingLat, double gridDiff) {
        return findStartLat(endingLat, gridDiff) - gridDiff;
    }

    private <T> void print2DArray(T[][] arr) {
        //for
    }

}
