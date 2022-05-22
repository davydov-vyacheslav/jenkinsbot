
## Developer Info

* Regular build: `./gradlew bootJar`
* Release build (with push to docker.io): `./gradlew release`
* Run local bootJar build
    * `java -jar .\jenkinsBot-$version.jar --bot.token=<bot token> --spring.data.mongodb.uri=mongodb://admin:password@localhost:9999/jenkinsbot?authSource=admin`
* Run build (from docker):
    * `docker pull davs87/jenkinsbot:<version>`
    * ```sh
      docker run -d \
      -e BOT_TOKEN=<bottoken> \
      -e SPRING_DATA_MONGODB_URI=mongodb://admin:password@localhost:9999/jenkinsbot?authSource=admin \
      --name=jenkinsBot-<version> \
      --restart unless-stopped \
      jenkinsbot:<version>
      ```
* Restart Docker
    * TBD
* WatchTower configuration
    * TBD
    * FIXME: what to do with non-latest versions like 0.0.5, 0.0.6

## Predecessor for project configuration

### Data Prerequisites

In order to make the system minimum viable, you need to add at least one (default) ConsoleOutputType which represents 
configuration of Jenkins logs parsing. 'default' configuration is mandatory. You may set up other configuration 
(and apply them to buildInfo) later. Samples are provided in `004.add_jenkins_console_output_types.sql`. 

Sample:

```mongodb-json-query
        db.ConsoleOutputConfig.insert({ 
        'name': 'default',
        'failedTestPattern': '.*\[junit] TEST (.*Test) FAILED',
        'executedTestPattern': '.*\[junit] Tests run.*',
        'unitTestsResultFilepathPrefix': 'output/reports/TEST-',
        'fileEncoding': 'windows-1252'})
```

Where:
- `failedTestPattern` - pattern to search for failed tests
- `executedTestPattern` - pattern to search for executed tests
- `unitTestsResultFilepathPrefix` - unit tests result location prefix. E.g. all tests results located at `output/reports`
folder and starts with `TEST-` prefix in name
- `fileEncoding` - Jenkins consoleOutput encoding. Some builds (especially old ones) may use different encoding. It
isn't so easy to determine the encoding ourself, 

### Gradle projects configuration

By default, gradle output is not enough to get executes tests. Information about failed tests also meager. 
Recommended to add following configuration to your gradle project: 

```groovy
    test {
        afterTest { desc, result ->
            logger.quiet "Executing test ${desc.name} [${desc.className}] with result: ${result.resultType}"
        }
    }
```

## Unclassified TODOs

- Event sourcing
- Database
    - (daily) mongo database backups
    - database fallback to local datasource if mongo is unavailable / choose database type in config
- webhooks
- source code documentation
- test containers
- Common Project files: CONTRIBUTING.md, SUPPORT.md, ACKNOWLEDGMENTS, CONTRIBUTORS
- Unit Tests:
    - refactor unit tests to extract common methods
    - cover with unit tests constructable messages that are greyed
        - label.field.*.*
        - label.welcome.field.*.*
- there are should be: stage env, prod env, dev (xxx) env
- Document :latest processing (with watchtower). No way to verify and make docs
- Document stable release build processing
- ?? add docker-compose.yml file?
- ?? migrate docker registry to GH ?
- Parse errors for non-Java/junit projects. E.g. - get Database build errors (Oracle, based on ORA- messages)
- Execute Jenkins build
- auto-upgrade gradle: https://api.github.com/repos/gradle/gradle/releases/latest : (https://github.com/dependabot/dependabot-core/issues/2223)
- Jenkins' info and ETA to complete build
- i18n coverage and editor plugin
- In further need to add constraints for entities . Now it is impossible as
  - user may want to rename referenced entities
  - no uniqueness between reference name and entity name: `@CompoundIndex(name = "unique_idx", def = "{'repoName': 1, 'creatorId': 1}", unique = true)`

