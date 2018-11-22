package ideaeclipse.gitHubRepo;

public abstract class Call {
    private final GithubUser user;
    private String returnValue;

    public Call(final GithubUser user) {
        this.user = user;
        this.returnValue = null;
        if (permissionCheck())
            returnValue = execute();

    }

    private boolean permissionCheck() {
        Permissions.Permission permission = this.getClass().getAnnotation(Permissions.class).permission();
        if (user.getScopes().contains(permission)) {
            return true;
        } else {
            System.out.println("Token doesn't have required permisson: " + permission);
        }
        return false;
    }

    public String getReturn() {
        return returnValue;
    }

    GithubUser getUser() {
        return user;
    }

    abstract String execute();
}
