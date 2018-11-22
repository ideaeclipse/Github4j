package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.JsonArray;
import ideaeclipse.JsonUtilities.Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

@Permissions(permission = Permissions.Permission.WRITEPUBLICKEY)
class Keys extends Call {
    private static final boolean windows;
    private static final String spacer;

    static {
        windows = System.getProperty("os.name").toLowerCase().contains("windows");
        spacer = windows ? "\\" : "/";
    }

    Keys(final GithubUser user) {
        super(user);
    }

    @Override
    String execute() {
        for (Json json : new JsonArray(String.valueOf(Objects.requireNonNull(Util.httpsCall(Api.getKeys, getUser().getToken()))))) {
            KeyParser parser = Parser.convertToPayload(json, KeyParser.class);
            if (parser.title.toLowerCase().equals("github4j"))
                return "true";
        }
        //generate pub files
        File file = new File(System.getProperty("user.dir") + spacer + "temp");
        if (!file.exists())
            file.mkdir();
        String executableName = file.getAbsolutePath() + "/exec" + (windows ? ".bat" : ".sh");
        writeToFile(executableName, createExecutable());
        try {
            if (!windows)
                Runtime.getRuntime().exec("chmod u+rtx" + executableName);
            Runtime.getRuntime().exec(executableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //upload to github


        return "word";
    }

    private String createExecutable() {
        return "ssh-keygen -b 4086 -t rsa -f \"" + System.getProperty("user.dir") + spacer + "temp" + spacer + "github_rsa\" -q -N \"\"";
    }

    private boolean createFile(final File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void writeToFile(final String fileName, final String fileData) {
        if (createFile(new File(fileName))) {
            FileWriter writer;
            try {
                writer = new FileWriter(fileName, false);
                writer.write(fileData, 0, fileData.length());
                writer.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static class KeyParser {
        public String title;
    }
}
