# Jenkins bot

![Coverage](.github/badges/jacoco.svg)

Goals:
- get Jenkins Build's status info (especially failed unit tests statistics)
- get endpoints health-check status

System is relying on Java/jUnit projects

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
- screenshot / gif / video based description + features in new versions
  - /healthcheck
  - /buildinfo
- Optimize database-related unit tests by having single configuration they are running within
- source code documentation
- Validators: https://github.com/davydov-vyacheslav/jenkinsbot/issues/4
- Progressing moving to CI/CD: https://github.com/davydov-vyacheslav/jenkinsbot/issues/5

- -----

## TODO: unclassified

- Event sourcing
- Database
  - (daily) mongo database backups
  - database fallback to local datasource if mongo is unavailable / choose database type in config
- webhooks
- test containers
- Common Project files: CONTRIBUTING.md, SUPPORT.md, ACKNOWLEDGMENTS, CONTRIBUTORS
- Unit Tests: cover with unit tests constructable messages that are greyed
  - label.field.*.*
  - label.welcome.field.*.*
  - DirtiesContext for CommandTests