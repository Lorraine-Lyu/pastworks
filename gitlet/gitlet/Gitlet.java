package gitlet;

//import java.nio.file.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
//import java.util.*;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.Collections;
import java.io.Serializable;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileNotFoundException;
import java.io.InterruptedIOException;

import static gitlet.Utils.readContents;


public class Gitlet {
    /** create new gitlet in current directory */

    /**
     * A LinkedHashMap from sha1 to pathToCommit
     */
    LinkedHashMap<String, String> shaToPath;
    /**
     * A LinkedHashMap from message to a list of sha1
     */
    LinkedHashMap<String, List<String>> msgToShas;
    /**
     * A LinkedHashMap from branch to sha1
     */
    LinkedHashMap<String, String> branchToSha;
    /**
     * A list of removed files
     */
    ArrayList<String> removedFiles;
    /**
     * current branch name
     */
    String branch;
    //this should be changed to .gitlet later;
    static final String HOMEDIR = ".gitlet";

    static final String STAGING = HOMEDIR + "/staging";

    static final String GITDATA = HOMEDIR + "/gitData";

    static final String COMMIT = HOMEDIR + "/commits";

/*    private class KeyListLinkedHashMap
<K, V, T extends List<? extends V>> extends LinkedHashMap<K, V> {
        @Override
        public V put(K key, V value) {
            if (containsKey(key)) {
                get(key).add(value);
            } else {
                super(key, new ArrayList<T>());
                get(key).add(value);
            }
        }
    }*/


    protected Gitlet() {
        shaToPath = new LinkedHashMap<String, String>();
        msgToShas = new LinkedHashMap<String, List<String>>();
        removedFiles = new ArrayList<String>();
        branchToSha = new LinkedHashMap<String, String>();
        branch = "master";
    }

    protected static String getCommitPath(String shaCommit) {
        return COMMIT + "/" + shaCommit;
    }

    protected Commit getOneCommit(String shaCommit) throws IOException, ClassNotFoundException {
        if (!shaToPath.keySet().contains(shaCommit)) {
            throw new IllegalArgumentException("No commit with that id exists.");
        }
        //System.out.println(readOneObject(getCommitPath(shaCommit)
        // + "/" + shaCommit + ".ser" ).getClass());
        return (Commit) readOneObject(
                getCommitPath(shaCommit) + "/" + shaCommit + ".ser");
    }

    protected static File getBlobFromPathName(String shaCommit, String shaBlob) {
        return new File(COMMIT + "/" + shaCommit + "/" + shaBlob + ".ser");
    }

    /** Creates a new gitlet version-control system in the current directory.
     This system will automatically start with one commit: a commit that contains no
     files and has the commit message initial commit.
     It will have a single branch: master, which initially points to this initial commit,
     and master will be the current branch.*/

    /**
     * If there is already a gitlet version-control system in the current directory,
     * it should abort.
     * It should NOT overwrite the existing system with a new one.
     * Should print the error message A gitlet version-control
     * system already exists in the current directory.
     */
    protected static Gitlet init() throws InstantiationException, IOException {
        //inititalize the file structure
        File homeDir = new File(HOMEDIR);
        if (homeDir.exists()) {
            throw new InstantiationException("A gitlet version-control system "
                    + "already exists in the current directory.");
        }
//      if (!homeDir.exists()) {
        homeDir.mkdir();
//      }
        File gitData = new File(GITDATA);
        gitData.mkdir();
        File staging = new File(STAGING);
        staging.mkdir();
        File commit = new File(COMMIT);
        commit.mkdir();

        //make initial commit
        Gitlet gitlet = new Gitlet();
        //gitlet.commit();
        Commit commitObj = new Commit();

        String sha = commitObj.getSha();
        String message = commitObj.getCommitMessage();

        gitlet.shaToPath.put(sha, COMMIT + "/".concat(sha));
        gitlet.updateTreeList(gitlet.msgToShas, message, sha);
        gitlet.branchToSha.put(gitlet.branch, sha);

        String commitPath = COMMIT + "/" + sha;
        File commitDir = new File(commitPath);
        commitDir.mkdir();
        writeOneObject(commitDir + "/" + sha + ".ser", commitObj);

        return gitlet;
    }


    //write the four instance variable of gitlet from memory to hardisk
    protected void writeAllObjects() throws IOException /*specify the IOException later*/ {
        writeShaToPath();
        writeMsgToShas();
        writeBranchToSha();
        writeBranch();
        writeRemovedFiles();
    }


    protected void readAllObjects() throws IOException, ClassNotFoundException  {
        readShaToPath();
        readMsgToShas();
        readBranchToSha();
        readBranch();
        readRemovedFiles();
    }

    protected static <T extends Serializable> void writeOneObject(String pathName, T object)
            throws IOException {
        try (FileOutputStream file = new FileOutputStream(pathName);
             ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(object);
        }
    }

    protected void writeShaToPath() throws IOException {
        writeOneObject(GITDATA + "/shaToPath.ser", shaToPath);
    }

