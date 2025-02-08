# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

## Phase 2 Sequence Diagram
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=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




```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
