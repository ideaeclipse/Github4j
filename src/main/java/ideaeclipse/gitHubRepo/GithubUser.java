package ideaeclipse.gitHubRepo;

import java.util.*;
import java.util.stream.Collectors;

import static ideaeclipse.gitHubRepo.Permissions.Permission;

public class GithubUser {
    private final String username;
    private final String token;
    private final List<Permission> scopes;

    public GithubUser(final String username, final String token) {
        this.username = username;
        this.token = token;
        this.scopes = findScopes();
        call();
    }

    private void call() {
        System.out.println(new Keys(this).getReturn());
    }

    List<Permission> getScopes() {
        return this.scopes;
    }

    String getToken() {
        return token;
    }

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
