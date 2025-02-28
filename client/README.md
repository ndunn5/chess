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
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAEYAdAAZM9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATG43N0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weURraDAjrOmxTryjAABqwDIFomQwFU+ivEsPGJgSuEsh6uqZLG-rTiGzHmpGHLRjAqnxnK2HJlBHblChPLZrmmD-iCMHHFcUyMSsXzTrOzYTJM35XrZhTZD2MD9oOvQDMRowLpOfSuY2zbjq2fR7Nxy6rt4fiBF4KDoHuB6+Mwx7pJkmB+ReRTUNe0gAKK7uV9Tlc0LQPqoT7dFFc7tr+ZxAgBkVBm584TvF1kme1WHwdlvrIWNYBoRimGGRqCmkjA5JgKpAY9dFaCkUyboUeU1ExkGdFhC16ChmRO2sSV-FwTA6naLJ2HyedikcCg3AqUGa1xtoW1mhGhSWtIb0UoY+n3VhnY2WZk2WQgeZDZ2P5XAl0AwJQ3hQG1nZFWAfYDkOS6cMl66BJCtq7tCMAAOKjqyeWnoV57MHZ15U9VdX2KOzXra1P5slDpbdXWG3jHFkGdcNhnwdCNOjKoyEy7T00YQ983PYty2rSdm1ndtLEA5R+16YdXH0drut-YjUu3Sb8iqzh6vuktFKy2osK-eR2l7TytoiQgS20xbnt2eCe6UxAABm1OBxDl0CxTsSu6osPwxLVulWm-Sc3LosVFno4AJLSKLTi9gAzAALE8J6ZAaFZVl8OgIKADZ18BDdPNnKAAHLt1M8WNF5GfY0zeNBfnOflHnfRd0XJfl1XUw1-qoX3P3TxNy3bdhR3Uxd73O-93sg9mETngpRu2A+FA2DcPAymGK7KT5WeOTM3x161A0HNc8EPPoEOfeo4h7HGMhLK4M9RwHzXn0AaNk2QjRgJ6PUrtYRwAfq7ZWWJ7ZPT1hrCkWt-4600v9dk3teRg3kEKY6RCg4XRDgJO6dscEyAWk7ZBmRUFANGB7C6Btyi6TQCgZIAdRgwGSBkVIoie6jjoSxBhN1Z7SHtmA1M5R0Fek4aOFOg006XSRoLJR89K4gI7JdHGY8hyQNGHPcopcTGJTPmuVKARLBvQQiIgAUhAHk0cxEBE3iABsjM34IO8lPKolI7wtC7tzYWc4hy32AG4qAcAIAISgLMJRpj2oC36EklJaSMmiz6AAdRYAXGqLQABCu4FBwAANJfCMXYhe4tUxhPYjAAAVj4tAqDvE8kwSgNEM0WEkPwitT62teH6zIVRH2xtvpULNrQkh6drrlCYcAcZbDJlcMLtIWZWl+ELN5F3ah0ii5yK0go8ortBQsIhuo1IKAgnwHSajGIt0qC-F0NwFRuDLblEpNgMSKBUEPJ+jciMXsJJRL8SgAysFIZDXKIM-p2i1BWXgfo8JpYUawHRtALG5jR4BXxr0BKpgkrnxJgELwySuxelgMAbAt9CDxESM-BmONOklCnhVKqNU6rGFJX+NFBLdEdLuUg7geBGR6AMGg+VUBFX6BQFg2aKKHZ4PYaq-SCgajuxhbtGAkJhgQBoLdZUNt4noA0LHIF4ZyggFVbRI1Jr1lwsiJa61fsGIdEdXNXVf1XWqoeZ645pDLTpj9YYLuGhvWypzOGQFHU1HMoVdoJVmrsVw2ldBFmmcckjzfpYql3FT4riAA



```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
