package ideaeclipse.gitHubRepo;

public class Repository extends IRepository {
    private final String pushed_at, langauge, ssh_url, html_url, name;
    private final Boolean isPrivate;

    Repository(final String pushed_at, final String language, final String ssh_url, final String html_url, final String name, final Boolean isPrivate, final GithubUser user) {
        super(user);
        this.pushed_at = pushed_at;
        this.langauge = language;
        this.ssh_url = ssh_url;
        this.html_url = html_url;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    @Override
    public String getTimeStamp() {
        return this.pushed_at;
    }

    @Override
    public String getLanguage() {
        return this.langauge;
    }

    @Override
    public String getSSH_URL() {
        return this.ssh_url;
    }

    @Override
    public String getHTML_URL() {
        return this.html_url;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Boolean isPrivate() {
        return this.isPrivate;
    }

    @Override
    public String toString() {
        return "{Repository: " + getName() + "} Pushed At: " + getTimeStamp() + " ssh_url: " + getSSH_URL() + " html_url: " + getHTML_URL() + " language: " + getLanguage() + " isPrivate: " + isPrivate();
    }
}
