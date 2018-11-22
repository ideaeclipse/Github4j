package ideaeclipse.gitHubRepo;

/**
 * Logic for annotation permission system
 *
 * @author Ideaeclipse
 */
abstract class Call {
    private final GithubUser user;

    Call(final GithubUser user) {
        this.user = user;
    }

    /**
     * Checks if permissions for the user is valid
     * @return if permissions is valid
     */
    private boolean permissionCheck() {
        Permissions.Permission permission = this.getClass().getAnnotation(Permissions.class).permission();
        if (user.getScopes().contains(permission)) {
            return true;
        } else {
            System.out.println("Token doesn't have required permisson: " + permission);
        }
        return false;
    }

    /**
     * Data from execute
     * @return data from execute
     */
    String getReturn() {
        return permissionCheck() ? execute() : null;
    }

    /**
     * @return user that was passed
     */
    GithubUser getUser() {
        return user;
    }

    /**
     * @return abstract method for each api call
     */
    abstract String execute();
}
