# Igor

Igor is a home assistant app, or at least will be. In early stages right now.

Currently, project right now is the backend API's that will support the Igor UI.

## Build

This is set up as a Gradle project, to build just run

`./gradlew build`

From the project root.

## Security

This project uses a JWT token based authorization.

## Run

To run a local instance:

`./gradlew bootRun`

A Swagger UI will be running on http://localhost:8080/swagger-ui.html with the OpenAPI spec
on http://localhost:8080/spec

## Configuration

Configuration settings are driven by the Spring Boot [application.yml](src/main/resources/application.yml)
file. Each setting has an associated environment variable that can be used to override the default. I won't list all of
the settings here, you are smart enough to read the file. But if some settings are not too clear I will make notes in
the property file.

## Roadmap

* Security!
  * ~~enable JWT security on API endpoints~~
  * add user storage
  * add user management (create, delete, update)
* Temperature
  * ~~keep a record of temps.~~
  * add a threshold and alarm if over.
  * run scheduled reports on temp trends.
* Grocery
  * Inventory
  * Shopping lists
* Menu
  * Suggested favorites
  * Check against grocery inventory to make sure enough is on hand
* Games
  * Tic Tac Toe
  * Checkers
  * Chess
* More unit and integration tests! (Is there EVER enough?)


