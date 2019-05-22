package gitlet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Blob Class
 * <p>
 * The Blob class is used to represent the content of a file,
 * together with some metadata describing the file
 * e.g. path, sha-1. The instances of Blob is also made to be immutable,
 * as it would prevent any modification after creation.
 * Instead of modifying, a new instance of blob will be created to ensure data
 * consistency (a mismatch of sha-1 and file may happen).
 * <p>
 * The class also implements the Serializable interface
 * so it can be stored and restored as an object.
 * <p>
 * Serialization converts an object into static stream of bytes so it persists.
 * Serializable interface is a marker interface (no methods).
 * Serialization saves an object's state only. No object class file or object methods.
 **/

public class Blob implements Serializable {
    //  SerialVersionUID?

    /**
     * pathToFile + Filename
     */
    private final String pathName;
    /**
     * sha1 of the object it contains
     */
    private final String sha1;
    /**
     * Content of the file
     */
    private final byte[] content;

    /**
     * a constructor that pass the fileName
     */
    public Blob(String pathName, byte[] content) {
/*        Object[] shaSource = new Object[content.length + 1];
        shaSource[0] = pathName;
        for(int i = 0; i < content.length; i++) {
            shaSource[i+1] = new Byte content[i];
        }*/
        //System.arraycopy(Bytes.asList, 0, shaSource, 1, content.length);
        this.pathName = pathName;
        this.sha1 = Utils.sha1(content);
        this.content = content;
    }

    public static String getFileName(String pathName) {
        String[] paths = pathName.split("/");
        return paths[paths.length - 1];
    }

    /**
     * The equals() method compares two blobs based on its sha1 and pathName
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Blob blob = (Blob) o;
        return Objects.equals(pathName, blob.pathName)
                && Objects.equals(sha1, blob.sha1);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pathName, sha1);
    }

    /**
     * return the pathName of the Blob.
     */
    public String getPathName() {
        return pathName;
    }

    /**
     * return the sha1 of the current Blob
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * get the content of the current Blob
     */
    public byte[] getContent() {
        return content;
    }

    public static boolean areShasEqual(Blob b1, Blob b2) {
        return b1.sha1 == b2.sha1;
    }

    /**
     * read all the blobs in the the directory and return it as a List
     */
    protected static List<Blob> readBlobsDir(String path) {
        // interacting with Commit Class which will give the pathName
        // Returns a list of the names of all plain files in the directory DIR, in
        //   lexicographic order as Java Strings. Returns null if DIR does not denote
        //   a directory.
        List stringNames = Utils.plainFilenamesIn(path);
        // Creating a new ArrayList of all the blobs
        List<Blob> listOfBlobs = new ArrayList<Blob>();
        // Iterate through blob in path
//        for (string : stringNames) {
//            // Read a blob
//            Utils.readContents(string);
//            // Make a new blob to avoid collision
//            Blob newBlob = new Blob(string);
//            // Add the newBlob to listOfBlobs
//            listOfBlobs.add(newBlob);
//        }
        return listOfBlobs;
    }

    /**
     * return the status of the Blob
     */
    /* List of files that are tracked are available */
    protected static int checkStatus(Blob file) {
        // java.Gitlet.main.checkStatus
        // if in ./staging, then gitrm + git add means removed
        // if in .staging and working == ./staging then tracked
        // if in ./staging and working != ./staging then modified but not added
        // if not in ./staging then working != parent's means modified or deleted
        // if not in ./staging and not in parent's means untracked
        return 0;
    }
}
