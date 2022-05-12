# What's new / Changelog

## 0.0.7
  - BuildInfoModelChange to contain full jobUrl: https://github.com/davydov-vyacheslav/jenkinsbot/issues/3
  - GitHub CI/CD improvements
## 0.0.6
  - added i18n
  - added build status icons
  - `/healthcheck`: all applicable endpoints are being triggered for their accessibility
  - saving LastMessageId (for each entity) for end user in database (useful when bot restarts)
  - added l10n support, based on UserInfoDto.locale value
  - During filling field value, there is now welcome message of what is requested (including sample if any)
  - added dockerization
## 0.0.5
  - Cli processor: get rid of external utilities (usage: curl)
  - added build completion status info
  - fixed Link representation issue for short-typed domain names by adding `.perceptive.cloud` during migration
  - Added Project files (https://github.com/kmindi/special-files-in-repository-root/blob/master/README.md):
    - CHANGELOG, AUTHORS, LICENSE
  - Remaster /history, /help; help should refer to other informative links
## 0.0.4
  - BotFather: added Bot icon
  - updated CliProcessor to process one logs single time; get rid of cmd utilities: cat, grep, head, wc
  - BuildRepos got icons:  🌎 (public) 🔒 (private)
  - migrate to Callback buttons (from inline query buttons; for list of actions; list of build repos)
  - Replace choosing build operation to regex
  - Added `/cancel` command to cancel any in-progressing command
  - Remastered Repository management (based on BotFather flow):
    - get rid of semi-infinite-state-machine during Add -> now everything on buttons
    - Added Edit repo
    - Remastered Add/Edit/Status/Delete actions/flows
    - Build Flow: updating existing messages, not add new only
## 0.0.3
  - Store repositories in database (mongodb) + added mongo unit tests
  - Added build {status, delete} command test
  - Remastered Add Build Flow to enter data without annoying suffix (`@botname /build add`)
  - Added Database Version Setting to perform Data Migration gracefully in future
  - Added `/start` command to show menu
  - `/help` command now shows content of README.md file
## 0.0.2
  - Ability to add/delete repo (in-memory)
  - Status output became more user-friendly
## 0.0.1
  - PoC of idea