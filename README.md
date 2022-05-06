# Insight Jenkins bot

Основная задача - получение статуса билдов от Дженкинса (состояние билда и кол-во упавших тестов).
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

## Flow: /healthcheck

TODO: description

## Developer Info

* Regular build: `./gradlew bootJar`
* Release build (with push to docker.io): `./gradlew release`
* Run local bootJar build
  * `java -jar .\jenkinsBot-$version.jar --bot.token=<bot token> --spring.data.mongodb.uri=mongodb://admin:password@localhost:9999/jenkinsbot?authSource=admin`
* Run build (from docker): 
  * `docker pull davs87/jenkinsbot:<version>`
  * `docker run -d \
    -e BOT_TOKEN=<bottoken> \
    -e SPRING_DATA_MONGODB_URI=mongodb://admin:password@localhost:9999/jenkinsbot?authSource=admin \
    --name=jenkinsBot-<version> \
    --restart unless-stopped \
    jenkinsbot:<version>`
* Restart Docker
  * TBD
* WatchTower configuration
  * TBD
  * FIXME: what to do with non-latest versions like 0.0.5, 0.0.6

## TODO list

TODO in 0.0.7:


- -----
- Moving to CI/CD
  - stage env, prod env
  - watchtower (or alternatives)
  - migrate system to db01
  - GitHub + GH Actions / Gitlab host
  - test startup from docker :)
  - add docker-compose.yml file?
  - make docker build delegation to CI/CD like GH/GL 
- Event sourcing
- Database
  - (daily) mongo database backups
  - database fallback to local datasource if mongo is unavailable / choose database type in config
- webhooks
- ? menu/actions list as keyboard
- Obfuscate
- Optimize database-related unit tests by having single configuration they are running within
- /stop and /restart commands ?
- source code documentation
- Common Project files: CONTRIBUTING.md, SUPPORT.md, ACKNOWLEDGMENTS, CONTRIBUTORS
- ?? spring.jmx.enabled=false
- screenshot / gif / video based description + features in new versions
- cleanup validators: 
  - add url validator, 
  - add unique validator
- Unit Tests: cover with unit tests constructable messages that are greyed
  - label.field.*.*
  - label.welcome.field.*.*
  - DirtiesContext for CommandTests
- Локализация.
  * добавить «меню» настройки с выбором языка. Список регламентируется классом LocaleType и файлами в resources/i18n
- Информирование про завершенность сборки:
  * Создать сущность подписка, где пользователь «подписан» на билды.
  * Создать меню подписка, где можно подписаться на собственные или публичные репозитории.
    * овнеры репозиториев автоматически подписываются без права отписки
  * Раз в 15 минут идёт проверка завершенности билда. Но только в том случае, если его предыдущий статус - в процессе.
  * Если билд завершился - Всем пользователям из подписки приходит уведомление. Обновляется информация про предыдущую сборку
  * Проблема?? Зы, получить состояние «в процессе» можно только когда какой-то из пользователей проверит его статус
  * проблема? Что, если пользователь хочет подписаться только на текущий билд, а не все билды этого "репозитория"
- ? do we need to change domain storage format to full link (include schema and port, e.g. `http://10.172.100.174:7331`)
  + migration
