package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.Collection;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData userData) throws Exception {
        var path = "/user";
        return makeRequest("POST", path, userData, AuthData.class,null);
    }

    public void clearAll() throws Exception {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    //maybe change response class
    public Collection<GameData> listGames(String authToken) throws Exception{
        var path = "/game";
        return makeRequest("GET", path, null, Collection.class, authToken);
    }

    public AuthData login(UserData userData) throws Exception {
        var path = "/session";
        return makeRequest("POST", path, userData, AuthData.class, null);
    }

    public void logout(String authToken) throws Exception {
        var path = "/session";
        makeRequest("DELETE", path, null, null, authToken);
    }

    public int createGame(String authToken, GameData gameData) throws Exception {
        var path = "/game";
        return makeRequest("POST", path, gameData, Integer.class, authToken);
    }

    public void joinGame(Integer gameID, String playerColor, String authToken) {
//        var path = "/game";
//        makeRequest("PUT", path, null, null,authToken);
//        return "";
    }

    public void observeGame(String authToken, int gameIndex) throws Exception {
        var path = "/game";
        makeRequest("PUT", path, null, null, authToken);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws Exception {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            if(authToken != null && !authToken.isEmpty()) {
                http.setRequestProperty("Authorization", authToken);
            }
            //only write the body if request is not null
            if(request != null) {
                http.setDoOutput(true);
                writeBody(request, http);
            }
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch(Exception e) {
            throw new Exception();
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if(request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, Exception {
        var status = http.getResponseCode();
        if(!(status / 100 == 2)) {
            throw new Exception();
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if(http.getContentLength() < 0) {
            try(InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if(responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }
}
