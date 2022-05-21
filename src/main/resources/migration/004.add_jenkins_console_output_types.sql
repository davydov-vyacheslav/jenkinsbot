// Add Default values for processing the Jenkins output
db.createCollection('ConsoleOutputConfig');
db.ConsoleOutputConfig.insertMany(
  [{ 'name': 'default',
    'failedTestPattern': '.*\[junit] TEST (.*Test) FAILED',
    'executedTestPattern': '.*\[junit] Tests run.*',
    'unitTestsResultFilepathPrefix': 'output/reports/TEST-',
    'fileEncoding': 'windows-1252'},
  { 'name': 'gradle',
    'failedTestPattern': '.*Executing test .* \[(.*Test)] with result: FAILURE',
    'executedTestPattern': '.*Executing test .*Test] with result.*',
    'unitTestsResultFilepathPrefix': 'build/test-results/test/TEST-',
    'fileEncoding': 'UTF-8'}]
);
db.Endpoint.updateMany(
  { referencedByUsers : { $exists: false } },
  [{ $set : { "referencedByUsers" : [] }}]
);
db.BuildInfo.updateMany(
  { referencedByUsers : { $exists: false } },
  [{ $set : { "referencedByUsers" : [] }}]
);
db.Settings.update(
    {'key' : 'dbVersion'},
    { $set : {'value' : '004'}}
);