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
https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAEYAdAAZM9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATG43N0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weURraDAjrOgSuEsh6uqZLG-rTiGzHmpGHLRjAInxnK2HJlBHblChPLZrmmD-iCMHHFcUyMSsXzTrOzYTJM35XjphTZD2MD9oOvQDMRowLpOfQmY2zbjq2fR7Nxy6rt4fiBF4KDoHuB6+Mwx7pJkmC2ReRTUNe0gAKK7ml9Rpc0LQPqoT7dJ5c7tr+ZxAgBHlBqZ84Tn5WnKWVWHwVFvrIa1YBoRimEKRq-GkjA5JgCJAbVV5aCkUyboUeU1ExkGdFhMV6ChmR02sclTryjAYnaDxiZ8WtAkcCg3DCUGo1xtok1mhGhSWtIp0UoYcl7VhnbaapHUaQgeaNZ2P5XP50AwJQ3hQKVnaJWAfYDkOS6cEF66BJCtq7tCMAAOKjqysWngl57MLp16Y1luX2KORVjSVP5sp9pZVXW43jL5kEVU1CnwdC2OjKoyHczjXUYft2GHVNAkwCAqQoCADYiTVl0zuN2LGJ2EnuljWU7dTK3vThR0DUNPNqLCN3kVJs08rawDKoNOOreLkm6eCe4YxAABmWP23rSns6pAu8z9f3swDVlXH0FO8yzFT9JHKAAJLSCzTi9gAzAALE8J6ZAaFZVl8OgIKADa58B+dPHHAByZdTH5jSWSl1ldjkMP2XDTlx6o0ex6OifJ2nmdTNn+oufctdPIXxel655dTFXNd9HXCMrp4wUbtgPhQNg3DwEJhjGykcVni3bKA5UtQNOTlPBDrz69PPowN8cvupuHD9j4vbOpqfnOS3vxuwjgP-UcQssQiz6gbDWUsZZy1vorGqKsdBq36hrUm9RtZMxpr1fWjsoHANGLCd+Zt1r3UojJNAKBkh21GDAZIGRUjUJQNXcU6tQ7sUYYncBG16ZAK9JkY2QcGohw2mfHuow+7lBThnJ+HYNrQ1ho5MRCck6SIHv5MwiNV7IwCJYU6CEqEACkIA8i9jQgIk9ZYExPsTNM1RKR3haHHKmmD0BDm3sAXRUA4AQAQlAWYcdE4yLKvTfo7jPHeN8SzPoAB1Fg8dsotAAEK7gUHAAA0l8AJKjnBqKEd-Z220ABWxi0AAKMTyARKA0TdS4WLW65RoGy3luNeBytNBII2urcoaCMFKywbBCBuD8JgAAVk4hLFSGW15HHIUYQskO1umwracFTEoEFFwnBizyiUmwFofho5YTG3EigzsloYBVHsas+SAzuGNXKOU0pIC1CaW0ksgCwNYBg2gJDORhMFHw24holea4QoBC8B4rsXpYDAGwNvQg8REiH3xtDH+jdygVHSplbKuVjA-L-Hc0sX9oJsWWQ07geBGR6AMIA8lUBKX6BQKAnqNy6nhjJVCuSCgaimwWebSZMBITDAgDQHattlpoA0HrVleFJa0tolynlrCLbpiFSKm2CAGIdEldg6VEsQC0qOQq8Zkl+WRFVYYTuvL1oFJWTmcMGz8V+0hRS7QVLGXPN+nk6CNigY-NOPItuij1FAswEAA




```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
