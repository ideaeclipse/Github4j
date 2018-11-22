package ideaeclipse.gitHubRepo;

import java.util.*;
import java.util.stream.Collectors;

import static ideaeclipse.gitHubRepo.Permissions.Permission;

/**
 * Main object to pass your username and token. This while query your data
 * TODO: custom directory for backups
 * TODO: Windows repo code
 * @author Ideaeclipse
 */
public class GithubUser {
    private final String username;
    private final String token;
    private final List<Permission> scopes;

    /**
     * @param username github username
     * @param token github oauth token
     */
    public GithubUser(final String username, final String token) {
        this.username = username;
        this.token = token;
        this.scopes = findScopes();
        call();
    }

    /**
     * Temporary call method to start backup
     */
    private void call() {
        String folder = new Keys(this).getReturn();
        if (folder.length() > 0) {
            new Repositories(this,folder).getReturn();
        } else {
            System.out.println("Key couldn't synced");
        }
    }

    /**
     * @return permission scopes
     */
    List<Permission> getScopes() {
        return this.scopes;
    }

    /**
     * @return OAuth token
     */
    String getToken() {
        return token;
    }

    /**
     * @return list of scopes from a ratelimit header because there is no other way to find scopes
     */
    private List<Permission> findScopes() {
        List<Permission> permissions = new LinkedList<>();
        String temp = String.valueOf(Objects.requireNonNull(Util.httpsCallCon(Api.rateLimit, token)).getHeaderFields().get("X-OAuth-Scopes"));
        String[] scopes = temp.substring(1, temp.length() - 1).split(", ");
        for (String s : scopes) {
            String a = s.replaceAll(":", "").replaceAll("_", "");
            List<Permission> filtered = Arrays.stream(Permission.values()).filter(o -> o.name().toLowerCase().equals(a.toLowerCase())).collect(Collectors.toList());
            if (filtered.size() == 1)
                permissions.add(filtered.get(0));
        }
        return permissions;
    }

}