    protected void writeMsgToShas() throws IOException {
        writeOneObject(GITDATA + "/msgToShas.ser", msgToShas);
    }

    protected void writeBranchToSha() throws IOException {
        writeOneObject(GITDATA + "/branchToSha.ser", branchToSha);
    }

    protected void writeBranch() throws IOException {
        writeOneObject(GITDATA + "/branch.ser", branch);
    }

    protected void writeRemovedFiles() throws IOException {
        writeOneObject(GITDATA + "/removedFiles.ser", removedFiles);

    }

    protected static <T extends Serializable> T readOneObject(String pathName)
            throws IOException, ClassNotFoundException {
        try (FileInputStream file = new FileInputStream(pathName);
             ObjectInputStream in = new ObjectInputStream(file)) {
            return (T) in.readObject();
        }
    }


    protected void readShaToPath() throws IOException, ClassNotFoundException {
        shaToPath = (LinkedHashMap<String, String>) readOneObject(GITDATA + "/shaToPath.ser");
    }

    protected void readMsgToShas() throws IOException, ClassNotFoundException {
        msgToShas = (LinkedHashMap<String, List<String>>) readOneObject(GITDATA + "/msgToShas.ser");
    }

    protected void readBranchToSha() throws IOException, ClassNotFoundException {
        branchToSha = (LinkedHashMap<String, String>) readOneObject(GITDATA + "/branchToSha.ser");
    }

    protected void readBranch() throws IOException, ClassNotFoundException {
        branch = (String) readOneObject(GITDATA + "/branch.ser");
    }

    protected void readRemovedFiles() throws IOException, ClassNotFoundException {
        removedFiles = (ArrayList<String>) readOneObject(GITDATA + "/removedFiles.ser");
    }


    protected String getBranch() {
        return branch;

    }

    protected LinkedHashMap<String, String> getShaToPath() {
        return shaToPath;
    }


    protected LinkedHashMap<String, List<String>> getMsgToShas() {
        return msgToShas;
    }

    protected LinkedHashMap<String, String> getBranchToSha() {
        return branchToSha;
    }


//  /** add file to staging area, throw error when file not exist */
    /*     protected static void add(String filename)
    throws IllegalArgumentException, IOException, ClassNotFoundException {
     */

    /**
     * read file from working dir
     *//*
       String filePath = getCurrWorkDir() + filename;
       FileInputStream fis = new FileInputStream(filePath);
       ObjectInputStream ois = new ObjectInputStream(fis);
       File toBeAdded = (File) ois.readObject();
       // String path = System.getProperty("user.dir") + "/" + filename;
       // create byte array and new blob
       byte[] bOfFile = readContents(toBeAdded);
       Blob b = new Blob(filePath, bOfFile);
       // save blob to staging area, check whether the same file has already been added
       if (isInStaging(b)) {
         FileOutputStream fout = new FileOutputStream(STAGING + "/" + filename + ".ser");
         ObjectOutputStream oos = new ObjectOutputStream(fout);
         oos.writeObject(b);
       }
    }*/
    protected void add(String pathName) throws IOException, ClassNotFoundException {
        File file = new File(pathName); //as they are in the root directory as .gitlet
        if (!file.exists()) {
//            if(removedFiles.contains(pathName)) {
//                removedFiles.remove(pathName);
//                return ;
//            }
            throw new FileNotFoundException("File does not exist.");
        }
        if (removedFiles.contains(pathName)) {
            removedFiles.remove(pathName);
            return;
        }
        byte[] content = readContents(file);
        Blob b = new Blob(pathName, content);

        if (isTracked(file)) {
            Commit prevCommit = getOneCommit(getSHAOfHeadCommit());
            String prevCommitSHA = prevCommit.getTrackedFilesNameToCommitSha()
                    .get(pathName + ".ser");
            Blob prevBlob = readOneObject(COMMIT + "/" + prevCommitSHA + "/"
                    + pathName + ".ser");
            if (prevBlob.getSha1().equals(b.getSha1())) {
                return;
            }
        }
        String newPathName = STAGING + "/" + pathName + ".ser";
        File toBeWritten = new File(newPathName);
        if (!toBeWritten.exists()) {
            writeOneObject(newPathName, b);
        } else {
            Blob oldB = readOneObject(newPathName);
            if (Blob.areShasEqual(oldB, b)) {
                return;
            } else {
                new File(newPathName).delete();
                writeOneObject(newPathName, b);
            }
        }

    }

    /*    */

