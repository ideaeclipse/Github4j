package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.JsonArray;
import ideaeclipse.JsonUtilities.Parser;

import java.io.*;
import java.util.Objects;
import java.util.Scanner;

/**
 * @apiNote User must have {@link ideaeclipse.gitHubRepo.Permissions.Permission#WRITEPUBLICKEY}
 *
 * Class queries all ssh keys adds one for this application if doesn't have one
 *
 * @author Ideaeclipse
 */
@Permissions(permission = Permissions.Permission.WRITEPUBLICKEY)
class Keys extends Call {
    private static final boolean windows;
    private static final String spacer;

    static {
        windows = System.getProperty("os.name").toLowerCase().contains("windows");
        spacer = windows ? "\\" : "/";
    }

    /**
     * @param user githubuser object
     */
    Keys(final GithubUser user) {
        super(user);
    }

    /**
     * Checks users ssh keys
     * If github4j is not there create a new ssh key for this application
     * TODO: check locally for ssh pair
     * @return folder
     */
    @Override
    String execute() {
        String folder = System.getProperty("user.dir") + spacer + "gitHubBackups";
        for (Json json : new JsonArray(String.valueOf(Objects.requireNonNull(Util.httpsCall(Api.keys, getUser().getToken()))))) {
            KeyParser parser = Parser.convertToPayload(json, KeyParser.class);
            if (parser.title.toLowerCase().equals("github4j")) {
                return folder + spacer;
            }
        }
        System.out.println("Generating new ssh key");
        //generate pub files
        File file = new File(folder);
        Util.mkdir(file);
        File keyDir = new File(file.getAbsolutePath() + spacer + "keys");
        Util.mkdir(keyDir);
        String executableName = keyDir.getAbsolutePath() + spacer + "exec" + (windows ? ".bat" : ".sh");
        Util.writeToFile(executableName, createExecutable());
        try {
            if (!windows)
                Runtime.getRuntime().exec("chmod u+rtx " + executableName);
            Runtime.getRuntime().exec(executableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //upload to github
        Json json = new Json();
        json.put("title", "Github4J");
        String key = Util.readFile(folder + spacer + "keys" + spacer + "github_rsa.pub");
        json.put("key", key);
        Util.httpsCall(Api.keys, getUser().getToken(), json);
        file = new File(executableName);
        if (file.exists())
            file.delete();
        return folder + spacer;
    }

    /**
     * Asks for github email
     * @return ssh-keygen command
     */
    private String createExecutable() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Input your github email: ");
        return "ssh-keygen -b 4086 -t rsa -C \"" + sc.next() + "\" -f \"" + System.getProperty("user.dir") + spacer + "gitHubBackups" + spacer + "keys" + spacer + "github_rsa\" -q -N \"\"";
    }

    /**
     * takes title from json string
     */
    public static class KeyParser {
        public String title;
    }
}

