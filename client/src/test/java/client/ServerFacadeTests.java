package client;

import dataaccess.UserDAO;
import exception.ResponseException;
import model.LoginRequest;
import model.RegisterRequest;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

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

}
