package ideaeclipse.gitHubRepo;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ideaeclipse.gitHubRepo.Permissions.Permission;

/**
 * Main object to pass your username and token. This while query your data
 * TODO: custom directory for backups
 * TODO: Windows repo code
 *
 * @author Ideaeclipse
 */
public class GithubUser {
    private final String token;
    private final List<Permission> scopes;

    /**
     * @param token github oauth token
     */
    public GithubUser(final String token) {
        this.token = token;
        this.scopes = findScopes();
    }

    public List<IRepository> getRepositories() {
        return new Repositories(this).getRepositories();
    }

    /**
     * Temporary call method to start backup
     */
    public void backUpAllRepos() {
        String folder = new Keys(this, System.getProperty("user.dir") + "/gitHubBackups").getReturn();
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
        String temp = String.valueOf(Objects.requireNonNull(Util.getCon(Api.rateLimit, token)).getHeaderFields().get("X-OAuth-Scopes"));
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
