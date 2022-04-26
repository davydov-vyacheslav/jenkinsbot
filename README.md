# Insight Jenkins bot

## TODO: description, actions

### Run

```
1. Place `application.properties` file near .jar file
  bot.token=<bot token>
  spring.data.mongodb.uri=mongodb://admin:mongo@localhost:9999/jenkinsbot?authSource=admin
2. java -jar .\jenkinsBot-0.0.x-SNAPSHOT.jar 
```


## What's new

- 0.0.3
  - Store repositories in database (mongodb) + added mongo unit tests
  - Added build {status, delete} command test
  - Remastered Add Build Flow to enter data without annoying suffix (`@botname /build add`)
  - Added Database Version Setting to perform Data Migration gracefully in future
  - Added `/start` command to show menu
  - `/help` command now shows content of README.md file
- 0.0.2 
  - Ability to add/delete repo (in-memory)
  - Status output became more user-friendly 
- 0.0.1 
  - PoC of idea

## TODO list

- -----
- Remaster add flow like BotFather have
  - edit repo (only after add polished)
  - ? commands multi-association (to use emoji, like 👷🗑 , 👷🆕 , 👷ℹ️ , 👷📝)
    - make allow to replay build status keyboard to buttons
  - ? do we need to change domain storage format to full link (include schema and port)
- Deploy in Azure as ACI
- update readme file wih description
- Event sourcing
- Intro message. Aka press start or help
- CLIProcessor - don't download same file x4 times
- Database
  - mongo database backups
  - database fallback to local datasource if mongo is unavailable
- Unit Tests
  - fix unit tests :) (`/help`), add missing (`/start`)
  - AddBuildCommandTest / EditBuildCommandTest
  - ? Optimize database-related unit tests by closing connection afterall, not aftereach 
