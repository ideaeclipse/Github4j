package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.JsonArray;
import ideaeclipse.JsonUtilities.Parser;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @apiNote user must have scope {@link ideaeclipse.gitHubRepo.Permissions.Permission#REPO}
 *
 * Class queries all repo data and creates a shell script to clone all repos
 *
 * TODO: create repo object make download optional
 * TODO: Don't download repo if it is already up to date
 *
 * @author Ideaeclipse
 */
@Permissions(permission = Permissions.Permission.REPO)
class Repositories extends Call {
    private final String folder;
    private static final boolean windows;
    private static final String spacer;

    static {
        windows = System.getProperty("os.name").toLowerCase().contains("windows");
        spacer = windows ? "\\" : "/";
    }

    /**
     * @param user githubuser
     * @param folder folder to store data
     */
    Repositories(final GithubUser user, final String folder) {
        super(user);
        this.folder = folder;
    }

    /**
     * Creates a shell script to organize all cloned repos
     * TODO: Windows
     * @return nothing currently
     */
    @Override
    String execute() {
        for (Json json : new JsonArray(Objects.requireNonNull(Util.get(Api.userRepos, getUser().getToken())))) {
            RepoMapper mapper = Parser.convertToPayload(json, RepoMapper.class);
            String time = mapper.pushed_at.substring(0, mapper.pushed_at.indexOf("T"));
            String command = "";
            if(!windows) {
               command = "mkdir " + folder + "backups\n" +
                        "cd " + folder + "backups\n" +
                        "mkdir " + mapper.name + "\n" +
                        "cd " + mapper.name + "\n" +
                        "GIT_SSH_COMMAND='ssh -i " + folder + "keys" + spacer + "github_rsa' git clone --quiet " + mapper.ssh_url + "\n" +
                        "zip -r " + mapper.name + ".zip " + mapper.name + "\n" +
                        "rm -rf " + mapper.name + "\n" +
                        "mkdir " + time + "\n" +
                        "mv " + mapper.name + ".zip " + time + spacer;
            }
            String fileName = folder + "exec2.sh";
            Util.writeToFile(fileName, command);
            executeCommand(fileName, mapper.name);
        }
        return null;
    }

    /**
     * Executes the shell script
     * @param fileName fileName of shell script
     * @param name name of directory
     */
    private void executeCommand(final String fileName, final String name) {
        File file = new File(fileName);
        System.out.println("Downloading " + name);
        if (file.exists()) {
            try {
                Runtime.getRuntime().exec("chmod u+rtx " + fileName);
                ProcessBuilder pb = new ProcessBuilder(fileName);
                Process p = pb.start();
                p.waitFor();
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

    /**
     * Mapper object for repo json
     */
    public static class RepoMapper {
        public String ssh_url;
        public String name;
        public String pushed_at;
    }
}