    /**
     * before we add a blob to staging area, use this function
     * to check if blob containing the same file exists.
     * if true, if the content are the same, we stop adding.
     *//*
    private static boolean isInStaging(Blob b) throws IOException, ClassNotFoundException {
      FileInputStream fis = new FileInputStream(STAGING);
      ObjectInputStream ois = new ObjectInputStream(fis);
      File toCheck = (File) ois.readObject();
      File[] matchingFiles = toCheck.listFiles(new FilenameFilter()) {
        public boolean accept(File toCheck, String name) {
        return name.startsWith(b.getFileName) && name.endsWith("ser");
        }
      });
      if (matchingFiles.length == 0) {
        return true;
      } else {
        Blob old = (Blob)matchingFiles[0];
          if (old.getSha == b.getSha) {
            return false;
          } else {
            return true;
          }
        }
      }*/
    protected static <K, V extends List<T>, T> void updateTreeList(Map<K, V> m, K msg, T sha) {
        if (m.containsKey(msg)) {
            m.get(msg).add(sha);
        } else {
            V aList = (V) new ArrayList<T>();
            aList.add(sha);
            m.put(msg, aList);
        }
        return;
    }

    /**
     * permanently save files in the staging area,
     * record the commit message paired with each commit
     */
    protected void commit(String message)
            throws IOException, ClassNotFoundException, NullPointerException {
//        if (message.length()==0) {
//            throw new IllegalArgumentException("Please enter a commit message.");
//        }
        File folder = new File(STAGING);
        if (folder.list().length == 0 && removedFiles.size() == 0) {
            throw new IllegalCallerException("No changes added to the commit.");
        }
        Commit prevCommit = getOneCommit(branchToSha.get(branch));
        List<String> newParent = prevCommit.getUpdatedParents();
        LinkedHashMap<String, String> trackedFilesNameToCommitSha =
                prevCommit.getTrackedFilesNameToCommitSha();


        Commit commit = new Commit(newParent, message,
                trackedFilesNameToCommitSha, folder.list(), removedFiles);

        String sha = commit.getSha();

        shaToPath.put(sha, COMMIT + "/".concat(sha));
        updateTreeList(msgToShas, message, sha);
        branchToSha.put(branch, sha);

        //Move all Blobs and move it to the suitable files
        //make the new dir under /Commit
        String commitPath = COMMIT + "/" + sha;
        File commitDir = new File(commitPath);
        commitDir.mkdir();
        writeOneObject(commitDir + "/" + sha + ".ser", commit);

        //clean up
        removedFiles = new ArrayList<String>();

        //recursive remove everything from staging and move to the new File
        String[] allFiles = new File(STAGING).list();
        for (String filename : allFiles) {
            Path path = new File(STAGING + "/" + filename).toPath();
            Files.copy(path, Paths.get(commitPath, "/" + filename));
            Files.delete(path);
        }
    }


    /**
     * Untrack the file; that is, indicate (somewhere in the .gitlet directory)
     * that it is not to be included in the next commit, even if it is tracked in
     * the current commit (which will become the next commit’s parent).
     * This command breaks down as follows:
     * <p>
     * If the file is tracked by the current commit, delete the file from the working directory,
     * unstage it if it was staged, and mark the file to be untracked by the next commit.
     * If the file isn’t tracked by the current commit but it is staged
     * , unstage the file and do nothing else.
     */
    protected void rm(String pathName) throws IOException, ClassNotFoundException {
        File file = new File(pathName);
        File stagedFile = new File(STAGING + "/" + pathName + ".ser");
        if (!isTracked(file) && !stagedFile.exists()) {
            throw new IllegalArgumentException("No reason to remove the file.");
        }
        if (isTracked(file)) {
            removedFiles.add(pathName);
            if (file.exists()) {
                file.delete();
            }
        }
        if (stagedFile.exists()) {
            stagedFile.delete();
        }


    }

/*    protected void rm(String filename) throws IOException, ClassNotFoundException {
       Commit curr = getOneCommit(getSHAOfHeadCommit());
       File[] stagingCheck = findFile(STAGING, filename, "ser");
       if (curr.getTrackedFilesNameToCommitSha().keySet().contains(filename)) {
           Path file = new File(HOMEDIR + "/" + filename).toPath();
           Files.delete(file);
           curr.removeDeletedFile(filename);
           if (stagingCheck.length > 0) {
               Path path = new File(STAGING + "/" + filename).toPath();
               Files.delete(path);
           }
           removedFiles.add(filename);
           return;
       }
         if (stagingCheck.length > 0) {
             Path file = new File(STAGING + "/" + filename).toPath();
             Files.delete(file);
//             removedFiles.add(filename);
             return;
        }
        throw new IllegalArgumentException("No reason to remove the file.");
    }*/

    /**
     * Starting at the current head commit, display information about each commit backwards
     * along the commit tree until the initial commit.
     * <p>
     * For every node in this history, the information it should display is the commit id,
     * the time the commit was made, and the commit message.
     */
    protected void log() throws IOException, ClassNotFoundException {
        String shaOfHeadCommit = getSHAOfHeadCommit();
        //String pathOfHeadCommit = getShaOfHeadCommit();
        Commit headCommit = getOneCommit(shaOfHeadCommit);
        printCommit(shaOfHeadCommit);
        List<String> parents = headCommit.getParents();
        Collections.reverse(parents);
        for (String shaOfParents : parents) {
            printCommit(shaOfParents);
        }
    }

