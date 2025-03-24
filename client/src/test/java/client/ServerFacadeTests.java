package client;

import dataaccess.UserDAO;
import exception.ResponseException;
import extramodel.JoinGameRequest;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }
    @BeforeEach
    public void setup(){
        try{
            facade.handleClearDatabase();
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("Clear Database")
    public void clearDatabase() {
        try{
            facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            facade.handleClearDatabase();
            Assertions.assertThrows(ResponseException.class, () -> facade.handleLogin(new LoginRequest("testUserName", "testPassword")));
        } catch (ResponseException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Register Positive")
    public void registerPositive() {
        assertDoesNotThrow(() -> {
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
        });
    }

    @Test
    @DisplayName("Register Negative")
    public void registerNegative() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            facade.handleLogout(new LogoutRequest(registerResult.authToken()));
            Assertions.assertThrows(ResponseException.class, () -> facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail")));
        }catch(ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Login Positive")
    public void loginPositive() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            facade.handleLogout(new LogoutRequest(registerResult.authToken()));
            assertDoesNotThrow(() -> {
                LoginResult loginResult = facade.handleLogin(new LoginRequest("testUserName", "testPassword"));
            });
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Login Negative")
    public void loginNegative() {
            Assertions.assertThrows(ResponseException.class, () -> facade.handleLogin(new LoginRequest("testUserName", "testPassword")));
    }

    @Test
    @DisplayName("Logout Positive")
    public void logoutPositive() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            assertDoesNotThrow(() -> {
                facade.handleLogout(new LogoutRequest(registerResult.authToken()));
            });
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Logout Negative")
    public void logoutNegative() {
        Assertions.assertThrows(ResponseException.class, () -> facade.handleLogout(new LogoutRequest("fakeAuthToken")));
    }

    @Test
    @DisplayName("List Games Positive")
    public void listGamesPositive() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            assertDoesNotThrow(() -> {
                facade.handleListGames(new ListGamesRequest(registerResult.authToken()));
            });
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("List Games Negative")
    public void listGamesNegative() {
        Assertions.assertThrows(ResponseException.class, () -> facade.handleListGames(new ListGamesRequest("fakeAuthToken")));
    }

    @Test
    @DisplayName("Join Game Positive")
    public void joinGamePositive() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            CreateGameResult createGameResult = facade.handleCreateGame(new CreateGameRequest(registerResult.authToken(), "testGame"));
            assertDoesNotThrow(() -> {
                JoinGameRequest joinGameRequest = new JoinGameRequest("WHITE", createGameResult.gameID());
                joinGameRequest.addAuthToken(registerResult.authToken());
                facade.handleJoinGame(joinGameRequest);
            });
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Join Game Negative")
    public void joinGameNegative() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            CreateGameResult createGameResult = facade.handleCreateGame(new CreateGameRequest(registerResult.authToken(), "testGame"));
            Assertions.assertThrows(ResponseException.class, () -> facade.handleJoinGame(new JoinGameRequest("white", createGameResult.gameID())));
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Create Game Positive")
    public void createGamePositive() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            assertDoesNotThrow(() -> {
                CreateGameResult createGameResult = facade.handleCreateGame(new CreateGameRequest(registerResult.authToken(), "testGame"));
            });
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Create Game Negative")
    public void createGameNegative() {
        try{
            RegisterResult registerResult = facade.handleRegister(new RegisterRequest("testUserName", "testPassword", "testEmail"));
            Assertions.assertThrows(ResponseException.class, () -> facade.handleCreateGame(new CreateGameRequest("fakeAuthToken", "testGame")));
        } catch (ResponseException e){
            throw new RuntimeException(e);
        }
    }

}
