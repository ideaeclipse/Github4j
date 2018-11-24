package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.JsonArray;
import ideaeclipse.JsonUtilities.Parser;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Ideaeclipse
 * @apiNote must have {@link ideaeclipse.gitHubRepo.Permissions.Permission#REPO}
 * This class allows for querying of all your repositories
 * It then parsers them and adds it to a list of repositories
 */
@Permissions(permission = Permissions.Permission.REPO)
class Repositories extends Call {
    private final List<IRepository> repositories;

    /**
     * @param user pass the user object
     */
    Repositories(final GithubUser user) {
        super(user);
        this.repositories = new LinkedList<>();
    }

    /**
     * Queries all repositories
     *
     * @return returns a string of true if they parsed successfully
     */
    @Override
    String execute() {
        for (Json json : new JsonArray(Objects.requireNonNull(Util.get(Api.userRepos, getUser().getToken())))) {
            RepoMapper mapper = Parser.convertToPayload(json, RepoMapper.class);
            mapper.isPrivate = (Boolean) json.get("private");
            repositories.add(new Repository(mapper.pushed_at, mapper.language, mapper.ssh_url, mapper.html_url, mapper.name, mapper.isPrivate, getUser()));
        }
        return repositories.isEmpty() ? "No repos" : "true";
    }

    /**
     * @return list of repository objects
     */
    public List<IRepository> getRepositories() {
        getReturn();
        return repositories;
    }

    /**
     * Mapper class for repositories
     * private gets manually set because you can't have a variable with the name private
     */
    public static class RepoMapper {
        public String pushed_at;
        public String language;
        public String ssh_url;
        public String html_url;
        public String name;
        public Boolean isPrivate;
    }
}
