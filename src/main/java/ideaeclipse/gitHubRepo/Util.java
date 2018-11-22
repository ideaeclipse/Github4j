package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

class Util {
    static HttpsURLConnection httpsCallCon(final String url, final String token) {
        try {
            return authorize(new URL(url), token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String httpsCall(final String url, final String token) {
        try {
            HttpsURLConnection connection = authorize(new URL(url), token);
            return getReturn(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static String httpsCall(final String url, final String token, final Json json) {
        try {
            HttpsURLConnection connection = json(authorize(new URL(url), token), json);
            return getReturn(connection);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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
}
