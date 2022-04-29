# Insight Jenkins bot

## TODO: description, actions

### Flow: /build

- Main Action List (`/build`)
  - List of Public/owned build infos. On Click -> get BuildInfo status (shortcut to `/build status <buildInfo>`)
  - Modify my items -> (see `Modify My BuildInfos`)
- Modify My BuildInfos (shortcut - `/build my_list`)
  - List of owned bi. On click -> modify (-> (see `Edit Flow`))
  - Add -> (see `Add Flow`)
  - Back to action list -> (see `Main Action List`)
- BuildInfo status (shortcut to `/build status <buildInfo>`)

### Developer info: Run

1. Place `application.properties` file near .jar file
```
bot.token=<bot token>
spring.data.mongodb.uri=mongodb://admin:password@localhost:9999/jenkinsbot?authSource=admin
```
2. `java -jar .\jenkinsBot-0.0.x-SNAPSHOT.jar`

## TODO list

TODO in 0.0.5:

- Cli processor:
  - get rid of external utilities' usage: curl . Cleanup CliProcessor
    - need fix authorization
- split /help into: /about, /history, /help
- /stop and /restart commands ?

- -----
- Remaster add flow like BotFather have
  - Во время заполненная информации о сборке указывать что заполнено или что сейчас заполняется
- ? do we need to change domain storage format to full link (include schema and port, e.g. `http://10.172.100.174:7331`)
- Deploy in Azure as ACI
- update readme file wih description
- Event sourcing
- CLIProcessor:
  - ? http://10.172.100.174:7331/job/Insight_72/lastSuccessfulBuild/api/json : 
    - Issues: Test vs TestCases; works only on completed builds
- Database
  - (daily) mongo database backups
  - database fallback to local datasource if mongo is unavailable
- Unit Tests
  - fix unit tests :) (`/help`), add missing (`/start`, `/cancel`)
  - AddBuildCommandTest / EditBuildCommandTest
  - ? Optimize database-related unit tests by closing connection afterall, not aftereach 
