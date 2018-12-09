package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.Parser;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Util class for all http calls and generic file util calls
 *
 * @author Ideaeclipse
 */
class Util {
    /**
     * Makes an http call but returns a {@link HttpsURLConnection}
     *
     * @param url   destination
     * @param token authorization token
     * @return Connection
     */
    static HttpsURLConnection getCon(final String url, final String token) {
        try {
            return authorize(new URL(url), token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get call but returns response
     *
     * @param url   destination
     * @param token authorization token
     * @return reponse
     */
    static String get(final String url, final String token) {
        try {
            HttpsURLConnection connection = authorize(new URL(url), token);
            return getReturn(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * delete call but returns response
     *
     * @param url   destination
     * @param token authorization token
     * @return reponse
     */
    static HttpsURLConnection delete(final String url, final String token) {
        try {
            return delete(authorize(new URL(url), token));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * post call with json data
     *
     * @param url   destination
     * @param token authorization token
     * @param json  json attachment
     * @return reponse
     */
    static String json(final String url, final String token, final Json json) {
        try {
            HttpsURLConnection connection = json(authorize(new URL(url), token), json);
            return getReturn(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Adds json object to url connection
     *
     * @param con    connection
     * @param object json object
     * @return url connectioj
     */
    private static HttpsURLConnection json(final HttpsURLConnection con, final Json object) {
        try {
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            con.setRequestProperty("Accept", "application/json");
            OutputStream os = con.getOutputStream();
            os.write(object.toString().getBytes(StandardCharsets.UTF_8));
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Sets method to DELETE
     *
     * @param con url connection
     * @return connection
     */
    private static HttpsURLConnection delete(final HttpsURLConnection con) {
        try {
            con.setDoOutput(true);
            con.setRequestMethod("DELETE");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Sets authorization header
     *
     * @param url   destination
     * @param token token
     * @return url connection
     */
    private static HttpsURLConnection authorize(final URL url, final String token) {
        HttpsURLConnection con = null;
        try {
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestProperty("Authorization", "Token " + token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return con;
    }

    /**
     * Gets response from url connection
     *
     * @param con connection
     * @return response
     * @throws IOException connection has bad response
     */
    private static String getReturn(final HttpsURLConnection con) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(con.getInputStream());
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder str = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
            str.append(line);
        reader.close();
        return str.toString();
    }

    /**
     * Creates a file
     *
     * @param file file
     * @return if it creates
     */
    private static boolean createFile(final File file) {
        if (!file.exists()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * writes data to file
     *
     * @param fileName file
     * @param fileData data
     */
    static void writeToFile(final String fileName, final String fileData) {
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

    /**
     * Reads data from file
     *
     * @param fileName file
     * @return data in file
     */
    static String readFile(final String fileName) {
        File file = new File(fileName);
        StringBuilder builder = null;
        if (file.exists()) {
            builder = new StringBuilder();
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                while ((st = br.readLine()) != null)
                    builder.append(st).append("\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Thread.sleep(1000);
                return readFile(fileName);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(builder);
    }

    /**
     * Creates directory for a file
     *
     * @param file file
     */
    static void mkdir(File file) {
        if (!file.exists())
            file.mkdir();
    }

    /**
     * Creates a new repository object based on a json string and a github user
     * @param json json data
     * @param user githubuser user
     * @return new repository
     */
    static Repository getRepository(final Json json, final GithubUser user) {
        Repositories.RepoMapper mapper = Parser.convertToPayload(json, Repositories.RepoMapper.class);
        mapper.isPrivate = (Boolean) json.get("private");
        return new Repository(mapper.pushed_at, mapper.language, mapper.ssh_url, mapper.html_url, mapper.name, mapper.isPrivate, user);
    }
}