    /**
     * show all commits history
     */
    protected void globalLog() throws IOException, ClassNotFoundException {
        for (String shaOfCommit : getShaToPath().keySet()) {
            printCommit(shaOfCommit);
        }
    }

    /**
     * print all commit ids with the given message
     */
    protected void find(String message) throws IllegalArgumentException {
        List<String> commitShas = getMsgToShas().get(message);
        if (commitShas == null) {
            throw new IllegalArgumentException("Found no commit with that message.");
        } else {
            for (String sha : commitShas) {
                System.out.println(sha);
            }
        }
    }

    /**
     * Takes the version of the file as it exists in the head commit,
     * the front of the current branch, and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one.
     * The new version of the file is not staged.
     */
    protected void checkout(String pathName) throws ClassNotFoundException, IOException {
        checkout(getSHAOfHeadCommit(), pathName);
    }

//    //Use the readOneObject()
//    /** Returns the .ser file with the given commit and filename. */
//   protected File getSERFile (Commit targetcommit, String fileName) throws FileNotFoundException{
//      //List listofblob = readBlobsDir (pathOfBlobSER);
//      *//** read all the blobs in the the directory and return it as a List *//*
//      //protected static List<Blob> readBlobsDir (String path){}
//      String pathOfBlobSER = targetcommit + "/" + fileName + ".ser";
//
//      /** Gets the file(.ser) of the previous version, so that we can access the
//     blob/cotent using function in Utils.java. */
//      FileOutputStream fout = new FileOutputStream(pathOfBlobSER);
//      return fout;
//    }
//


//    /** A helper method that overwrites the content of the given commit by the
//     previous commit. (given the sha1 of the previous commit) */
//    protected Commit overwriteWithSHA1 (Commit currcommit, String sha1) {
///*
//      String pathOfBlobSER = Commit + "/" + fileName + ".ser";
//
//        static byte[] readContents(File file) {}
//        static void writeContents(File file, byte[] bytes) {}
//
//        FileOutputStream fout = new FileOutputStream(STAGING + "/" + fileName + ".ser");
//        ObjectOutputStream oos = new ObjectOutputStream(fout);
//        oos.writeObject(b);
//      }*/
//        return null;
//    }
//


    /**
     * Takes the version of the file as it exists in the commit with the given id,
     * and puts it in the working directory,
     * overwriting the version of the file that’s already there if there is one. T
     * he new version of the file is not staged.
     */
    protected void checkout(String commitSha, String pathName)
            throws IOException, ClassNotFoundException {
        boolean paired = false;
        String completeID = "";
        Set<String> sHA = shaToPath.keySet();
        for (String i : sHA) {
            if (i.startsWith(commitSha)) {
                paired = true;
                completeID = i;
                //System.out.println(completeID);
                break;
            }
        }
        if (!paired) {
            throw new IllegalArgumentException("No commit with that id exists.");
        }
        Commit headCommit = getOneCommit(completeID);
        LinkedHashMap<String, String> trackedFiles = headCommit.getTrackedFilesNameToCommitSha();
        if (!trackedFiles.containsKey(pathName + ".ser")) {
            throw new IllegalArgumentException("File does not exist in that commit.");
        }
        Blob blob = (Blob) readOneObject(COMMIT + "/"
                + trackedFiles.get(pathName + ".ser") + "/" + pathName + ".ser");
        File toReplace = new File(pathName);
        Utils.writeContents(toReplace, blob.getContent());
    }

    /**
     * switch branch
     */
    protected void checkoutBranch(String branchName) throws IOException, ClassNotFoundException {
        if (!branchToSha.containsKey(branchName)) {
            throw new IllegalArgumentException("No such branch exists");
        } else if (branchName.equals(this.branch)) {
            throw new IllegalArgumentException("No need to checkout the current branch");
        } else if (isAnyUntracked()) {
            throw new IOException("There is an untracked file in the way; "
                    + "delete it or add it first.");
        } else {
            branch = branchName;

            //clean up staging area
            String[] allFiles = new File(STAGING).list();
            for (String filename : allFiles) {
                Path path = new File(STAGING + "/" + filename).toPath();
                Files.delete(path);
            }

            //clean up working dir
            File workingDir = new File(System.getProperty("user.dir"));
            File[] files = workingDir.listFiles();
            if (files.length != 0) {
                for (File file : files) {
                    if (!file.isDirectory()) {
                        file.delete();
                    }
                }
            }


            removedFiles = new ArrayList<>();

            Commit commitCheckouted = getOneCommit(branchToSha.get(branchName));
            LinkedHashMap<String, String> filesAndSha =
                    commitCheckouted.getTrackedFilesNameToCommitSha();
            for (String pathName : filesAndSha.keySet().toArray(new String[filesAndSha.size()])) {
                restoreOneFile(pathName, filesAndSha.get(pathName));
            }
        }
    }

