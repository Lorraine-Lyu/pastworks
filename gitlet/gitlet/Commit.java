package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * The name of the commit parent, the name of the tracked files,
 * the commit message and the date will be passed in the constructor.
 */
public class Commit implements Serializable {
    /**
     * The SHA-1 identifier of my parent, or null if the initial commit.
     */
    private final List<String> parents;

    /**
     * My log message.
     */
    private final String commitMessage;

    /**
     * My timestamp. (java.util.Date)
     */
    private final Date commitDate;

    /**
     * The names of the tracked files
     */
    private final LinkedHashMap<String, String> trackedFilesNameToCommitSha;

    private final String sha1;

    ///** A mapping of file names to the SHA-1's of their blobs. */
    //private HashMap<String, String> contents = new HashMap<>();

    /**
     * default constructor; for inital commit only
     */
    public Commit() {
        this(new ArrayList<String>(), "initial commit",
                new LinkedHashMap<String, String>(16, 0.75f, true),
                new String[0], new ArrayList<String>());
    }


    /**
     * constructor for other commits
     */
    public Commit(List<String> parents, String commitMessage, LinkedHashMap<String,
            String> trackedFilesNameToCommitSha, String[] addedFiles,
                  ArrayList<String> removedFiles) {
        this.parents = parents;
        this.commitMessage = commitMessage;
        this.commitDate = new Date();
        this.trackedFilesNameToCommitSha = trackedFilesNameToCommitSha;
        //getTIme would return the milliseconds from 1970 til now

        //so the updated tracking list can be used for making the sha
        //will be updated from HEAD to real sha1 after generating the sha1
        for (String filename : addedFiles) {
            trackedFilesNameToCommitSha.put(filename, "HEAD");
        }

        for (String filename : removedFiles) {
            trackedFilesNameToCommitSha.remove(filename + ".ser");
        }


        //System.out.println(parents.getClass());
        Object[] shaSource = new Object[2 + parents.size() + trackedFilesNameToCommitSha.size()];
        //System.out.println(shaSource.getClass());
        System.arraycopy(parents.toArray(), 0, shaSource, 0, parents.size());
        System.arraycopy(trackedFilesNameToCommitSha.keySet()
                        .toArray(new String[trackedFilesNameToCommitSha.size()]),
                0, shaSource, parents.size(),
                trackedFilesNameToCommitSha.size());
        shaSource[trackedFilesNameToCommitSha.size() + parents.size()] = commitMessage;
        shaSource[trackedFilesNameToCommitSha.size() + parents.size() + 1] = commitDate.toString();

        sha1 = Utils.sha1(shaSource);

        for (String file : addedFiles) {
            trackedFilesNameToCommitSha.put(file, sha1);
        }
    }


//
//    /** append constructor for other commits; the parent will be appended as new List
//     * appendFiles is set to be true if the user wish to append trackedFilesNameToCommitSha too;
//     * else it should be set to false.
//     * */
//    public Commit(List<String> parents, String commitMessage, Date commitDate,
// List<String> trackedFilesNameToCommitSha, boolean appendFiles) {
//
//    }


    /**
     * return a lsit of parents of the current commit object
     */
    public List<String> getParents() {
        return new ArrayList<>(parents);
    }

    public List<String> getUpdatedParents() {
        List newList = new ArrayList<String>(parents);
        newList.add(sha1);
        return newList;
    }

    /**
     * return the tracked Files
     */
    public LinkedHashMap<String, String> getTrackedFilesNameToCommitSha() {
        return new LinkedHashMap<String, String>(trackedFilesNameToCommitSha);
    }


    /**
     * return the commit message of the commit
     */
    public String getCommitMessage() {
        return commitMessage;
    }

    /**
     * return the commit date
     */
    public String getCommitDate() {
        SimpleDateFormat formatter = new SimpleDateFormat(("yyyy-MM-dd HH:mm:ss"));
        String format = formatter.format(commitDate);
        return format;
    }


    /**
     * add A list of tracking files and return as a list
     */
    private List<String> addTracking(File... files) {
        return null;
    }

    /**
     * return the sha1 of the current commit
     */
    public String getSha() {
        return sha1;
    }

    /**
     * concatenate two List as one
     */
    public static <T> List<T> concatList(List<T> l1, List<T> l2) {
        return null;
    }

    public void updateTrackingToSha(String filename) {
        trackedFilesNameToCommitSha.put(filename, this.getSha());
    }

    public void removeDeletedFile(String filename) {
        trackedFilesNameToCommitSha.remove(filename);
    }


}
