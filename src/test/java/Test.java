import ideaeclipse.gitHubRepo.GithubUser;
import ideaeclipse.gitHubRepo.IRepository;

import java.util.stream.Collectors;

public class Test {
    public static void main(String[] args) {
        for(IRepository repository: new GithubUser(args[0]).getRepositories()){
          repository.backup();
        }
    }
}
