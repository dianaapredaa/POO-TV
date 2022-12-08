package features;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fileio.Actions;
import fileio.Movies;
import fileio.Users;
import main.CurrentAuthentication;
import main.PageType;

import java.util.ArrayList;
import java.util.LinkedList;

public final class RegisterFeatures {
    private CurrentAuthentication currentAuth;
    public CurrentAuthentication getCurrent() {
        return currentAuth;
    }
    public void setCurrent(CurrentAuthentication currentAuthentication) {
        this.currentAuth = currentAuthentication;
    }
    private final ObjectMapper objectMapper = new ObjectMapper();

    // create a PageType object to get different types of pages
    private static final PageType PAGE_TYPE = new PageType();

    /**
     *
     * @param command
     * @param users
     * @param movies
     * @param output
     */
    public void register(final Actions command, final LinkedList<Users> users,
                         final LinkedList<Movies> movies, final ArrayNode output) {
        // only on Register page
        if (!currentAuth.getCurrentPage().getPageType().equals("register")) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.putPOJO("error", "Error");
            objectNode.putPOJO("currentMoviesList", new ArrayList<>());
            objectNode.putPOJO("currentUser", null);
            output.addPOJO(objectNode);
            return;
        }
        String username = command.getCredentials().getName();
        String password = command.getCredentials().getPassword();

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getCredentials().getName().equals(username)) {
                if (users.get(i).getCredentials().getPassword().equals(password)) {
                    // already Registered
                    currentAuth.setCurrentPage(PAGE_TYPE.type("HomePageNonAuthenticated"));

                    // output message
                    ObjectNode objectNode = objectMapper.createObjectNode();
                    objectNode.putPOJO("error", "Error");
                    objectNode.putPOJO("currentMoviesList", new ArrayList<>());
                    objectNode.putPOJO("currentUser", null);
                    output.addPOJO(objectNode);
                    return;
                }
            }
        }

        // register using given credentials
        users.add(new Users(command.getCredentials()));
        currentAuth.setCurrentUser(new Users(command.getCredentials()));

        // move to HomePageAuthenticated
        currentAuth.setCurrentPage(PAGE_TYPE.type("HomePageAuthenticated"));

        // output message
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("error", null);
        objectNode.putPOJO("currentMoviesList", new ArrayList<>());
        objectNode.putPOJO("currentUser", new Users(currentAuth.getCurrentUser()));
        output.addPOJO(objectNode);
    }
}