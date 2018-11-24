import ideaeclipse.gitHubRepo.GithubUser;
import ideaeclipse.gitHubRepo.IRepository;


public class Test {
    public static void main(String[] args) {
        new GithubUser(args[0]).backUpAllRepos();
    }
}
