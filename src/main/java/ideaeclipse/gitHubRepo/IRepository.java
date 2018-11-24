package ideaeclipse.gitHubRepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is the super class for the repository class
 * This class is called when you want to backup a specific repo
 *
 * @author Ideaeclipse
 */
public abstract class IRepository {
    private static final boolean windows;
    private static final String spacer;
    private final GithubUser user;

    /*
     * Allows for windows changes
     */
    static {
        windows = System.getProperty("os.name").toLowerCase().contains("windows");
        spacer = windows ? "\\" : "/";
    }

    IRepository(final GithubUser user) {
        this.user = user;
    }

    /**
     * Backups the repository to the backups directory
     * TODO: don't always return true
     * @return if it backups correctly
     */
    public boolean backup(String folder) {
        folder = new Keys(user, folder).getReturn();
        String time = getTimeStamp().substring(0, getTimeStamp().indexOf("T"));
        String command = (!windows?"#!/bin/bash\n":"") +
                "mkdir " + folder + "backups\n" +
                "cd " + folder + "backups\n" +
                "mkdir " + getName() + "\n" +
                "cd " + getName() + "\n" +
                (windows ? "IF NOT EXIST " + time + spacer + " (\n" : "if [ ! -d " + time + spacer + " ]\nthen\n") +
                "mkdir " + time + "\n" +
                (windows ? ("set GIT_SSH_COMMAND=ssh -i " + folder + "keys" + spacer + "github_rsa & git clone --quiet " + getSSH_URL()).replace("\\", "\\\\") : "GIT_SSH_COMMAND='ssh -i " + folder + "keys" + spacer + "github_rsa' git clone --quiet " + getSSH_URL()) + "\n" +
                (windows ? "set \"PATH=%PATH%;C:\\Program Files\\7-Zip\\\"\n" : "") +
                (windows ? "7z a " : "zip -r ") + getName() + (!windows ? ".zip " : " ") + getName() + "\n" +
                (windows ? "move " + getName() + ".7z " + getName() + ".zip " + "\n" : "") +
                (windows ? "move " : "mv ") + getName() + ".zip " + time + spacer + "\n" +
                (windows ? "rmdir /s /q " : "rm -rf ") + getName() + "\n" +
                "echo " + getName() + " finished downloading\n" +
                (windows ? ") ELSE (\n" : "else\n") +
                "echo " + getName() + " is already up to date\n" +
                (windows ? ")" : "fi");
        String fileName = folder + "exec2" + (windows ? ".bat" : ".sh");
        Util.writeToFile(fileName, command);
        executeCommand(fileName);
        return true;
    }

    /**
     * Executes the script that is created in {@link #backup(String)} )}
     * @param fileName absolute path to script
     */
    private void executeCommand(final String fileName) {
        File file = new File(fileName);
        System.out.println("Downloading " + getName());
        if (file.exists()) {
            try {
                if (!windows) {
                    Runtime.getRuntime().exec("chmod u+rtx " + fileName);
                    Thread.sleep(500);
                }
                ProcessBuilder pb = new ProcessBuilder(fileName);
                Process p = pb.start();
                p.waitFor();
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line, previous = "";
                while ((line = reader.readLine()) != null) {
                    previous = line;
                }
                System.out.println("    -> " + previous);
                file.delete();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            try {
                Thread.sleep(1000);
                executeCommand(fileName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @return timestamp of last repo commit
     */
    public abstract String getTimeStamp();

    /**
     * @return language used in the repo
     */
    public abstract String getLanguage();

    /**
     * @return ssh_url for git commands
     */
    public abstract String getSSH_URL();

    /**
     * @return html_url of repo
     */
    public abstract String getHTML_URL();

    /**
     * @return name of repo
     */
    public abstract String getName();

    /**
     * @return if it is private
     */
    public abstract Boolean isPrivate();
}