    private void restoreOneFile(String pathNameSER, String commitSha)
            throws IOException, ClassNotFoundException {
        Blob blob = (Blob) readOneObject(COMMIT + "/" + commitSha + "/" + pathNameSER);
        File toReplace = new File(pathNameSER.substring(0, pathNameSER.lastIndexOf('.')));
        Utils.writeContents(toReplace, blob.getContent());
    }

    private void conflictAppend(String pathNameSER, String commitSha)
            throws IOException, ClassNotFoundException {
        File toWrite = new File(pathNameSER.substring(0, pathNameSER.lastIndexOf('.')));
        byte[] headContent;
        if (toWrite.exists()) {
            headContent = readContents(toWrite);
        } else {
            headContent = new byte[0];
        }
        Files.write(toWrite.toPath(), "<<<<<<< HEAD\n".getBytes());
        Files.write(toWrite.toPath(), headContent, StandardOpenOption.APPEND);
        Files.write(toWrite.toPath(), "=======\n".getBytes(), StandardOpenOption.APPEND);
        byte[] byteToWrite;
        try {
            Blob blob = (Blob) readOneObject(COMMIT + "/" + commitSha
                    + "/" + pathNameSER);
            byteToWrite = blob.getContent();
            Files.write(toWrite.toPath(), byteToWrite, StandardOpenOption.APPEND);
            Files.write(toWrite.toPath(), ">>>>>>>\n".getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            byteToWrite = new byte[0];
            Files.write(toWrite.toPath(), byteToWrite, StandardOpenOption.APPEND);
            Files.write(toWrite.toPath(), ">>>>>>>\n".getBytes(), StandardOpenOption.APPEND);
        }
    }

    private boolean isAnyUntracked(String commitId) throws IOException, ClassNotFoundException {
        Set<String> prevTracking = getOneCommit(branchToSha.get(branch))
                .getTrackedFilesNameToCommitSha().keySet();
        //System.out.println("llL" + Paths.get(System.getProperty("user.dir")).toString());
        File[] files = Paths.get(System.getProperty("user.dir")).toFile().listFiles();
        if (files.length == 0) {
            return false;
        }
        for (File file : files) {
            if (file.getName().trim().equals(".gitlet")) {
                continue;
            }
            if (!prevTracking.contains(file.getName() + ".ser")
                    && getOneCommit(commitId).getTrackedFilesNameToCommitSha()
                    .containsKey(file.getName() + ".ser")) {
                //System.out.println(file.getName());
                return true;
            }
        }
        return false;
    }

    private boolean isAnyUntracked() throws IOException, ClassNotFoundException {
        Set<String> prevTracking = getOneCommit(branchToSha.get(branch))
                .getTrackedFilesNameToCommitSha().keySet();
        //System.out.println("llL" + Paths.get(System.getProperty("user.dir")).toString());
//        File[] files = Paths.get(System.getProperty("user.dir")).toFile().listFiles();
        File[] files = new File(System.getProperty("user.dir")).listFiles();
        if (files.length == 0) {
            return false;
        }
        for (File file : files) {
            if (file.getName().trim().equals(".gitlet")) {
                continue;
            }
            if (!prevTracking.contains(file.getName() + ".ser")) {
                //System.out.println(file.getName());
                return true;
            }
        }
        return false;
    }

//    /** Takes a filename, and returns the pathName */
//    protected static Path ToPathName (String fileName) {
//        //String path = "";
//        Path path = FileSystems.getDefault().getPath(fileName);
//        return path;
//    }
//
//    protected static Path getCurrWorkDir() {
//
//        Path path = FileSystems.getDefault().getPath(".");
//        Path absolute_path = FileSystems.getDefault().getPath(".").toAbsolutePath();
//
//        return path;
//        //return absolute_path;
//    }


//    /** given a value of a map, and return the key of it. Basically,
//     doing get reversely */
//    protected <K, V> Set getKeyFromValue (Map<K, V> map, V value) {
//        return map.entrySet()
//                .stream()
//                .filter(entry -> Objects.equals(entry.getValue(), value))
//                .map(Map.Entry :: getKey)
//                .collect(Collectors.toSet());
//    }
//


    /**
     * create a new branch
     */
    protected void branch(String branchName) throws IllegalArgumentException {
        if (branchToSha.containsKey(branchName)) {
            throw new IllegalArgumentException("A branch with that name already exists.");
        }
        String shaOfHead = getSHAOfHeadCommit();
        branchToSha.put(branchName, shaOfHead);
    }

    /**
     * Deletes the branch with the given name. (delete the pointer)
     */
    protected void rmBranch(String branchName) {
        if (!branchToSha.containsKey(branchName)) {
            throw new IllegalArgumentException("A branch with that name does not exist.");
        } else if (branch.equals(branchName)) {
            throw new IllegalArgumentException("Cannot remove the current branch.");
        }
        branchToSha.remove(branchName);
    }


    /**
     * Checks out all the files tracked by the given commit. Removes tracked
     * // files that are not present in the given commit. Moves the current branch’s
     * // head pointer and the head pointer to that commit node.
     */
    protected void reset(String cmdID) throws IOException, ClassNotFoundException {
        boolean paired = false;
        String completeID = "";
        Set<String> sha = shaToPath.keySet();
        for (String i : sha) {
            if (i.startsWith(cmdID)) {
                paired = true;
                completeID = i;
                //System.out.println(completeID);
                break;
            }
        }
        //System.out.println("hh" +completeID);
        if (!paired) {

            throw new IllegalArgumentException("No commit with that id exists.");
        }
        //ArrayList<String> untrackedFiles = findUntrackedFile();
        //if (!untrackedFiles.isEmpty()) { //LOR: this method is wrong, f
        // or more details, check the notes in the method
        if (isAnyUntracked(cmdID)) {
            throw new InterruptedIOException("There is an untracked file in the way; "
                    + "delete it or add it first.");
            //System.out.println("There is an untracked file in the way;
            // delete it or add it first.");
            //return;
        }

        File workingDir = Paths.get(System.getProperty("user.dir")).toFile();
        //System.out.println("cc" + workingDir);
        //System.out.println(workingDir.listFiles().length);
        File[] files = workingDir.listFiles();
        if (files.length != 0) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    file.delete();
                }
            }
        }


