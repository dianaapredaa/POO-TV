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

public final class SeeDetailsFeatures {
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
    public void purchase(final Actions command, final LinkedList<Users> users,
                         final LinkedList<Movies> movies, final ArrayNode output) {
        // only on SeeDetails page
        if (!currentAuth.getCurrentPage().getPageType().equals("see details")) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.putPOJO("error", "Error");
            objectNode.putPOJO("currentMoviesList", new ArrayList<>());
            objectNode.putPOJO("currentUser", null);
            output.addPOJO(objectNode);
            return;
        }

        String objectType = command.getObjectType();
        String movieName;

        if (objectType != null) {
            movieName = command.getMovie();
        } else {
            movieName = currentAuth.getCurrentMoviesList().get(0).getName();
        }

        if (currentAuth.getCurrentMoviesList().get(0).getName().equals(movieName)) {
            // charge for purchasing a movie
            String accType = currentAuth.getCurrentUser().getCredentials().getAccountType();
            if (accType.equals("standard") || currentAuth.getCurrentUser().getNumFreePremiumMovies() == 0){
                int numTokens = currentAuth.getCurrentUser().getTokensCount();
                currentAuth.getCurrentUser().setTokensCount(numTokens - 2);
            } else {
                int numFreePremiumMovies;
                numFreePremiumMovies = currentAuth.getCurrentUser().getNumFreePremiumMovies();
                currentAuth.getCurrentUser().setNumFreePremiumMovies(numFreePremiumMovies - 1);
            }

            // update current user purchased list
            Movies movie = currentAuth.getCurrentMoviesList().get(0);
            currentAuth.getCurrentUser().getPurchasedMovies().add(movie);

            // output message
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.putPOJO("error", null);

            ArrayList<Movies> currentMoviesList = new ArrayList<>();
            for (int j = 0; j < currentAuth.getCurrentMoviesList().size(); j++) {
                movie = new Movies(currentAuth.getCurrentMoviesList().get(j));
                currentMoviesList.add(movie);
            }
            objectNode.putPOJO("currentMoviesList", currentMoviesList);
            objectNode.putPOJO("currentUser", new Users(currentAuth.getCurrentUser()));
            output.addPOJO(objectNode);
            return;
        }

