package service;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.*;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.*;
import passoff.model.*;
import passoff.server.TestServerFacade;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;
import com.google.gson.Gson;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

import dataaccess.UserDAO;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import service.ClearService;
import service.RegisterService;


public class PersonalAPITests {

    private UserDAO userDAO;
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private ClearService clearService;
    private RegisterService registerService;
    private LoginService loginService;
    private LogoutService logoutService;
    private ListGameService listGameService;

    @BeforeEach
    void setUp() {
        this.userDAO = new UserDAO();
        this.gameDAO = new GameDAO();
        this.authDAO = new AuthDAO();

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
        this.loginService = new LoginService(authDAO);
        this.logoutService = new LogoutService(authDAO);
        this.listGameService = new ListGameService(gameDAO, authDAO);
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

}

