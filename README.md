# liquibase-playground
Learning Liquibase DB Schema Management

## Overview
Proof-of-concept scala app and PostgreSQL to explore:
- schema management using [liquibase](https://docs.liquibase.com/home.html)
- a simple JDBC persistence layer using [slick](https://scala-slick.org/)
- integration tests using [testcontainers](testcontainers.org) and [scalatest](https://www.scalatest.org/)

## Build
`sbt clean compile`

## Test
`sbt clean test`

Integration tests are implemented using [scalatest](https://www.scalatest.org/).    

Using the [testcontainers](testcontainers.org) framework, an actual PostgreSQL 13.5 container is launched, and the all liquibase changelogs are applied to setup the schema.

Then, the persistence layer tests run against the PostgreSQL testcontainer.

## Run
1. Bring up a local postgreSQL server instance as a docker container.    
`docker-compose -f docker-compose.yaml up -d`


2. Run the application with a `buildVersion` argument that represents the target tag (which could be the build version of the app).    
`sbt "run <buildVersion>"`
