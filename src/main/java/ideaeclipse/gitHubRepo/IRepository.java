package ideaeclipse.gitHubRepo;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class IRepository {
    private static final boolean windows;
    private static final String spacer;
    private final GithubUser user;

    static {
        windows = System.getProperty("os.name").toLowerCase().contains("windows");
        spacer = windows ? "\\" : "/";
    }

    IRepository(final GithubUser user) {
        this.user = user;
    }

    public boolean backup() {
        String folder = new Keys(user, System.getProperty("user.dir") + "/gitHubBackups").getReturn();
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
        executeCommand(fileName, getName());
        return true;
    }

    private static void executeCommand(final String fileName, final String name) {
        File file = new File(fileName);
        System.out.println("Downloading " + name);
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
                executeCommand(fileName, name);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract String getTimeStamp();

    public abstract String getLanguage();

    public abstract String getSSH_URL();

    public abstract String getHTML_URL();

    public abstract String getName();

    public abstract Boolean isPrivate();
}
