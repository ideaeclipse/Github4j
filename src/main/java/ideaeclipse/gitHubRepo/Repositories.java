package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.JsonArray;
import ideaeclipse.JsonUtilities.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Permissions(permission = Permissions.Permission.REPO)
class Repositories extends Call {
    private final List<IRepository> repositories;

    Repositories(GithubUser user) {
        super(user);
        this.repositories = new LinkedList<>();
    }

    @Override
    String execute() {
        for (Json json : new JsonArray(Objects.requireNonNull(Util.get(Api.userRepos, getUser().getToken())))) {
            RepoMapper mapper = Parser.convertToPayload(json, RepoMapper.class);
            mapper.isPrivate = (Boolean) json.get("private");
            repositories.add(new Repository(mapper.pushed_at, mapper.language, mapper.ssh_url, mapper.html_url, mapper.name, mapper.isPrivate,getUser()));
        }
        return repositories.isEmpty() ? "No repos" : "true";
    }

    public List<IRepository> getRepositories() {
        getReturn();
        return repositories;
    }

    public static class RepoMapper {
        public String pushed_at;
        public String language;
        public String ssh_url;
        public String html_url;
        public String name;
        public Boolean isPrivate;
    }
}
