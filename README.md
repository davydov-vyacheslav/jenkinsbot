# Insight Jenkins bot

Основная задача - получение статуса билдов от Дженкиса (состояние билда и кол-во упавших тестов).
Текущая версия заточена на работу с Java/jUnit проектами.

## Flow: /build

- Main Action List (`/build`)
  - List of Public/owned build infos. On Click -> get BuildInfo status (shortcut to `/build status <buildInfo>`)
  - Modify my items -> (see `Modify My BuildInfos`)
- Modify My BuildInfos (shortcut - `/build my_list`)
  - List of owned bi. On click -> modify (-> (see `Edit Flow`))
  - Add -> (see `Add Flow`)
  - Delete
  - Back to action list -> (see `Main Action List`)
- BuildInfo status (shortcut to `/build status <buildInfo>`)
- Add Flow (`/build add`)
  - Edit buttons for fields: RepoName, Publicity, Domain, User, Password, Job
  - Complete creation (validate, save data, and return to `Main Action List`)
  - Cancel creation (cancel and return to `Main Action List`)
- Edit Flow(`/build edit`)
  - Edit buttons for fields: Publicity, Domain, User, Password, Job
  - Apply changes (validate, save data, and return to `Main Action List`)
  - Cancel Editing (cancel and return to `Main Action List`)

## Developer info: Run

1. Place `application.properties` file near .jar file
```
bot.token=<bot token>
spring.data.mongodb.uri=mongodb://admin:password@localhost:9999/jenkinsbot?authSource=admin
```
2. `java -jar .\jenkinsBot-0.0.x.jar`

## TODO list

TODO in 0.0.5:


- -----
- Во время заполненная информации о сборке указывать что заполнено или что сейчас заполняется, а также пример заполнения
- ? do we need to change domain storage format to full link (include schema and port, e.g. `http://10.172.100.174:7331`)
- Deploy in Azure as ACI
- Event sourcing
- Database
  - (daily) mongo database backups
  - database fallback to local datasource if mongo is unavailable
- Unit Tests
  - ? Optimize database-related unit tests by closing connection afterall, not aftereach 
- l10n
- i18n
- webhooks
- ? menu/actions list as keyboard
- Obfuscate
- /stop and /restart commands ?
- Common Project files: CONTRIBUTING.md, SUPPORT.md, ACKNOWLEDGMENTS, CONTRIBUTORS