/*        Commit des = getOneCommit(completeID);
        for (String file : des.getTrackedFilesNameToCommitSha().values()) {
            Blob temp = (Blob) readOneObject(file);
            writeContents(new File(getCurrWorkDir().toString() + "/" +
            temp.getFileName()), temp.getContent());
        }*/
        removedFiles = new ArrayList<>();

        //clean up staging area
        String[] allFiles = new File(STAGING).list();
        for (String filename : allFiles) {
            Path path = new File(STAGING + "/" + filename).toPath();
            Files.delete(path);
        }

        branchToSha.put(branch, completeID);

        Commit des = getOneCommit(completeID);
        //System.out.println("haha"+des);
        LinkedHashMap<String, String> filesAndSha = des.getTrackedFilesNameToCommitSha();
        for (String pathName : filesAndSha.keySet().toArray(new String[filesAndSha.size()])) {
            restoreOneFile(pathName, filesAndSha.get(pathName));
        }


    }

    private void checkWeirdException(String branchName) throws IOException,
            ClassNotFoundException {
        if (!branchToSha.containsKey(branchName)) {
            throw new IllegalArgumentException("A branch with that name does not exist");
        } else if (removedFiles.size() != 0 || Paths.get(System.getProperty("user.dir"), STAGING)
                .toFile().list().length != 0) {
            throw new IllegalArgumentException("You have uncommitted changes.");
        } else if (branchName.equals(branch)) {
            throw new IllegalArgumentException("Cannot merge a branch with itself.");
        } else if (isAnyUntracked()) {
            throw new IllegalArgumentException("There is an untracked file in the way; "
                    + "delete it or add it first.");
        }
    }

    /**If the split point is the same commit as the given branch, then we do nothing; the merge
     * is complete, and the operation ends with the message Given branch is an ancestor of
     * the current branch.*/
    /**
     * If the split point is the current branch, then the current branch is set to the same commit
     * as the given branch and the operation ends after printing the message Current branch
     * fast-forwarded.
     */
    //haha
    protected void merge(String branchName) throws IOException,
            ClassNotFoundException {
        checkWeirdException(branchName);
        String shaOfSplitPoint = findSplitPoint(branchName);
        Commit splitPoint = getOneCommit(shaOfSplitPoint);
        if (shaOfSplitPoint.equals(getBranchToSha().get(branchName))) {
            throw new IllegalArgumentException("Given branch is an ancestor of the current"
                    + " branch.");
        }
        if (shaOfSplitPoint.equals(getSHAOfHeadCommit())) {
            branch = branchName;
            throw new IllegalArgumentException("Current branch fast-forwarded.");
        }
        LinkedHashMap<String, String> st = splitPoint.getTrackedFilesNameToCommitSha();
        LinkedHashMap<String, String> ct = getOneCommit(getSHAOfHeadCommit())
                .getTrackedFilesNameToCommitSha();
        LinkedHashMap<String, String> bt = getOneCommit(getBranchToSha().get(branchName))
                .getTrackedFilesNameToCommitSha();
        Set<String> allFilenames = new HashSet<>();
        allFilenames.addAll(st.keySet());
        allFilenames.addAll(ct.keySet());
        allFilenames.addAll(bt.keySet());

        boolean hasConflict = false;

        for (String file : allFilenames) {
            String actualFilename = file.substring(0, file.lastIndexOf('.'));
            if (existsInBranch(file, ct, st, bt)) { //exists in all branch
                String btSha = bt.get(file);
                String ctSha = ct.get(file);
                String stSha = st.get(file);
                if (ctSha.equals(btSha)) { //unmodified in ct, bt
                    continue;
                } else if (!stSha.equals(btSha) && stSha.equals(ctSha)) { //modified only in bt
                    restoreOneFile(file, btSha);
                    add(actualFilename);
                } else if (!stSha.equals(ctSha) && stSha.equals(btSha)) {
                    continue;
                } else { // exist in all; both modified
                    hasConflict = true;
                    conflictAppend(file, btSha);
                }
            } else if (!existsInBranch(file, st)) {
                if (existsInBranch(file, ct) && !existsInBranch(file, bt)) {
                    continue;
                } else if (existsInBranch(file, bt) && !existsInBranch(file, ct)) {
                    String btSha = bt.get(file);
                    restoreOneFile(file, btSha);
                    add(actualFilename);
                } else {
                    String btSha = bt.get(file);
                    hasConflict = true;
                    conflictAppend(file, btSha);
                }
            } else if (existsInBranch(file, st)) {
                String btSha = bt.get(file);
                String ctSha = ct.get(file);
                String stSha = st.get(file);
                if (!existsInBranch(file, bt) && ctSha.equals(stSha)) {
                    File toBeDeleted = new File(actualFilename);
                    if (toBeDeleted.exists()) {
                        toBeDeleted.delete();
                    }
                    removedFiles.add(actualFilename);
                } else if (!existsInBranch(file, ct) && btSha.equals(stSha)) {
                    continue;
                } else {
                    hasConflict = true;
                    conflictAppend(file, btSha);
                }
            } else {
                throw new IOException("This is impossible I know.");
            }
        }
        if (!hasConflict) {
            commit(String.format("Merged %s with %s.", branch, branchName));
        } else {
            throw new IOException("Encountered a merge conflict.");
        }
    }

    private static boolean existsInBranch(String filename, Map<String, String>... maps) {
        for (Map<String, String> map : maps) {
            if (!map.containsKey(filename)) {
                return false;
            }
        }
        return true;
    }

    public static boolean equalsNullCheck(Object o1, Object o2) {
        if (o1 == o2) {
            return true;
        } else if (o1 == null || o2 == null) {
            return false;
        } else {
            return o1.equals(o2);
        }
    }

    private String findSplitPoint(String branchName) throws IOException, ClassNotFoundException {
        Commit branchCommit = getOneCommit(getBranchToSha().get(branchName));
        Commit curr = getOneCommit(getSHAOfHeadCommit());
        /*List<String> pofB = branch.getParents();
        List<String> pofC = curr.getParents();
        pofB.add(branch.getSha());
        pofC.add(curr.getSha());*/
        List<String> pofB = branchCommit.getUpdatedParents();
        List<String> pofC = curr.getUpdatedParents();
        Collections.reverse(pofC);
        //String first = pofC.get(0);
        //String sec;
        for (String commitSha : pofC) {
            if (pofB.contains(commitSha)) {
                return commitSha;
            }
        }
        throw new IllegalArgumentException("Split point not found.");
    }


    /**
     * print out commit ID, date and message, given SHA-1 of commit
     */
    protected void printCommit(String shaOfCommit) throws ClassNotFoundException, IOException {
        System.out.println("===");
        System.out.println("Commit " + shaOfCommit);
        Commit curr = getOneCommit(shaOfCommit);
        System.out.println(curr.getCommitDate());
        System.out.println(curr.getCommitMessage());
        System.out.printf("%n");
    }

    protected String getSHAOfHeadCommit() {
        return getBranchToSha().get(branch);
    }
