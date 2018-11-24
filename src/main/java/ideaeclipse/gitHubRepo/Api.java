package ideaeclipse.gitHubRepo;

/**
 * Endpoints
 *
 * @author Ideaeclipse
 */
class Api {
    private static String endpoint = "https://api.github.com/";
    static String rateLimit = endpoint + "rate_limit";
    static String keys = endpoint + "user/keys";
    static String userRepos = endpoint + "user/repos";
    static String emails = endpoint + "user/emails";
}
