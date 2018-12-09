import ideaeclipse.gitHubRepo.GithubUser;


public class Test {
    public static void main(String[] args) {
        new GithubUser(args[0]).backUpAllRepos(System.getProperty("user.dir") + "/gitHubBackups");
    }
}
