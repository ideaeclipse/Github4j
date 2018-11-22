package ideaeclipse.gitHubRepo;

class Api {
    private static String endpoint = "https://api.github.com/";
    static String rateLimit = endpoint + "rate_limit";
    static String getKeys = endpoint + "user/keys";
    static String userRepos = endpoint + "user/repos";
    static String publicUserRepos = endpoint + "users/:username/repos";
}
