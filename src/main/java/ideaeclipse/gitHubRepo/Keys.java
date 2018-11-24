package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.JsonArray;
import ideaeclipse.JsonUtilities.Parser;

import java.io.*;
import java.util.Objects;

/**
 * @author Ideaeclipse
 * @apiNote User must have {@link ideaeclipse.gitHubRepo.Permissions.Permission#ADMINPUBLICKEY}
 * <p>
 * Class queries all ssh keys adds one for this application if doesn't have one
 */
@Permissions(permission = Permissions.Permission.ADMINPUBLICKEY)
class Keys extends Call {
    private static final boolean windows;
    private static final String spacer;
    private String folder;
    private String keys;

    static {
        windows = System.getProperty("os.name").toLowerCase().contains("windows");
        spacer = windows ? "\\" : "/";
    }

    /**
     * @param user githubuser object
     */
    Keys(final GithubUser user, final String folder) {
        super(user);
        this.folder = folder;
    }

    /**
     * Checks users ssh keya
     *
     * @return folder
     */
    @Override
    String execute() {
        this.keys = folder + spacer + "keys" + spacer;
        for (Json json : new JsonArray(String.valueOf(Objects.requireNonNull(Util.get(Api.keys, getUser().getToken()))))) {
            KeyParser parser = Parser.convertToPayload(json, KeyParser.class);
            if (parser.title.toLowerCase().equals("github4j")) {
                if (new File(this.keys + "github_rsa").exists()) {
                    System.out.println("SSH Key found");
                    return folder + spacer;
                }else {
                    if (deleteOldSSHKey(parser.id))
                        break;
                    else {
                        System.out.println("Could not delete ssh key with id: " + parser.id +
                                "Please check the OAuth token permissions or manually delete the key");
                        System.exit(-1);
                    }

                }
            }
        }
        generateSSHKeyPair();
        return folder + spacer;
    }

    /**
     * If github4j is not in the public keys list create a new ssh key for this application
     */
    private void generateSSHKeyPair() {
        System.out.println("Generating new ssh key");
        //generate pub files
        File file = new File(folder);
        Util.mkdir(file);
        File keyDir = new File(keys);
        Util.mkdir(keyDir);
        String executableName = keys + "exec" + (windows ? ".bat" : ".sh");
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
        String key = Util.readFile(keys + "github_rsa.pub");
        json.put("key", key);
        Util.json(Api.keys, getUser().getToken(), json);
        file = new File(executableName);
        if (file.exists())
            file.delete();
    }

    /**
     * Called when the key exists on the website but not locally
     *
     * @param id key id
     */
    private boolean deleteOldSSHKey(final int id) {
        System.out.println("Deleting missing sshkey pair with id: " + id);
        try {
            return Objects.requireNonNull(Util.delete(Api.keys + "/" + id, getUser().getToken())).getResponseCode() == 204;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Asks for github email
     *
     * @return ssh-keygen command
     */
    private String createExecutable() {
        return "ssh-keygen -b 4086 -t rsa -C \"" + new Email(getUser()).getReturn() + "\" -f \"" + this.keys + "github_rsa\" -q -N \"\"";
    }

    /**
     * takes title from json string
     */
    public static class KeyParser {
        public String title;
        public Integer id;
    }
}

