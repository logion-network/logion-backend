[![Test](https://github.com/logion-network/logion-backend/actions/workflows/maven.yml/badge.svg)](https://github.com/logion-network/logion-backend/actions/workflows/maven.yml)

# logion-backend
The Logion off-chain backend handles data which should not (or must not) be stored on-chain. It exposes a REST API
and is implemented using Spring Boot.

## Quick start
First, run a PostgreSQL 12 server:

`docker run --name logion-postgres -e POSTGRES_PASSWORD=secret -p 5432:5432 postgres:12`

(or `docker start -a logion-postgres` if you already executed the above command).

Then, if not already done, create your own configuration file by executing

`cp application.properties.sample application.properties`

The sample configuration may be used out of the box with the above setup.

Finally, run the backend with:

`mvn spring-boot:run`

## Living documentation
The `logion.backend.annotation` package contains annotations used to document patterns, concepts, conventions used
in the code.

## Domain-Driven Design
Domain-Driven Design (DDD) is used to model Logion's domain.
The `logion.backend.model` package contains the aggregates of Logion model.

## Coverage report
In order to generate the JaCoCo coverage report, run

`mvn clean verify`

The report is generated under `target/site/jacoco/index.html`

## Code quality
It is recommended to install the [SonarLint plugin](https://www.sonarlint.org/) in your IDE in order to get
real-time feedback on potential bugs, security issues, etc. The project should be analyzed on a regular basis
e.g., before each push.
