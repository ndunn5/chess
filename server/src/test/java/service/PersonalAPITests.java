package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import extramodel.JoinGameRequest;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class PersonalAPITests {

    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;

    private ClearService clearService;
    private RegisterService registerService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ListGameService listGameService;
    private CreateGameService createGameService;
    private JoinGameService joinGameService;

    @BeforeEach
    void setUp() {
        this.userDAO = new InMemoryUserDAO();
        this.gameDAO = new InMemoryGameDAO();
        this.authDAO = new InMemoryAuthDAO();

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
        this.loginService = new LoginService(authDAO);
        this.logoutService = new LogoutService(authDAO);
        this.listGameService = new ListGameService(gameDAO, authDAO);
        this.createGameService = new CreateGameService(gameDAO, authDAO);
        this.joinGameService = new JoinGameService(gameDAO, authDAO);
    }

    @Test
    @DisplayName("Clear Service Positive")
    public void clearServicePositive() throws DataAccessException {
        UserData testUser = new UserData("noah","dunn", "gmail");
        AuthData testData = new AuthData("m1thousandmilesanhour", "noah");
        userDAO.insertUser(testUser);
        gameDAO.insertGame(new GameData(12345, "eden", "beazer", "sewingGame", new ChessGame()));
        authDAO.insertAuth(testData, testUser);


        clearService.clear(new ClearDatabaseRequest());

        assertTrue(userDAO.isEmpty(), "user failed");
        assertTrue(gameDAO.isEmpty(), "game failed");
        assertTrue(authDAO.isEmpty(), "auth failed");
    }

    @Test
    @DisplayName("Register Service Positive")
    public void registerServicePositive() throws DataAccessException {
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        RegisterResult testResult = registerService.register(testRegisterRequest);

        assertNull(testResult.message());
    }

    @Test
    @DisplayName("Register Service Negative")
    public void registerServiceNegative() throws DataAccessException {
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","", "gmail");
        RegisterResult testResult = registerService.register(testRegisterRequest);

        assertNotNull(testResult.message());
    }

    @Test
    @DisplayName("Login Service Positive")
    public void loginServicePositive() throws DataAccessException {
        RegisterService registerService = new RegisterService(userDAO, authDAO);
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        RegisterResult registerResult = registerService.register(testRegisterRequest);

        LoginService loginService = new LoginService(authDAO);
        LoginRequest testLoginRequest = new LoginRequest("noah", "dunn");
        LoginResult testResult = loginService.login(testLoginRequest);

        assertNull(testResult.message());
    }

    @Test
    @DisplayName("Login Service Negative")
    public void loginServiceNegative() throws DataAccessException {
        LoginService loginService = new LoginService(authDAO);
        LoginRequest testLoginRequest = new LoginRequest("noah", "dunn");
        LoginResult testResult = loginService.login(testLoginRequest);

        assertNotNull(testResult.message());
    }

    @Test
    @DisplayName("Logout Service Positive")
    public void logoutServicePositive() throws DataAccessException {
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        registerService.register(testRegisterRequest);
        LoginResult testLoginResult = loginService.login(new LoginRequest(testRegisterRequest.username(),"dunn"));
        LogoutResult testLogoutResult = logoutService.logout(new LogoutRequest(testLoginResult.authToken()));

        assertNull(testLogoutResult.message());
    }

    @Test
    @DisplayName("Logout Service Negative")
    public void logoutServiceNegative() throws DataAccessException {
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        registerService.register(testRegisterRequest);
        LoginResult testLoginResult = loginService.login(new LoginRequest(testRegisterRequest.username(),"funn"));
        LogoutResult testLogoutResult = logoutService.logout(new LogoutRequest(testLoginResult.authToken()));

        assertNotNull(testLogoutResult.message());
    }

    @Test
    @DisplayName("List Games Service Positive")
    public void listGamesPositive() throws DataAccessException {
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        registerService.register(testRegisterRequest);
        LoginResult testLoginResult = loginService.login(new LoginRequest(testRegisterRequest.username(),"dunn"));
        gameDAO.insertGame(new GameData(12345, "jimothy", "asterics", "alabama", new ChessGame()));
        ListGamesResult testListGamesResult = listGameService.listGames(new ListGamesRequest(testLoginResult.authToken()));

        assertNotNull(testListGamesResult.games());
    }

    @Test
    @DisplayName("List Games Service Negative")
    public void listGamesNegative() throws DataAccessException {
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        registerService.register(testRegisterRequest);
        LoginResult testLoginResult = loginService.login(new LoginRequest(testRegisterRequest.username(),"dunn"));
        gameDAO.insertGame(new GameData(12345, "jimothy", "asterics", "alabama", new ChessGame()));
        ListGamesResult testListGamesResult = listGameService.listGames(new ListGamesRequest("wrongAuthToken"));

        assertNotNull(testListGamesResult.message());
    }

    @Test
    @DisplayName("Create Games Service Positive")
    public void createGamesPositive() throws DataAccessException {
        String gameName = "babySharkDooDoo";
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        registerService.register(testRegisterRequest);
        LoginResult testLoginResult = loginService.login(new LoginRequest(testRegisterRequest.username(),"dunn"));
        CreateGameResult testCreateGameResult = createGameService.createGame(new CreateGameRequest(testLoginResult.authToken(), gameName));

        assertNull(testCreateGameResult.message());
    }

    @Test
    @DisplayName("Create Games Service Negative")
    public void createGamesNegative() throws DataAccessException {
        String gameName = "babySharkDooDoo";
        RegisterRequest testRegisterRequest = new RegisterRequest("noah","dunn", "gmail");
        registerService.register(testRegisterRequest);
        LoginResult testLoginResult = loginService.login(new LoginRequest(testRegisterRequest.username(),"dunn"));
        CreateGameResult testCreateGameResult = createGameService.createGame(new CreateGameRequest(gameName, gameName));

        assertNotNull(testCreateGameResult.message());
    }

    @Test
    @DisplayName("Join Game Service Positive")
    public void joinGamePositive() throws DataAccessException {
        registerService.register(new RegisterRequest("noah", "dunn", "gmail"));
        LoginResult loginResult = loginService.login(new LoginRequest("noah", "dunn"));
        CreateGameResult gameResult = createGameService.createGame(new CreateGameRequest(loginResult.authToken(), "babySharkDooDoo"));
        JoinGameRequest testJoinGameRequest = new JoinGameRequest("WHITE", gameResult.gameID());
        testJoinGameRequest.addAuthToken(loginResult.authToken());
        JoinGameResult joinResult = joinGameService.joinGame(testJoinGameRequest);

        assertNull(joinResult.message());
    }

    @Test
    @DisplayName("Join Game Service Negative")
    public void joinGameNegative() throws DataAccessException {
        registerService.register(new RegisterRequest("noah", "dunn", "gmail"));
        LoginResult loginResult = loginService.login(new LoginRequest("noah", "dunn"));
        CreateGameResult gameResult = createGameService.createGame(new CreateGameRequest(loginResult.authToken(), "babySharkDooDoo"));
        JoinGameRequest testJoinGameRequest = new JoinGameRequest("WHITE", gameResult.gameID());
        JoinGameResult joinResult = joinGameService.joinGame(testJoinGameRequest);

        assertNotNull(joinResult.message());
    }

    @Test
    @DisplayName("getUser Positive")
    public void getUserPositive() throws DataAccessException {
        registerService.register(new RegisterRequest("noah", "dunn", "gmail"));
        UserData testUserData = userDAO.getUser("noah");
        assertNotNull(testUserData);
    }

    @Test
    @DisplayName("getUser Negative")
    public void getUserNegative() throws DataAccessException {
        UserData testUserData = userDAO.getUser("noah");
        assertNull(testUserData);
    }

    @Test
    @DisplayName("Clear User")
    public void clearUser() throws DataAccessException {
        registerService.register(new RegisterRequest("noah", "dunn", "gmail"));
        userDAO.clear();
        assertTrue(userDAO.isEmpty());
    }

    @Test
    @DisplayName("User isEmpty Positive")
    public void userIsEmptyPositive() throws DataAccessException {
        registerService.register(new RegisterRequest("noah", "dunn", "gmail"));
        userDAO.clear();
        assertTrue(userDAO.isEmpty());
    }

    @Test
    @DisplayName("User isEmpty Negative")
    public void userIsEmptyNegative() throws DataAccessException {
        registerService.register(new RegisterRequest("noah", "dunn", "gmail"));
        assertFalse(userDAO.isEmpty());
    }

    @Test
    @DisplayName("insertUser Positive")
    public void insertUserPositive() throws DataAccessException {
        userDAO.insertUser(new UserData("noah", "dunn", "email"));
        assertNotNull(userDAO.getUser("noah"));
    }

    @Test
    @DisplayName("insertUser Negative")
    public void insertUserNegative() throws DataAccessException {
        assertNull(userDAO.getUser("noah"));
    }

    @Test
    @DisplayName("insertGame Positive")
    public void insertGamePositive() throws DataAccessException {
        gameDAO.insertGame(new GameData(12242000, null, null, "testGame", new ChessGame()));
        assertFalse(gameDAO.isEmpty());
    }

    @Test
    @DisplayName("insertGame Negative")
    public void insertGameNegative() throws DataAccessException {
        gameDAO.insertGame(new GameData(100, null, null, "testGame", new ChessGame()));
        assertNull(gameDAO.getGame(4000));
    }

    @Test
    @DisplayName("getGame Positive")
    public void getGamePositive() throws DataAccessException {
        gameDAO.insertGame(new GameData(12242000, null, null, "testGame", new ChessGame()));
        assertNotNull(gameDAO.getGame(12242000));
    }

    @Test
    @DisplayName("getGame Negative")
    public void getGameNegative() throws DataAccessException {
        gameDAO.insertGame(new GameData(100, null, null, "testGame", new ChessGame()));
        assertNull(gameDAO.getGame(4000));
    }

    @Test
    @DisplayName("updateGame Positive")
    public void updateGamePositive() throws DataAccessException {
        GameData originalGameData = new GameData(12242000, null, null, "testGame", new ChessGame());
        GameData updatedGameData = new GameData(12242000, "germany", "denmark", "testGame", originalGameData.game());
        gameDAO.insertGame(originalGameData);
        gameDAO.updateGame(updatedGameData);
        assertNotEquals(originalGameData, gameDAO.getGame(12242000));
    }

    @Test
    @DisplayName("updateGame Negative")
    public void updateGameNegative() throws DataAccessException {
        GameData originalGameData = new GameData(12242000, null, null, "testGame", new ChessGame());
        gameDAO.insertGame(originalGameData);
        assertEquals(originalGameData, gameDAO.getGame(12242000));
    }

    @Test
    @DisplayName("returnAllGames Positive")
    public void returnAllGamesPositive() throws DataAccessException {
        GameData firstGameData = new GameData(12242000, null, null, "testGame", new ChessGame());
        GameData secondGameData = new GameData(15, "germany", "denmark", "testGame", new ChessGame());
        gameDAO.insertGame(firstGameData);
        gameDAO.insertGame(secondGameData);
        assertNotNull(gameDAO.returnAllGames());
    }

    @Test
    @DisplayName("returnAllGames Negative")
    public void returnAllGamesNegative() throws DataAccessException {
        assertEquals(new ArrayList<>(), gameDAO.returnAllGames());
    }

    @Test
    @DisplayName("Clear games")
    public void clearGames() throws DataAccessException {
        gameDAO.insertGame(new GameData(12555, "testWhiteUsername", "testBlackUsername", "testName", new ChessGame()));
        gameDAO.clear();
        assertTrue(gameDAO.isEmpty());
    }


    @Test
    @DisplayName("Game isEmpty Positive")
    public void gameIsEmptyPositive() throws DataAccessException {
        gameDAO.insertGame(new GameData(12555, "testWhiteUsername", "testBlackUsername", "testName", new ChessGame()));
        gameDAO.clear();
        assertTrue(gameDAO.isEmpty());
    }

    @Test
    @DisplayName("Game isEmpty Negative")
    public void gameIsEmptyNegative() throws DataAccessException {
        gameDAO.insertGame(new GameData(12555, "testWhiteUsername", "testBlackUsername", "testName", new ChessGame()));
        assertFalse(gameDAO.isEmpty());
    }

    @Test
    @DisplayName("insertAuth Positive")
    public void insertAuthPositive() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertFalse(authDAO.isEmpty());
    }

    @Test
    @DisplayName("insertAuth Negative")
    public void insertAuthNegative() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertNull(authDAO.getAuthDataWithAuthToken("noah"));
    }

    @Test
    @DisplayName("getUserDataWithUsername Positive")
    public void getUserDataWithUsernamePositive() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertNotNull(authDAO.getUserDataWithUsername("username"));
    }

    @Test
    @DisplayName("getUserDataWithUsername Negative")
    public void getUserDataWithUsernameNegative() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertNull(authDAO.getUserDataWithUsername("noah"));
    }

    @Test
    @DisplayName("getAuthDataWithAuthToken Positive")
    public void getAuthDataWithAuthTokenPositive() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertNotNull(authDAO.getAuthDataWithAuthToken("authtoken"));
    }

    @Test
    @DisplayName("getAuthDataWithAuthToken Negative")
    public void getAuthDataWithAuthTokenNegative() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertNull(authDAO.getAuthDataWithAuthToken("noah"));
    }

    @Test
    @DisplayName("deleteAuth Positive")
    public void deleteAuthPositive() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        authDAO.deleteAuth(new AuthData("authtoken","username" ), new UserData("username", "password", "email"));
        assertNull(authDAO.getAuthDataWithAuthToken("authtoken"));
    }

    @Test
    @DisplayName("deleteAuth Negative")
    public void deleteAuthNegative() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertNotNull(authDAO.getAuthDataWithAuthToken("authtoken"));
    }

    @Test
    @DisplayName("Auth isEmpty Positive")
    public void authIsEmptyPositive() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        authDAO.clear();
        assertTrue(authDAO.isEmpty());
    }

    @Test
    @DisplayName("Auth isEmpty Negative")
    public void authIsEmptyNegative() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        assertFalse(authDAO.isEmpty());
    }

    @Test
    @DisplayName("Clear Auth")
    public void clearAuth() throws DataAccessException {
        authDAO.insertAuth(new AuthData("authtoken", "username"), new UserData("username", "password", "email"));
        authDAO.clear();
        assertTrue(authDAO.isEmpty());
    }


}

