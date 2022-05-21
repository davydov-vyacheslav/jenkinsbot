# Jenkins bot

[![Build Status](https://github.com/davydov-vyacheslav/jenkinsbot/actions/workflows/build.yml/badge.svg)](https://github.com/davydov-vyacheslav/jenkinsbot/actions)

[![codecov](https://codecov.io/gh/davydov-vyacheslav/jenkinsbot/branch/master/graph/badge.svg?token=Ntc7Kn0qXz)](https://codecov.io/gh/davydov-vyacheslav/jenkinsbot)
[![TODOs](https://badgen.net/https/api.tickgit.com/badgen/github.com/davydov-vyacheslav/jenkinsbot/master)](https://www.tickgit.com/browse?repo=github.com/davydov-vyacheslav/jenkinsbot&branch=master)

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![versionjava](https://img.shields.io/badge/jdk-8-brightgreen.svg?logo=java)](https://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![Release](https://img.shields.io/github/v/release/davydov-vyacheslav/jenkinsbot.svg)](https://github.com/davydov-vyacheslav/jenkinsbot/releases/latest)

## Goals
- get Jenkins Build's status info (especially failed unit tests statistics)
- get endpoints health-check status

System is relying on Java/jUnit projects

NOTE: Development info / Info for developers and project prerequisites located [here](./Developers.info.md).

## Why do we need this bot
- Sometimes target system may locate within private network it isn't so easy to get access to (e.g., VM in AWS). Especially,
if there are no direct way to access it (you need to log in to AWS, then go to your remote workstation, then remotely from 
this remote workstation to another remote workstation (Yes, house that Jack built))
- Easy to control final status of your build
- Some Jenkins' projects may have huge console output (more than 10Mb). During the build it isn't so easy to get insight
what tests failed. This bot provides a list with failed tests and a link to particular test result in xml format
(where you can check details of the failure)

## Flows 
### Flow: /build

Allows you to monitor specific Jenkins Build for failed unit tests. 

Flows:
* `/build`: List of owned and public repositories with ability to get their status
* `/build my_list`: List of owned repositories with ability to create/edit/delete ones
* `/build status <buildInfo>`: Get status of specific build. If provided value is empty or invalid, you will be provided with
list of applicable ones (see `/build`)
* `/build add`: Create new build info
* `/build add_reference`. Add link to existing public repo
* `/build edit <buildInfo>`: Edit specific build info. If provided value is empty or invalid, you will be provided with 
list of applicable ones (see `/build my_list`)

Logic:
- Job Status logic is based on Jenkins final message: Finished: FAILURE, Finished: UNSTABLE, Finished: ABORTED, Finished: SUCCESS.
If no such final message - assume that progress is still in progress
- Approximate amount of tests is based on amount of tests (test files) processed in previous build
- Processed (Executed) test: Looking for the line matched regex, defined in `ConsoleOutputConfig.executedTestPattern` of current name (default - 'default') 
- Failed test: Looking for the line matched regex, defined in `ConsoleOutputConfig.failedTestPattern` of current name (default - 'default')
- Failed test location: Is aggregation to follow this pattern: `%jobUrl%/%unitTestsResultFilepathPrefix%%TestName%.xml`, where 
  - JobUrl - link to jenkins Job, 
  - unitTestsResultFilepathPrefix - prefix for tests like `build/test-results/test/TEST-`
  - TestName - full qualified test name like `com.package.DatabaseTest`

![Build Status](./doc/assets/build_status.png)
![Build Add](./doc/assets/build_add.png)


### Flow: /healthcheck

Allows you to monitor status of external resources: web-servers, microservice heath status, etc.

Flows:
* `/healthcheck`: get status check for all owned and public external resources
* `/healthcheck add`: add new resource
* `/healthcheck add_reference`: Add link to existing public resource
* `/healthcheck edit`: menu for add/edit/delete owned external resources

Logic bases on return code: 
* 2xx - OK (🟢)
* Server alive, but non 2xx code (e.g. 404) - UNSTABLE (🟡)
* Server isn't alive - DOWN (🔴)

![HealthCheck](./doc/assets/healthcheck.png)