//
//    protected String getShaOfHeadCommit() {
//        String ShaOfHeadCommit = getBranchToSha().get(branch);
//        //String pathOfHeadCommit = getCommitPath(ShaOfHeadCommit);
//        return ShaOfHeadCommit;
//    }

//    protected String getPathOfCommit(String SHAOfCommit) {
//        return getShaToPath().get(SHAOfCommit);
//    }

    /**
     * find file from the given working directory
     */
/*    protected File[] findFile(String dir, String fileName, String extension)
throws FileNotFoundException, IOException, ClassNotFoundException{
        FileInputStream fis = new FileInputStream(dir);
        ObjectInputStream ois = new ObjectInputStream(fis);
        File toCheck = (File) ois.readObject();
        File[] matchingFiles = toCheck.listFiles(new FilenameFilter() {
            public boolean accept(File toCheck, String name) {
                return name.startsWith(fileName) && name.endsWith(extension);
            }
        });
        return matchingFiles;
    }*/
//
//    protected List<String> getUntrackingFiles() throws IOException, ClassNotFoundException {
//        return null;
//    }
    protected void status() throws IOException, ClassNotFoundException {
        System.out.println("=== Branches ===");
        String[] branches = getBranchToSha().keySet().toArray(new String[getBranchToSha().size()]);
        Arrays.sort(branches);
        for (String branchName : branches) {
            if (branchName.equals(this.branch)) {
                System.out.printf("*%s\n", branchName);
            } else {
                System.out.println(branchName);
            }
        }


        System.out.println("\n=== Staged Files ===");
        String[] files = new File(STAGING).list();
        Arrays.sort(files);
        for (String file : files) {
            System.out.println(file.substring(0, file.lastIndexOf('.')));
        }


        System.out.println("\n=== Removed Files ===");
        for (String filename : removedFiles) {
            System.out.println(filename);
        }


        /*ArrayList<String> modified = new ArrayList<>();
        ArrayList<String> deleted = new ArrayList<>();
        ArrayList<String> untracked = new ArrayList<>();

        checkFilesStatus(modified, deleted, untracked);*/


        System.out.println("\n=== Modifications Not Staged For Commit ===");
        /*for (String m : modified) {
            System.out.println(m + "(modified)");
        }
        for (String d : deleted) {
            System.out.println(d + "(deleted)");
        }*/

        System.out.println("\n=== Untracked Files ===");
        /*String[] workingDir = new File(getCurrWorkDir().toString()).list();
        Set<String> sf = new HashSet<>(Arrays.asList(files));
        Set<String> tf = new HashSet<>(getOneCommit(getSHAOfHeadCommit())
        .getTrackedFilesNameToCommitSha().keySet());
        for (String w : workingDir) {
            if (!sf.contains(w) && modified.contains(w) && deleted.contains(w)
            && !tf.contains(w)) {
                System.out.println(w);
            }
        }*/
    }






