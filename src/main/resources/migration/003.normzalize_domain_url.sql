// Migration Jenkins's info: domain + jobName to full-qualified path like `http://domain.com:7331/job/Project`
use jenkinsbot;
db.BuildInfo.updateMany(
  {'jenkinsInfo.domain' : { $not: { $regex : /^http/ } }},
  [{ $set : { "jobUrl0006" : { $concat: [ "http://", "$jenkinsInfo.domain", ":7331/job/", "$jenkinsInfo.jobName" ]} }}]
);
db.Settings.update(
    {'key' : 'dbVersion'},
    { $set : {'value' : '003'}}
);