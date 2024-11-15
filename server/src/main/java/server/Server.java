package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.*;
import model.*;
import service.GameService;
import service.UserService;
import spark.*;

import java.util.Collection;

public class Server {

    MySQLAuthDAO authDAO = new MySQLAuthDAO();
    MySQLUserDAO userDAO = new MySQLUserDAO();
    MySQLGameDAO gameDAO = new MySQLGameDAO();

    private final UserService userService = new UserService(userDAO,authDAO);
    private final GameService gameService = new GameService(gameDAO,authDAO);

    public Server() {
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        //UserService endpoints
        Spark.delete("/db",this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session",this::logout);
        //GameService endpoints
        Spark.post("/game",this::createGame);
        Spark.put("/game",this::joinGame);
        Spark.get("/game",this::listGames);
        //This line initializes the server and can be removed once you have a functioning endpoint 
//        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        try {
            userService.clear();
            gameService.clear();
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(401);
            return "{\"message\": \"Error: (description of error)\"}";
        }
    }

    private Object register(Request req, Response res) {
        UserData userData = new Gson().fromJson(req.body(),UserData.class);
        //call service to register
        try{
            AuthData authData = userService.register(userData);
            res.status(200);
            return new Gson().toJson(authData);
        } catch(BadRequestException e) {
            res.status(400);
            return "{\"message\": \"Error: bad request\"}";
        } catch (DataAccessException e) {
            res.status(500);
            return "{\"message\": \"Error: (description of error)\"}";
        } catch(AlreadyTakenException e) {
            res.status(403);
            return "{\"message\": \"Error: already taken\"}";
        }
    }

    private Object login(Request req, Response res) {
        UserData userData = new Gson().fromJson(req.body(),UserData.class);
        //call service to login
        try{
            AuthData authData = userService.loginUser(userData);
            res.status(200);
            return new Gson().toJson(authData);
        } catch(DataAccessException e) {
            res.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        }
    }

    private Object logout(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            userService.logoutUser(authToken);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            res.status(500);
            return "{\"message\": \"Error: (description of error)\"}";
        } catch(UnauthorizedException e) {
            res.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        }
    }

    private Object createGame(Request req, Response res) {
        GameData gameData = new Gson().fromJson(req.body(), GameData.class);
        String authToken = req.headers("authorization");
        try {
            int gameID = gameService.createGame(authToken,gameData);
            res.status(200);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("gameID",gameID);
            return jsonObject.toString();
        } catch(DataAccessException e) {
            res.status(500);
            return "{\"message\": \"Error: (description of error)\"}";
        } catch(UnauthorizedException e) {
            res.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        } catch (BadRequestException e) {
            res.status(400);
            return "{\"message\": \"Error: bad request\"}";
        }
    }

    private Object joinGame(Request req, Response res) {
        String authToken = req.headers("authorization");
        record JoinGameData(String playerColor, int gameID) {}
        JoinGameData joinGameData = new Gson().fromJson(req.body(),JoinGameData.class);

        try {
            String playColor = joinGameData.playerColor == null ? null : (joinGameData.playerColor).toUpperCase();
            gameService.joinGame(authToken, playColor, joinGameData.gameID);
            res.status(200);
            return "{}";
        } catch(BadRequestException e) {
            res.status(400);
            return "{\"message\": \"Error: bad request\"}";
        } catch(UnauthorizedException e) {
            res.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        } catch(AlreadyTakenException e) {
            res.status(403);
            return "{\"message\": \"Error: already taken\"}";
        } catch(DataAccessException e) {
            res.status(500);
            return "{\"message\": \"Error: (description of error)\"}";
        }
    }

    private Object listGames(Request req, Response res) {
        String authToken = req.headers("authorization");
        try {
            Collection<GameData> games = gameService.listGames(authToken);
            res.status(200);
            return new Gson().toJson(new ListGameResponse(games));
        } catch (UnauthorizedException e) {
            res.status(401);
            return "{\"message\": \"Error: unauthorized\"}";
        } catch (DataAccessException e) {
            res.status(500);
            return "{\"message\": \"Error: (description of error)\"}";
        }
    }
}
