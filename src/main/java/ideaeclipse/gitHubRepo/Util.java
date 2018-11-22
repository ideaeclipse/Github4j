package ideaeclipse.gitHubRepo;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
            return getReturn(authorize(new URL(url), token));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        String line = null;
        while ((line = reader.readLine()) != null)
            str.append(line);
        reader.close();
        return str.toString();
    }
}
