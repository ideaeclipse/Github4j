package ideaeclipse.gitHubRepo;

import ideaeclipse.JsonUtilities.Json;
import ideaeclipse.JsonUtilities.JsonArray;
import ideaeclipse.JsonUtilities.Parser;

import java.util.Objects;

/**
 * @apiNote requires permission {@link Permissions.Permission#USEREMAIL}
 *
 * Execute returns the users primary email to use in ssh key generation
 *
 * @author Ideaeclipse
 */
@Permissions(permission = Permissions.Permission.USEREMAIL)
class Email extends Call {
    Email(final GithubUser user) {
        super(user);
    }

    /**
     * @return users primary email
     */
    @Override
    String execute() {
        String email = "";
        for(Json json: new JsonArray(Objects.requireNonNull(Util.get(Api.emails, getUser().getToken())))){
            EmailMapper mapper = Parser.convertToPayload(json,EmailMapper.class);
            if(mapper.primary)
                email = mapper.email;
        }
        return email;
    }

    /**
     * Mapper class for email object
     */
    public static class EmailMapper{
        public String email;
        public Boolean primary;
    }
}
