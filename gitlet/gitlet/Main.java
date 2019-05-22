package gitlet;

import java.io.FileNotFoundException;
import java.io.IOException;

import static java.lang.System.exit;

/* Driver class for Gitlet, the tiny stupid version-control system.
   @Andy Kwok, Yun Lyu, Eindra Kyi, Kathy Xu, Emily
*/
public class Main {
    private static Gitlet gitlet;

//haha
    public static void main(String... args) {
        String function = args[0];
        try {
            if (function.equals("init")) {
                gitlet = Gitlet.init();
                gitlet.writeAllObjects();
                return;
            }
            gitlet = new Gitlet();
            gitlet.readAllObjects();
            switch (function) {
                case "add": gitlet.add(args[1]);
                    break;
                case "commit":
                    if (args.length != 2) {
                        throw new IllegalAccessException("Please enter a commit message.");
                    }
                    if (args[1].length() == 0) {
                        throw new IllegalAccessException("Please enter a commit message.");
                    }
                    gitlet.commit(args[1]);
                    break;
                case "rm": gitlet.rm(args[1]);
                    break;
                case "log": gitlet.log();
                    break;
                case "global-log": gitlet.globalLog();
                    break;
                case "find": gitlet.find(args[1]);
                    break;
                case "status": gitlet.status();
                    break;
                case "checkout":
                    if (args.length == 3 && args[1].equals("--")) {
                        gitlet.checkout(args[2]);
                    } else if (args.length == 4) {
                        if (!args[2].equals("--")) {
                            throw new UnsupportedOperationException("Incorrect operands.");
                        }
                        gitlet.checkout(args[1], args[3]); //commitId and filename
                    } else if (args.length == 2) {
                        gitlet.checkoutBranch(args[1]);
                    } else {
                        throw new UnsupportedOperationException(String.format("The operation"
                                + " %s doesn't exist", function));
                    }
                    break;
                case "branch": gitlet.branch(args[1]); //branchName
                    break;
                case "rm-branch": gitlet.rmBranch(args[1]); //branchName
                    break;
                case "reset": gitlet.reset(args[1]); //commit ID
                    break;
                case "merge":
                    gitlet.merge(args[1]); //branch name
                    break;
                default:
                    throw new UnsupportedOperationException(String.format("The operation %s "
                            + "doesn't exist", function));
            }
            gitlet.writeAllObjects();
        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
            exit(0);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(/*"Unidentified Exception: " + */e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (ReflectiveOperationException e) {
            System.out.println(e.getMessage());
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }
}
