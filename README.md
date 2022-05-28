<h1 align="center">
  <br>
  <a href="https://github.com/sopra-fs22-group-14"><img src="src/CAH_Logo.png" alt="Cards Against Humanity" width="300"></a>
  <br>
  Cards Against Humanity - Server
  <br>
</h1>

<p align="center">
   <a href="https://github.com/sopra-fs22-group-14/server/actions">
     <img src="https://github.com/sopra-fs22-group-14/server/workflows/Deploy%20Project/badge.svg" alt="Deployment Status">
   </a>
   <a href="https://sonarcloud.io/project/overview?id=sopra-fs22-group-14_server">
      <img src="https://sonarcloud.io/api/project_badges/measure?project=sopra-fs22-group-14_server&metric=alert_status" alt="Quality Gate Status">
  </a>
  <a>
      <img src="https://sonarcloud.io/api/project_badges/measure?project=sopra-fs22-group-14_server&metric=coverage" alt="Test Coverage">
  </a>
</p>

## Introduction

Cards Against Humanity is a fill-in-the-blank party game that turns your awkward personality and lackluster social skills into hours of fun! It is a game usually played in person, but we wanted to make this fun available to anyone, anywhere and at anytime - even if you cannot be physically together!

This repository is the back-end bit of our application!

## Technologies

The back-end is written in Java using Spring-Boot as a framework.

To communicate with the client, REST is used. To ensure that all clients have the relevant information at hand, we make use of polling and therefore the front-end hits different enpoints in defined time intervals.

Since we have a user system containing records of previous games, we use PostgreSQL as a persistent database to store the data.

## High-Level Components

TODO

## Launch & Development

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/) or [Visual Studio Code](https://code.visualstudio.com/)) and make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

### Build

```bash
./gradlew build
```

### Run

```bash
./gradlew bootRun
```

### Test

```bash
./gradlew test
```

### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### Database setup to see our schema/persistent data

Since we use a persistent non-volatile database from Postgres and run the DB-instance on AWS, there are some further steps necessary to see the persistent data:
The following [article](https://docs.aws.amazon.com/AmazonRDS/latest/UserGuide/USER_ConnectToPostgreSQLInstance.html) will guide you through the necessary steps.

Sidenote: To gain access when developing, the environment variables with the corresponding credentials must have been set locally

### Useful links to ge started with Spring Boot

-   Documentation: https://docs.spring.io/spring-boot/docs/current/reference/html/index.html
-   Guides: http://spring.io/guides
    -   Building a RESTful Web Service: http://spring.io/guides/gs/rest-service/
    -   Building REST services with Spring: http://spring.io/guides/tutorials/bookmarks/

## Illustrations

TODO add pictures with description

## Roadmap

The following things could be implemented in the future:

   1. New endpoint for the round ending in the Community Mode, so that the played cards and the times chosen can be associated to the users
   2. Include a chat feature to enable communication during the game
   3. Develop a mobile version of the game, so that it can be played on mobile devices too

## Authors and Acknowledgements

<h3>Members of Group 14 (SoPra 2022):</h3>

[Diego Bugmann](https://github.com/diegobugmann), [Szymon Kaczmarski](https://github.com/Szymskiii), [Alexander Lerch](https://github.com/lerchal1) and [Ege Onur Güleç](https://github.com/ogegulec16)

<h3>Acknowledgements</h3>

We would like to thank our tutor [Kyrill Hux](https://github.com/realChesta), who was always able to support us when we had any concerns. His support resulted in an application that is more user-friendy and also more secure :)

## License

[Apache License 2.0](LICENSE)
