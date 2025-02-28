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

    @BeforeEach
    void setUp() {
        this.userDAO = new UserDAO();
        this.gameDAO = new GameDAO();
        this.authDAO = new AuthDAO();

        this.clearService = new ClearService(userDAO, gameDAO, authDAO);
        this.registerService = new RegisterService(userDAO, authDAO);
    }

    @Test
    @DisplayName("Clear Service Positive")
    public void clearServicePositive() throws DataAccessException {
        userDAO.insertUser(new UserData("noah","dunn", "gmail"));
        gameDAO.insertGame(new GameData(12345, "eden", "beazer", "sewingGame", new ChessGame()));
        authDAO.insertAuth(new AuthData("m1thousandmilesanhour", "theGorillaz"));

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

}