        // output message for Error
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("error", "Error");
        objectNode.putPOJO("currentMoviesList", new ArrayList<>());
        objectNode.putPOJO("currentUser", null);
        output.addPOJO(objectNode);
        currentAuth.setCurrentPage(PAGE_TYPE.type("movies"));
    }

    /**
     *
     * @param command
     * @param users
     * @param movies
     * @param output
     */
    public void watch(final Actions command, final LinkedList<Users> users,
                      final LinkedList<Movies> movies, final ArrayNode output) {
        // only on SeeDetails page
        if (!currentAuth.getCurrentPage().getPageType().equals("see details")) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.putPOJO("error", "Error");
            objectNode.putPOJO("currentMoviesList", new ArrayList<>());
            objectNode.putPOJO("currentUser", null);
            output.addPOJO(objectNode);
            return;
        }

        String objectType = command.getObjectType();
        String movieName;

        if (objectType != null) {
            movieName = command.getMovie();
        } else {
            movieName = currentAuth.getCurrentMoviesList().get(0).getName();
        }

        // check if you purchased the movie
        for (int i = 0; i < currentAuth.getCurrentUser().getPurchasedMovies().size(); i++) {
            String purchasedMovieName = currentAuth.getCurrentUser().getPurchasedMovies().get(i).getName();
            if (purchasedMovieName.equals(movieName)) {

                Movies movie;
                // update current user watched list
                movie = currentAuth.getCurrentUser().getPurchasedMovies().get(i);
                currentAuth.getCurrentUser().getWatchedMovies().add(movie);

                // output message
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.putPOJO("error", null);
                ArrayList<Movies> currentMoviesList = new ArrayList<>();
                for (int j = 0; j < currentAuth.getCurrentMoviesList().size(); j++) {
                    movie = new Movies(currentAuth.getCurrentMoviesList().get(j));
                    currentMoviesList.add(movie);
                }
                objectNode.putPOJO("currentMoviesList", currentMoviesList);
                objectNode.putPOJO("currentUser", new Users(currentAuth.getCurrentUser()));
                output.addPOJO(objectNode);
                return;
            }
        }

        // output message for Error
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("error", "Error");
        objectNode.putPOJO("currentMoviesList", new ArrayList<>());
        objectNode.putPOJO("currentUser", null);
        output.addPOJO(objectNode);
        currentAuth.setCurrentPage(PAGE_TYPE.type("movies"));
    }

    /**
     *
     * @param command
     * @param users
     * @param movies
     * @param output
     */
    public void rate(final Actions command, final LinkedList<Users> users,
                     final LinkedList<Movies> movies, final ArrayNode output) {
        // only on SeeDetails page
        if (!currentAuth.getCurrentPage().getPageType().equals("see details")) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.putPOJO("error", "Error");
            objectNode.putPOJO("currentMoviesList", new ArrayList<>());
            objectNode.putPOJO("currentUser", null);
            output.addPOJO(objectNode);
            return;
        }

        String objectType = command.getObjectType();
        String movieName;

        if (objectType != null) {
            movieName = command.getMovie();
        } else {
            movieName = currentAuth.getCurrentMoviesList().get(0).getName();
        }

        int rating = command.getRate();

        if (rating > 5) {
            // output message
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.putPOJO("error", "Error");
            objectNode.putPOJO("currentMoviesList", new ArrayList<>());
            objectNode.putPOJO("currentUser", null);
            output.addPOJO(objectNode);
            return;
        }

        // check if you watched the movie
        for (int i = 0; i < currentAuth.getCurrentUser().getWatchedMovies().size(); i++) {
            if (currentAuth.getCurrentUser().getWatchedMovies().get(i).getName().equals(movieName)) {
                // increase number of likes
                int numRatings = currentAuth.getCurrentUser().getWatchedMovies().get(i).getNumRatings() + 1;
                currentAuth.getCurrentUser().getWatchedMovies().get(i).setNumRatings(numRatings);
                // calculate rate
                int avgRating = (currentAuth.getCurrentUser().getWatchedMovies().get(i).getNumRatings() * (numRatings - 1) + rating) / numRatings;
                currentAuth.getCurrentUser().getWatchedMovies().get(i).setRating(avgRating);

                // update current user rated list
                Movies movie = currentAuth.getCurrentUser().getWatchedMovies().get(i);
                currentAuth.getCurrentUser().getRatedMovies().add(movie);

                // output message
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.putPOJO("error", null);
                ArrayList<Movies> currentMoviesList = new ArrayList<>();
                for (int j = 0; j < currentAuth.getCurrentMoviesList().size(); j++) {
                    movie = new Movies(currentAuth.getCurrentMoviesList().get(j));
                    currentMoviesList.add(movie);
                }
                objectNode.putPOJO("currentMoviesList", currentMoviesList);
                objectNode.putPOJO("currentUser", new Users(currentAuth.getCurrentUser()));
                output.addPOJO(objectNode);
                return;
            }
        }

        // output message
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("error", "Error");
        objectNode.putPOJO("currentMoviesList", new ArrayList<>());
        objectNode.putPOJO("currentUser", null);
        output.addPOJO(objectNode);
        currentAuth.setCurrentPage(PAGE_TYPE.type("movies"));
    }

    /**
     *
     * @param command
     * @param users
     * @param movies
     * @param output
     */
    public void like(final Actions command, final LinkedList<Users> users,
                     final LinkedList<Movies> movies, final ArrayNode output) {
        // only on SeeDetails page
        if (!currentAuth.getCurrentPage().getPageType().equals("see details")) {
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.putPOJO("error", "Error");
            objectNode.putPOJO("currentMoviesList", new ArrayList<>());
            objectNode.putPOJO("currentUser", null);
            output.addPOJO(objectNode);
            return;
        }
        String objectType = command.getObjectType();
        String movieName;

        if (objectType != null) {
            movieName = command.getMovie();
        } else {
            movieName = currentAuth.getCurrentMoviesList().get(0).getName();
        }


        // check if you watched the movie
        for (int i = 0; i < currentAuth.getCurrentUser().getWatchedMovies().size(); i++) {
            String watchedMovieName = currentAuth.getCurrentUser().getWatchedMovies().get(i).getName();
            if (watchedMovieName.equals(movieName)) {
                // increase number of likes
                int numLikes;
                numLikes = currentAuth.getCurrentUser().getPurchasedMovies().get(i).getNumLikes();
                currentAuth.getCurrentUser().getWatchedMovies().get(i).setNumLikes(numLikes + 1);

                // update current user likes list
                Movies movie = currentAuth.getCurrentUser().getWatchedMovies().get(i);
                currentAuth.getCurrentUser().getLikedMovies().add(movie);

                // output message
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.putPOJO("error", null);
                ArrayList<Movies> currentMoviesList = new ArrayList<>();
                for (int j = 0; j < currentAuth.getCurrentMoviesList().size(); j++) {
                    movie = new Movies(currentAuth.getCurrentMoviesList().get(j));
                    currentMoviesList.add(movie);
                }
                objectNode.putPOJO("currentMoviesList", currentMoviesList);
                objectNode.putPOJO("currentUser", new Users(currentAuth.getCurrentUser()));
                output.addPOJO(objectNode);
                return;
            }
        }

        // output message for Error
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("error", "Error");
        objectNode.putPOJO("currentMoviesList", new ArrayList<>());
        objectNode.putPOJO("currentUser", null);
        output.addPOJO(objectNode);
        currentAuth.setCurrentPage(PAGE_TYPE.type("movies"));
    }

    /**
     *
     * @param command
     * @param output
     */
    public void seeDetailsMovies(final Actions command, final ArrayNode output) {
        String movie = command.getMovie();

        for (int j = 0; j < currentAuth.getCurrentMoviesList().size(); j++) {
            if (currentAuth.getCurrentMoviesList().get(j).getName().equals(movie)) {

                LinkedList<Movies> currentMoviesList = new LinkedList<>();
                Movies newMovie = currentAuth.getCurrentMoviesList().get(j);
                currentMoviesList.add(newMovie);
                currentAuth.setCurrentMoviesList(currentMoviesList);

                // output message
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.putPOJO("error", null);

                LinkedList<Movies> moviesListToPrint = new LinkedList<>();
                moviesListToPrint.add(new Movies(newMovie));

                objectNode.putPOJO("currentMoviesList", moviesListToPrint);
                objectNode.putPOJO("currentUser", new Users(currentAuth.getCurrentUser()));
                output.addPOJO(objectNode);
                return;
            }
        }

        // output message for Error
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.putPOJO("error", "Error");
        objectNode.putPOJO("currentMoviesList", new ArrayList<>());
        objectNode.putPOJO("currentUser", null);
        output.addPOJO(objectNode);
        currentAuth.setCurrentPage(PAGE_TYPE.type("movies"));
    }
}
