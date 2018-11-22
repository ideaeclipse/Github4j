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
        if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
            File file = new File(System.getProperty("user.dir") + "/temp");
            if (!file.exists())
                file.mkdir();
            writeToFile(file.getAbsolutePath() + "/exec.sh", createExecutable());
        }
        return "word";
    }

    private String createExecutable() {
        StringBuilder builder = new StringBuilder();
        return builder.toString();
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
        public Boolean verified;
        public String title;
        public String key;
    }
}
