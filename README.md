# liquibase-playground
Learning Liquibase DB Schema Management

## Build
`sbt clean compile`

## Run
First, bring up a local postgreSQL server instance as a docker container.    
`docker-compose -f docker-compose.yaml up -d`

Then, run the application with a `buildVersion` argument that represents the target tag (which could be the build version of the app).    
`sbt "run <buildVersion>"`
