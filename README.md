# Kry - Service Poller

## Instructions

### Docker

You need to have docker and docker-compose installed.

Start the MySQL server by running:
`docker-compose up -d`

### Gradle
You need to have Gradle and a JDK installed.

In the root folder of the project run:
`./gradlew build` and then `./gradlew run`

### Npm
You need to have node and npm installed.

From the root folder you can run:
`cd src/main/ui && npm install` and then `npm start`to start the ui.

If there are any errors you might need to install any of the following packages:

- @emotion/react,
- @emotion/styled,
- @mui/material",
- @mui/x-data-grid",
- axios

You can do that by for example:
`npm install axios`

## Ports

- SQL: 3309
- Vertx Http Server: 8080
- React ui: 3000