/*    protected void checkFilesStatus(ArrayList<String> modified, ArrayList<String> deleted,
ArrayList<String> untracked) throws IOException, ClassNotFoundException {
        Commit headCommit = getOneCommit(getSHAOfHeadCommit());
        File[] filesWorkingDir = new File("").listFiles();
        for (File file: filesWorkingDir) {

        }
    }*/


//    /**Tracked in the current commit, changed in the working directory, but not staged;*/
//    /** Staged for addition, but with different contents than in the working directory;*/
/*    public Set<String> find_modified() throws IOException, ClassNotFoundException {
        Set<String> modified = new HashSet<>();
        Commit headCommit = getOneCommit(getSHAOfHeadCommit());
        ArrayList<Blob> trackedBlob = new ArrayList<>(*//*LOR: the get method
        will generate and return a new list; headCommit.getTrackedFilesNameToCommitSha()
        .size()*//*);
        File[] workingDir = new File("").listFiles();
        File[] staged = new File(STAGING).listFiles();
        ArrayList<Blob> stagedBlob = new ArrayList<>(staged.length);
        ArrayList<Blob> workingBlob = new ArrayList<>(workingDir.length);
        for (File s: staged) {
            stagedBlob.add(readOneObject(s.toPath().toString()));
            // LOR: the return type of readOneObject is of Blob type
        }
        for (File v: workingDir) {
            workingBlob.add(new Blob(v.getPath(), readContents(v)));
        }
        for (String des : headCommit.getTrackedFilesNameToCommitSha().values()) {
            trackedBlob.add((Blob) readOneObject(des));
        }
        for (Blob wb : workingBlob) {
            for (Blob tb : trackedBlob) {
                if (tb.getFileName().equals(wb.getFileName())) {
                    if (tb.getSha1() != wb.getSha1()) {
                        modified.add(tb.getFileName());
                    }
                }
            }
        }
*//*        for (Blob wb : workingBlob) {
            for (Blob sb : stagedBlob) {
                if (sb.getFileName().equals(wb.getFileName())) {
                    if (sb.getSha1() != wb.getSha1()) {
                        modified.add(sb.getFileName());
                    }
                }
            }
        }*//*
        return modified;
    }*/

    /*    /** Staged for addition, but deleted in the working directory; or Not staged for removal,
     * but tracked in the current commit and deleted from the working directory.*//*
    public ArrayList<String> find_deleted() throws ClassNotFoundException, IOException {
        ArrayList<String> deleted = new ArrayList<>();
        String[] stagingfiles = new File(STAGING).list();
//        Arrays.sort(stagingfiles);
        Set<String> sf = new HashSet<>(Arrays.asList(stagingfiles));

        String[] workingfiles = new File(getCurrWorkDir().toString()).list();
//        Arrays.sort(workingfiles);
        Set<String> wf = new HashSet<>(Arrays.asList(workingfiles));
        Commit headCommit = getOneCommit(getSHAOfHeadCommit());
        String[] tracking = (String[]) headCommit.getTrackedFilesNameToCommitSha().
        keySet().toArray();
        Set<String> tf = new HashSet<>(Arrays.asList(tracking));
        for (String file : tracking) {
            if (!wf.contains(file) && !sf.contains(file)) {
                deleted.add(file);
//                System.out.println(file + "(deleted)");
            }
        }
        for (String file : workingfiles) {
            if (!wf.contains(file) && !tf.contains(file)) {
                deleted.add(file);
//                System.out.println(file + "(deleted)");
            }
        }
        return deleted;
    }*/

    protected boolean isTracked(File file) throws IOException, ClassNotFoundException {
        Commit prevCommit = getOneCommit(branchToSha.get(branch));
        if (prevCommit.getTrackedFilesNameToCommitSha().containsKey(file.getName() + ".ser")) {
            return true;
        }
        return false;
    }
}
